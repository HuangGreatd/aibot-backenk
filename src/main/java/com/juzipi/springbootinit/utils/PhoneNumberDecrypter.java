package com.juzipi.springbootinit.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @ClassName PhoneNumberDecrypter
 * @Description:
 * @Author: 橘子皮
 * @CreateDate: 2025/3/21 19:37
 */
public class PhoneNumberDecrypter {
    public static String decryptPhoneNumber(String sessionKey, String encryptedData, String iv) {
        try { // 将 Base64 编码的 sessionKey、encryptedData 和 iv 解码为字节数组
            byte[] keyBytes = Base64.getDecoder().decode(sessionKey);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] ivBytes = Base64.getDecoder().decode(iv);
            // 创建 AES 密钥和 IV 参数
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
            // 创建 Cipher 实例并初始化为解密模式
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            // 执行解密操作
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            // 将解密后的字节数组转换为字符串
            String decryptedData = new String(decryptedBytes, StandardCharsets.UTF_8);
            // 解析 JSON 数据获取手机号
            // 这里可以使用 JSON 解析库，如 Jackson 或 Gson
            // 示例代码中简单假设 JSON 数据格式为 {"phoneNumber": "13800138000"}
            int startIndex = decryptedData.indexOf("\"phoneNumber\":\"");

            if (startIndex != -1) {
                startIndex += "\"phoneNumber\":\"".length();
                int endIndex = decryptedData.indexOf("\"", startIndex);
                return decryptedData.substring(startIndex, endIndex);
            }
        } catch (Exception e) {
            System.err.println("解密失败: " + e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        String sessionKey = "your_session_key";
        String encryptedData = "your_encrypted_data";
        String iv = "your_iv";
        String phoneNumber = decryptPhoneNumber(sessionKey, encryptedData, iv);
        System.out.println("解密后的手机号: " + phoneNumber);
    }
}
