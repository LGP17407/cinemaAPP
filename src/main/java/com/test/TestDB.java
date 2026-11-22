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

public class TestDB {

	public static void main(String[] args) {
		DB_CONNECTION dbConnect = DB_CONNECTION.getInstance();
		dbConnect.createConnection();
		UserServices.signIn();
		//UserServices.signUp();
		//AdminServices.addMovies();
		//AdminServices.alterMoviesDuration();
		dbConnect.closeConnection();
	}

}
