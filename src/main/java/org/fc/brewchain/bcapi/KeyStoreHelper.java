package org.fc.brewchain.bcapi;

import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.brewchain.bcapi.gens.Oentity.KeyStoreValue;
import org.codehaus.jackson.map.ObjectMapper;
import org.fc.brewchain.bcapi.EncAPI;
import org.fc.brewchain.bcapi.KeyPairs;
import org.fc.brewchain.bcapi.KeyStoreFile.CipherParams;
import org.fc.brewchain.bcapi.KeyStoreFile.KeyStoreParams;

import com.fasterxml.jackson.core.JsonParseException;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import onight.osgi.annotation.NActorProvider;
import onight.tfw.ntrans.api.ActorService;
import onight.tfw.ntrans.api.annotation.ActorRequire;

@NActorProvider
@Instantiate(name = "KeyStore_Helper")
@Provides(specifications = { ActorService.class }, strategy = "SINGLETON")
@Slf4j
@Data
public class KeyStoreHelper implements ActorService {
	@ActorRequire(name = "bc_encoder", scope = "global")
	EncAPI encApi;

	public KeyStoreValue getKeyStore(String keyStoreText, String pwd) {
		// verify the pwd
		KeyStoreFile oKeyStoreFile = parse(keyStoreText);
		if (!oKeyStoreFile.getPwd().equals(encApi.hexEnc(encApi.sha3Encode(pwd.getBytes())))) {
			log.error("pwd is wrong");
		}
		// get cryptoKey
		byte[] cryptoKey = getCryptoKey(oKeyStoreFile, pwd);
		try {
			KeyStoreValue oKeyStoreValue = KeyStoreValue
					.parseFrom(decrypt(encApi.hexDec(oKeyStoreFile.getCipherText()), cryptoKey));
			return oKeyStoreValue;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public KeyStoreFile generate(KeyPairs oKeyPairs, String pwd) {
		return generate(oKeyPairs.getAddress(), oKeyPairs.getPrikey(), oKeyPairs.getPubkey(), oKeyPairs.getBcuid(),
				pwd);
	}

	public KeyStoreFile generate(String address, String privKey, String pubKey, String bcuid, String pwd) {
		KeyStoreFile oKeyStoreFile = new KeyStoreFile();
		oKeyStoreFile.setKsType("pbkdf2");
		KeyStoreParams oKeyStoreParams = oKeyStoreFile.new KeyStoreParams();
		oKeyStoreParams.setSalt(IDGenerator.getInstance().generate());
		oKeyStoreParams.setC(16);
		oKeyStoreParams.setDklen(32);
		oKeyStoreFile.setParams(oKeyStoreParams);
		oKeyStoreFile.setPwd(encApi.hexEnc(encApi.sha3Encode(pwd.getBytes())));
		byte[] cryptoKey = getCryptoKey(oKeyStoreFile, pwd);
		oKeyStoreFile.setCipher("aes-128-ctr");
		String iv = IDGenerator.getInstance().generate();
		CipherParams oCipherParams = oKeyStoreFile.new CipherParams();
		oCipherParams.setIv(iv);
		oKeyStoreFile.setCipherParams(oCipherParams);

		KeyStoreValue.Builder oKeyStoreValue = KeyStoreValue.newBuilder();
		oKeyStoreValue.setAddress(address);
		oKeyStoreValue.setBcuid(bcuid);
		oKeyStoreFile.setCipherText(
				encApi.hexEnc(encrypt(oKeyStoreValue.build().toByteArray(), cryptoKey, encApi.hexDec(iv))));

		return oKeyStoreFile;
	}

	private byte[] encrypt(byte[] srcData, byte[] seed, byte[] iv) {
		try {
			Cipher cipher = Cipher.getInstance("AES");

			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(seed);
			kgen.init(128, secureRandom);
			SecretKey secretKey = kgen.generateKey();
			SecretKeySpec oSecretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

			cipher.init(Cipher.ENCRYPT_MODE, oSecretKeySpec);
			return cipher.doFinal(srcData);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static byte[] decrypt(byte[] encData, byte[] seed) throws Exception {
		try {
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器

			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(seed);
			kgen.init(128, secureRandom);
			SecretKey secretKey = kgen.generateKey();
			SecretKeySpec oSecretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

			cipher.init(Cipher.DECRYPT_MODE, oSecretKeySpec);// 初始化
			return cipher.doFinal(encData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private byte[] getCryptoKey(KeyStoreFile ksFile, String pwd) {
		if (ksFile.getKsType().equals("pbkdf2")) {
			SecretKeyFactory f;
			try {
				f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
				KeySpec ks = new PBEKeySpec(pwd.toCharArray(), ksFile.getParams().getSalt().getBytes(),
						ksFile.getParams().getC(), ksFile.getParams().getDklen());
				SecretKey s = f.generateSecret(ks);
				Key k = new SecretKeySpec(s.getEncoded(), "AES");
				return k.getEncoded();
			} catch (NoSuchAlgorithmException e) {
				log.error(e.getMessage());
			} catch (InvalidKeySpecException e) {
				log.error(e.getMessage());
			}
		}
		log.error("the keystore type is wrong::" + ksFile.getKsType());
		return null;
	}

	public KeyStoreFile parse(String jsonText) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(jsonText, KeyStoreFile.class);
		} catch (Exception e) {
			log.error("keystore json parse error::" + e.getMessage());
		}
		return null;
	}

	public String parseToJsonStr(KeyStoreFile oKeyStoreFile) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(oKeyStoreFile);
		} catch (Exception e) {
			log.error("generate keystore text error::" + e.getMessage());
		}
		return null;
	}
}
