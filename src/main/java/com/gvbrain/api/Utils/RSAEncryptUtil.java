package com.gvbrain.api.Utils;

import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static com.alibaba.fastjson.util.IOUtils.UTF8;

public class RSAEncryptUtil {

    public static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCsD1gI70BxYujhNw8NpaVKRXkcRofoeUbN9Dj5m3i3h9XAIS6LkjI01L4ieRpTHnMEzoXUY8a2/svDf//xuHuDJlZBNtCXK4DPx5x4zHdUWDjFGpWlMQzhsqQlfs0tkN5gP095g27L0ki/NrRuBpgxP1q2dHKpL37sBF8XNRpedwIDAQAB";

    //RSA加密方法
    public static String encrypt(String source) throws Exception {
        PublicKey key = getPublicKey(PUBLIC_KEY);
        Cipher cipher = Cipher.getInstance("RSA");//java默认"RSA"="RSA/ECB/PKCS1Padding"
        cipher.init(Cipher.ENCRYPT_MODE, key);
        BASE64Encoder encoder = new BASE64Encoder();
        byte[] b = source.getBytes(UTF8);
        return encoder.encode(cipher.doFinal(b));
    }

    //将base64编码后的公钥字符串转成PublicKey实例
    public static PublicKey getPublicKey(String publicKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
/*
    public static void main (String[]args) throws Exception {
        PublicKey key = getPublicKey(PUBLIC_KEY);
        System.out.println(key);
        System.out.println(encrypt(source));
    }*/

}
