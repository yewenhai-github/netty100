package com.netty100.common.crypto;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author why
 * @version 1.0.0, 2023/1/6
 * @since 1.0.0, 2023/1/6
 */
@Slf4j
public class WhyRsaUtils {
    // 加密算法
    private final static String ALGORITHM_RSA = "RSA";

    /**
     * 直接生成公钥、私钥对象
     *
     * @param modulus
     *
     * @throws NoSuchAlgorithmException
     *
     */
    private static List<Key> getRSAKeyObject(int modulus) throws NoSuchAlgorithmException{
        List<Key> keyList = new ArrayList<>(2);
        // 创建RSA密钥生成器
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM_RSA);
        // 设置密钥的大小，此处是RSA算法的模长 = 最大加密数据的大小
        keyPairGen.initialize(modulus);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // keyPair.getPublic() 生成的是RSAPublic的是咧
        keyList.add(keyPair.getPublic());
        // keyPair.getPrivate() 生成的是RSAPrivateKey的实例
        keyList.add(keyPair.getPrivate());
        return keyList;
    }

    /**
     * 生成公钥、私钥的字符串
     * 方便传输
     *
     * @param modulus 模长
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static List<String> getRSAKeyString(int modulus) throws NoSuchAlgorithmException{
        List<String> keyList = new ArrayList<>(2);
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM_RSA);
        keyPairGen.initialize(modulus);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        keyList.add(publicKey);
        keyList.add(privateKey);
        return keyList;
    }


    /**
     * 公钥加密
     *
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String data, String publicKey) {
        try {
            RSAPublicKey rsaPublicKey = getPublicKey(publicKey);

            Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
            cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
            // 模长n转换成字节数
            int modulusSize = rsaPublicKey.getModulus().bitLength() / 8;
            // PKCS Padding长度为11字节，所以实际要加密的数据不能要 - 11byte
            int maxSingleSize = modulusSize - 11;
            // 切分字节数组，每段不大于maxSingleSize
            byte[][] dataArray = splitArray(data.getBytes(), maxSingleSize);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // 分组加密，并将加密后的内容写入输出字节流
            for (byte[] s : dataArray) {
                out.write(cipher.doFinal(s));
            }
            // 使用Base64将字节数组转换String类型
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            log.error("加密失败", e);
        }
        return data;
    }

    /**
     * 私钥解密
     *
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String data, String privateKey) {
        try {
            RSAPrivateKey rsaPrivateKey = getPrivateKey(privateKey);

            Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
            cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
            // RSA加密算法的模长 n
            int modulusSize = rsaPrivateKey.getModulus().bitLength() / 8;
            byte[] dataBytes = data.getBytes();
            // 之前加密的时候做了转码，此处需要使用Base64进行解码
            byte[] decodeData = Base64.getDecoder().decode(dataBytes);
            // 切分字节数组，每段不大于modulusSize
            byte[][] splitArrays = splitArray(decodeData, modulusSize);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for(byte[] arr : splitArrays){
                out.write(cipher.doFinal(arr));
            }
            return new String(out.toByteArray());
        } catch (NoSuchAlgorithmException e) {
            log.error("解密失败", e);
        } catch (NoSuchPaddingException e) {
            log.error("解密失败", e);
        } catch (InvalidKeyException e) {
            log.error("解密失败", e);
        } catch (IOException e) {
            log.error("解密失败", e);
        } catch (IllegalBlockSizeException e) {
            log.error("解密失败", e);
        } catch (BadPaddingException e) {
            log.error("解密失败", e);
        } catch (Exception e) {
            log.error("解密失败", e);
        }
        return null;
    }

    // Java中RSAPublicKeySpec、X509EncodedKeySpec支持生成RSA公钥
    // 此处使用X509EncodedKeySpec生成
    private static RSAPublicKey getPublicKey(String publicKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return (RSAPublicKey) keyFactory.generatePublic(spec);
    }

    // Java中只有RSAPrivateKeySpec、PKCS8EncodedKeySpec支持生成RSA私钥
    // 此处使用PKCS8EncodedKeySpec生成
    private static RSAPrivateKey getPrivateKey(String privateKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return (RSAPrivateKey) keyFactory.generatePrivate(spec);
    }


    /**
     * 按指定长度切分数组
     *
     * @param data
     * @param len 单个字节数组长度
     * @return
     */
    private static byte[][] splitArray(byte[] data,int len){

        int dataLen = data.length;
        if (dataLen <= len) {
            return new byte[][]{data};
        }
        byte[][] result = new byte[(dataLen-1)/len + 1][];
        int resultLen = result.length;
        for (int i = 0; i < resultLen; i++) {
            if (i == resultLen - 1) {
                int slen = dataLen - len * i;
                byte[] single = new byte[slen];
                System.arraycopy(data, len * i, single, 0, slen);
                result[i] = single;
                break;
            }
            byte[] single = new byte[len];
            System.arraycopy(data, len * i, single, 0, len);
            result[i] = single;
        }
        return result;
    }


    public static void main(String[] args) throws Exception {
        // 使用公钥、私钥对象加解密
        long start = System.currentTimeMillis();
        // 使用字符串生成公钥、私钥完成加解密
        List<String> keyStringList = getRSAKeyString(1024);
        String pukString = keyStringList.get(0);
        String prkString = keyStringList.get(1);
        System.out.println("公钥:" + pukString);
        System.out.println("私钥:" + prkString);

        String message = "666666666666666666666666666666";
        for (int i = 0; i < 10; i++) {
            String encryptedMsg = encryptByPublicKey(message, pukString);
            String decryptedMsg = decryptByPrivateKey(encryptedMsg, prkString);
//            System.out.println(encryptedMsg+"  "+ decryptedMsg);
//            System.out.println("string key ! message ==  decryptedMsg ? " + message.equals(decryptedMsg));
        }
        System.out.println("-=------------------cost time2 ----------------- " + (System.currentTimeMillis() - start));
    }

}
