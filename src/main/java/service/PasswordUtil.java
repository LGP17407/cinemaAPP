package service;

import java.nio.charset.StandardCharsets;		//Chỉ định bảng mã khi đổi chuỗi sang byte
import java.security.MessageDigest;				//thư viện dùng để băm(SHA-256)
import java.security.NoSuchAlgorithmException;	//thư viện xử lý lỗi
import java.util.HexFormat;						//Đổi mảng byte thành chuỗi hex (ví dụ a3f1...) để lưu vào cột VARCHAR trong MySQL.

public final class PasswordUtil {

    public static String hash(String password) { //Hàm băm
        try {
        	
            byte[] bytes =  MessageDigest.getInstance("SHA-256").digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);   // chuỗi 64 ký tự
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verify(String password, String storedHash) { //Hàm kiểm tra
        return hash(password).equals(storedHash);
    }
}