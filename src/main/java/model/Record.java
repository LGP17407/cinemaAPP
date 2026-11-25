package model;

import java.time.LocalDateTime;

public class Record {
	public record userRecord(int userId, String fullName, String email, String role, LocalDateTime createdAt ) {}
	public record ticketsRecord(int userId, String fullName, String email, String role, LocalDateTime createdAt ) {}
	public record showTimes(int showtimeId,
							int movieId, 
							int roomId, 
							LocalDateTime startTIme, 
							LocalDateTime endTime, 
							double basePrice,
							boolean isDeleted,
							LocalDateTime createdAt) {}
	public record seats(int seatId, int roomId, String seatRow, int seatNumber, String seatType, String status) {}
	public record rooms(int roomID, String roomName, int totalSeats, LocalDateTime createdAt) {}
	public record movies(int movieId, 
						 String title, 
						 String description, 
						 int durationMins, 
						 String poster_url,
						 String status, 						
						 LocalDateTime createdAt) {}
}
