package model;

import java.time.LocalDateTime;

public class Record {
	public record userRecord(int userId, String fullName, String email, String role, LocalDateTime createdAt ) {}
	public record ticketsRecord(int userId, String fullName, String email, String role, LocalDateTime createdAt ) {}
	
}
