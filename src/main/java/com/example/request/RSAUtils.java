package com.example.request;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * RSA加密解密工具类
 */
public class RSAUtils {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 生成密钥对
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    /**
     * 使用公钥加密数据
     * @param plainText
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String plainText, PublicKey publicKey) throws Exception {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] cipherText = encryptCipher.doFinal(plainText.getBytes(UTF_8));
        return Base64.getEncoder().encodeToString(cipherText);
    }

    /**
     * 使用私钥解密数据
     * @param cipherText
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String cipherText, PrivateKey privateKey) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(cipherText);
        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(decryptCipher.doFinal(bytes), UTF_8);
    }

    public static PublicKey getPublicKey(String base64EncodedPublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 解码Base64编码的公钥字符串
        byte[] encoded = Base64.getDecoder().decode(base64EncodedPublicKey);

        // 创建X509EncodedKeySpec对象
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);

        // 获取KeyFactory实例
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // 生成PublicKey对象
        return keyFactory.generatePublic(keySpec);
    }
}
