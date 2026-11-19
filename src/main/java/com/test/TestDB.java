package com.test;

import db.DB_CONNECTION;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDB {

	public static void main(String[] args) {
		DB_CONNECTION dbConnect = DB_CONNECTION.getInstance();
		dbConnect.createConnection();
		
		try {
		PreparedStatement pSt = null;
		ResultSet rs = null;
			
		pSt = dbConnect.getConnection().prepareStatement("select * from users");
		rs = pSt.executeQuery();
		int cols = rs.getMetaData().getColumnCount();   // số cột của bảng
		while (rs.next()) {                             // duyệt từng dòng
		    for (int i = 1; i <= cols; i++) {           // duyệt từng cột (đếm từ 1)
		    	if( i == 1)
		    		System.out.print(rs.getString(i));
		    	else
		    		System.out.print("\t\t" + rs.getString(i));
		    }
		    System.out.println();                       // xuống dòng sau mỗi dòng dữ liệu
		}
		
		
		}
		catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		}
		
		
		dbConnect.closeConnection();
	}

}
