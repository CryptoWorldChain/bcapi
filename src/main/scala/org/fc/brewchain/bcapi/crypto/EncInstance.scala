package org.fc.brewchain.bcapi.crypto

import onight.osgi.annotation.NActorProvider
import org.apache.felix.ipojo.annotations.Instantiate
import onight.tfw.ntrans.api.ActorService
import org.fc.brewchain.bcapi.EncAPI
import org.apache.felix.ipojo.annotations.Provides
import onight.oapi.scala.commons.SessionModules
import onight.oapi.scala.commons.PBUtils
import onight.oapi.scala.traits.OLog
import com.google.protobuf.Message
import org.apache.felix.ipojo.annotations.ServiceProperty
import scala.beans.BeanProperty
import org.fc.brewchain.bcapi.KeyPairs
import org.brewchain.core.crypto.ECKey
import org.spongycastle.util.encoders.Hex
import java.security.SecureRandom
import org.brewchain.core.crypto.ECIESCoder
import org.brewchain.core.crypto.jce.ECSignatureFactory
import org.brewchain.core.crypto.jce.SpongyCastleProvider
import org.brewchain.core.crypto.jce.ECKeyFactory
import org.spongycastle.jce.spec.ECPrivateKeySpec
import org.apache.commons.codec.binary.Base64


@NActorProvider
@Instantiate(name = "bc_encoder")
@Provides(specifications = Array(classOf[ActorService], classOf[EncAPI]), strategy = "SINGLETON")
object EncInstance extends SessionModules[Message] with PBUtils with OLog with EncAPI with ActorService {

  @ServiceProperty(name = "name")
  @BeanProperty
  var name: String = "bc_encoder";

  override def getModule: String = "BIP"
  override def getCmds: Array[String] = Array("ENC");
  def genKeys(): KeyPairs = {
    val ran = new SecureRandom();
    //ran.generateSeed(System.currentTimeMillis().asInstanceOf[Int])
    val eckey = new ECKey(ran);
    val pubstr = Hex.toHexString(eckey.getPubKey);
    val kp = new KeyPairs(
      Hex.toHexString(eckey.getPubKey),
      Hex.toHexString(eckey.getPrivKeyBytes),
      Hex.toHexString(eckey.getAddress),
      BCNodeHelper.nextUID(pubstr));
    return kp
  };

  def ecEncode(pubKey: String, content: Array[Byte]): Array[Byte] = {
    val eckey = ECKey.fromPublicOnly(Hex.decode(pubKey));
    val encBytes = ECIESCoder.encrypt(eckey.getPubKeyPoint, content);
    encBytes
  }

  def ecDecode(priKey: String, content: Array[Byte]): Array[Byte] = {
    val eckey = ECKey.fromPrivate(Hex.decode(priKey));
    val orgBytes = ECIESCoder.decrypt(eckey.getPrivKey, content);
    orgBytes;
  }

  def ecSign(priKey: String, contentHash: Array[Byte]): Array[Byte] = {
    val eckey = ECKey.fromPrivate(Hex.decode(priKey));
    val ecSig = ECSignatureFactory.getRawInstance(SpongyCastleProvider.getInstance());
    val prikey = ECKeyFactory
      .getInstance(SpongyCastleProvider.getInstance())
      .generatePrivate(new ECPrivateKeySpec(eckey.getPrivKey, ECKey.CURVE_SPEC));
    ecSig.initSign(prikey);
    ecSig.update(contentHash);
    ecSig.sign();
  }

  def ecVerify(priKey: String, contentHash: Array[Byte], sign: Array[Byte]): Boolean = {
    true
  }

  def base64Enc(data: Array[Byte]): String = {
    Base64.encodeBase64String(data);
  }

  def base64Dec(str: String): Array[Byte] = {
    Base64.decodeBase64(str);
  }

  def hexEnc(data: Array[Byte]): String = {
    Hex.toHexString(data);
  }

  def hexDec(str: String): Array[Byte] = {
    Hex.decode(str)
  }

  def ecSignHex(priKey: String, hexHash: String): String = {
    hexEnc(ecSign(priKey, hexHash.getBytes))
  }

  def ecSignHex(priKey: String, hexHash: Array[Byte]): String = {
    hexEnc(ecSign(priKey, hexHash))
  }

  def ecVerifyHex(priKey: String, hexHash: String, signhex: String): Boolean = {
    ecVerify(priKey, hexDec(hexHash), hexDec(signhex));
  }
  def ecVerifyHex(priKey: String, hexHash: Array[Byte], signhex: String): Boolean = {
    ecVerify(priKey, hexHash, hexDec(signhex));
  }

}