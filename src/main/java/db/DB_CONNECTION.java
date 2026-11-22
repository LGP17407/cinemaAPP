package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB_CONNECTION {
	// 0. các biến để kết nối tới database
	String url = "jdbc:mysql://localhost:3306/new_cinema_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh";
	String user = "root";
	String password = "@Sigma2006.sql";

	// 1. Biến instance duy nhất của class này
	private static DB_CONNECTION instance;

	// 2. Biến chứa đường ống kết nối
	private Connection connection;

	// 3. Constructor để private, ngăn không cho dùng lệnh 'new DB_CONNECTION()' ở
	// nơi khác
	private DB_CONNECTION() {

	}
	
	public Connection getConnection() {
		return this.connection;
	}

	// 4. Phương thức để các class khác (như TestDB) gọi ra để dùng
	public static DB_CONNECTION getInstance() {
		if (instance == null) {
			instance = new DB_CONNECTION(); // Chỉ tạo mới 1 lần duy nhất
		}
		return instance;
	}

	// 5. Phương thức để lấy "đường ống" ra truy vấn dữ liệu
	public Connection createConnection() {
		try {
			if (connection == null || !connection.isValid(2)) {
				// Mở kết nối và lưu vào biến connection
				this.connection = DriverManager.getConnection(url, user, password);
				System.out.println("Kết nối MySQL thành công!");
			}
		} catch (SQLException ex) {
			System.out.println("Lỗi kết nối: " + ex.getMessage());
		}
		return connection;
	}

	// 6. ngắt kết nối
	public void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
				System.out.println("Đã đóng kế nối");
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			connection = null;
			instance = null; // để lần getInstance sau mở lại được
		}
	}
}