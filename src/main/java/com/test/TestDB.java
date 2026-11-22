package com.test;


import db.DB_CONNECTION;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import service.*; 


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import model.ModelTickets;

public class TestDB {

	public static void main(String[] args) {
		DB_CONNECTION dbConnect = DB_CONNECTION.getInstance();
		dbConnect.createConnection();
		UserServices.signIn();
		//UserServices.signUp();
		//AdminServices.addMovies();
		//AdminServices.alterMoviesDuration();
		
		  BookingService bookingService = new BookingService();
		dbConnect.closeConnection();


	        try {
	            // Gọi hàm đặt vé, nhập dữ liệu trong BookingService
	            List<ModelTickets> tickets =
	                    bookingService.bookTickets();

	            System.out.println("\n===== KET QUA DAT VE =====");
	            System.out.println("Dat ve thanh cong!");
	            System.out.println("So ve da dat: " + tickets.size());

	        } catch (SQLException e) {
	            System.out.println("Loi database: " + e.getMessage());

	        } catch (Exception e) {
	            System.out.println("Dat ve that bai: " + e.getMessage());
	        }
	    }
		
		/// booking
}
