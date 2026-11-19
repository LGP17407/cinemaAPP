package service;

import db.DB_CONNECTION;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import model.*;
import java.util.ArrayList;


public class AdminServices {
	private Connection conn;
	
	public AdminServices () {
		conn = DB_CONNECTION.getInstance().createConnection();
	}
	
	//1. lấy thông tin user
	public ArrayList<ModelUser> getUserList () throws SQLException {
		ArrayList<ModelUser> userList = new ArrayList();
		String sql = "SELECT userid, fullname, email, password, role, DATE_FORMAT(createdat, '%d-%m-%Y') as ngay_tao from users";
		PreparedStatement pSt = null;
		ResultSet rs = null;
		
		pSt = conn.prepareStatement(sql);
		rs = pSt.executeQuery();
		
		while (rs.next()) {
            int userId = rs.getInt(1);
            String fullName = rs.getString(2);
            String email = rs.getString(3);
            String password = rs.getString(4);
            String role = rs.getString(5);
            String createdAt = rs.getString(6);
            ModelUser data = new ModelUser(userId, fullName, email, password, role, createdAt);
            userList.add(data);
        }
		
		return userList;
	}
}
