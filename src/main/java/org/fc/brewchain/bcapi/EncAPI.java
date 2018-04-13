package org.fc.brewchain.bcapi;

public interface EncAPI {
	public KeyPairs genKeys();

	public byte[] ecEncode(String pubKey, byte[] content);

	public byte[] ecDecode(String priKey, byte[] content);

	public byte[] ecSign(String priKey, byte[] contentHash);

	public String ecSignHex(String priKey, byte[] contentHash);

	public boolean ecVerify(String priKey, byte[] contentHash, byte[] sign);

	public String ecSignHex(String priKey, String hexHash);

	public boolean ecVerifyHex(String priKey, String hexHash, String signhex);

	public boolean ecVerifyHex(String priKey, byte[] hexHash, String signhex);

	public String base64Enc(byte[] data);

	public byte[] base64Dec(String data);

	public String hexEnc(byte[] data);

	public byte[] hexDec(String data);
	
	public byte[] ecToAddress(byte[] contentHash, byte[] r,byte[] s,byte v);
	public byte[] ecToAddress(byte[] contentHash, String signBase64);
	
	public byte[] ecToKeyBytes(byte[] contentHash, byte[] r,byte[] s,byte v);
	public byte[] ecToKeyBytes(byte[] contentHash, String signBase64);
	
	public boolean ecVerify(String pubKey, String content, byte[] r,byte[] s,  byte v);

	public byte[] sha3Encode(byte[] input);
}
