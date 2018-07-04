package org.fc.brewchain.bcapi;

import java.util.List;

public interface EncAPI {
	
//	/**
//	 * 创建BTC地址
//	 * @return
//	 */
//	public KeyPairs genBTCKeys();
	
	/**
	 * 创建IOTA地址
     * Generates a new address from a seed and returns the remainderAddress.
     * This is either done deterministically, or by providing the index of the new remainderAddress.
	 * @param seed 种子（IOTA Tryte-encoded Seed）规则:大于60个字符，小于等于81个字符，字符只能出现A～Z和数字9
     * @param security  Security level to be used for the private key / address. Can be 1, 2 or 3.
     * @param index     Key index to start search from. If the index is provided, the generation of the address is not deterministic.
     * @param checksum  Adds 9-tryte address checksum.
     * @param total     Total number of addresses to generate.
     * @param returnAll If <code>true</code>, it returns all addresses which were deterministically generated (until findTransactions returns null).
     * @return An array of strings with the specifed number of addresses.
	 * @return
	 */
	public List<String> genIOTAKeys(final String seed, int security, final int index, final boolean checksum, final int total, final boolean returnAll);
	
	/**
	 * 创建地址
	 * @return
	 */
	public KeyPairs genKeys();
	
	/**
	 * 创建地址
	 * @param seed 助记词字符串
	 * @return
	 */
	public KeyPairs genKeys(String seed);

//	public byte[] ecEncode(String pubKey, byte[] content);
//
//	public byte[] ecDecode(String priKey, byte[] content);

	/**
	 * 交易内容签名
	 * @param priKey 私钥
	 * @param contentHash 交易内容哈希
	 * @return
	 */
	public byte[] ecSign(String priKey, byte[] contentHash);

	/**
	 * 交易内容签名
	 * @param priKey 私钥
	 * @param contentHash 交易内容哈希
	 * @return
	 */
	public String ecSignHex(String priKey, byte[] contentHash);

	/**
	 * 交易内容签名
	 * @param priKey 私钥
	 * @param hexHash 交易内容哈希
	 * @return
	 */
	public String ecSignHex(String priKey, String hexHash);

	/**
	 * 交易验签
	 * @param priKey 私钥
	 * @param contentHash 交易内容哈希
	 * @param sign 交易内容签名
	 * @return
	 */
	public boolean ecVerify(String pubKey, byte[] contentHash, byte[] sign);

	/**
	 * 交易验签
	 * @param priKey 私钥
	 * @param hexHash 交易内容哈希
	 * @param signhex 交易内容签名
	 * @return
	 */
	public boolean ecVerifyHex(String pubKey, String hexHash, String signhex);

	/**
	 * 交易验签
	 * @param priKey 私钥
	 * @param hexHash 交易内容哈希
	 * @param signhex 交易内容签名
	 * @return
	 */
	public boolean ecVerifyHex(String pubKey, byte[] hexHash, String signhex);

	/**
	 * byte[] 到 Base64 String
	 * @param data
	 * @return
	 */
	public String base64Enc(byte[] data);

	/**
	 * Base64 String 到 byte[]
	 * @param data
	 * @return
	 */
	public byte[] base64Dec(String data);

	/**
	 * byte[] 到 String
	 * @param data
	 * @return
	 */
	public String hexEnc(byte[] data);

	/**
	 * String 到 byte[]
	 * @param data
	 * @return
	 */
	public byte[] hexDec(String data);
	
	/**
	 * 通过签名获取地址
	 * @param contentHash 签名之前的数据
	 * @param sign 签名之后的数据
	 * @return
	 */
	public byte[] ecToAddress(byte[] contentHash, String sign);
	
	/**
	 * 通过签名获取公钥
	 * @param contentHash 签名之前的数据
	 * @param sign 签名之后的数据
	 * @return
	 */
	public byte[] ecToKeyBytes(byte[] contentHash, String sign);

	/**
	 * 根据私钥计算地址
	 * @param priKey
	 * @return
	 */
	public String priKeyToAddress(String priKey);
	
	/**
	 * 根据私钥计算公钥
	 * @param priKey
	 * @return
	 */
	public String priKeyToPubKey(String priKey);
	
	/**
	 * 根据私钥计算地址、公钥
	 * @param priKey
	 * @return
	 */
	public KeyPairs priKeyToKey(String priKey);
	
	/**
	 * byte[] 到 sha3 byte[]
	 * @param input
	 * @return
	 */
	public byte[] sha3Encode(byte[] input);
	
	/**
	 * byte[] 到 sha256 byte[]
	 * @param input
	 * @return
	 */
	public byte[] sha256Encode(byte[] input);
	
}
