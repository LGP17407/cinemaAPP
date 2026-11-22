-- =====================================================================
-- final_cinema_management.sql
-- Yêu cầu: MySQL 8.0.16 trở lên (để các ràng buộc CHECK có hiệu lực)
-- Thứ tự: tạo database -> xóa bảng cũ -> tạo bảng -> thêm dữ liệu mẫu
-- CẢNH BÁO: phần DROP TABLE sẽ XÓA toàn bộ dữ liệu cũ của các bảng này.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS final_cinema_management
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE final_cinema_management;

-- ---------------------------------------------------------------------
-- 0. Xóa bảng cũ (theo thứ tự ngược của khóa ngoại) để chạy lại được nhiều lần
-- ---------------------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS Tickets;
DROP TABLE IF EXISTS Showtimes;
DROP TABLE IF EXISTS Seats;
DROP TABLE IF EXISTS Rooms;
DROP TABLE IF EXISTS Movies;
DROP TABLE IF EXISTS Users;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
-- 1. Bảng Users (tài khoản khách hàng & admin)
-- ---------------------------------------------------------------------
CREATE TABLE Users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    full_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,      -- UNIQUE đã tự tạo index
    password_hash VARCHAR(255) NOT NULL,
    role          ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 2. Bảng Movies (danh sách phim)
-- ---------------------------------------------------------------------
CREATE TABLE Movies (
    movie_id      INT AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(255) NOT NULL,
    description   TEXT NULL,
    duration_mins INT NOT NULL,
    poster_url    VARCHAR(500) NULL,
    status        ENUM('UPCOMING', 'SHOWING', 'STOPPED') NOT NULL DEFAULT 'SHOWING',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_movies_duration CHECK (duration_mins > 0),
    INDEX idx_movies_status (status)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 3. Bảng Rooms (phòng chiếu)
-- ---------------------------------------------------------------------
CREATE TABLE Rooms (
    room_id     INT AUTO_INCREMENT PRIMARY KEY,
    room_name   VARCHAR(50) NOT NULL UNIQUE,
    total_seats INT NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_rooms_total_seats CHECK (total_seats > 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 4. Bảng Seats (sơ đồ ghế cố định của từng phòng)
-- ---------------------------------------------------------------------
CREATE TABLE Seats (
    seat_id     INT AUTO_INCREMENT PRIMARY KEY,
    room_id     INT NOT NULL,
    seat_row    VARCHAR(5) NOT NULL,
    seat_number INT NOT NULL,
    seat_type   ENUM('NORMAL', 'VIP') NOT NULL DEFAULT 'NORMAL',
    status      ENUM('AVAILABLE', 'UNAVAILABLE', 'MAINTENANCE') NOT NULL DEFAULT 'AVAILABLE',

    CONSTRAINT fk_seats_room
        FOREIGN KEY (room_id) REFERENCES Rooms(room_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    -- Chống trùng vị trí ghế trong cùng 1 phòng (tự tạo index cho room_id)
    CONSTRAINT uq_room_seat UNIQUE (room_id, seat_row, seat_number),
    CONSTRAINT chk_seat_number CHECK (seat_number > 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 5. Bảng Showtimes (lịch chiếu)
-- ---------------------------------------------------------------------
CREATE TABLE Showtimes (
    showtime_id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id    INT NOT NULL,
    room_id     INT NOT NULL,
    start_time  DATETIME NOT NULL,
    end_time    DATETIME NOT NULL,
    base_price  DECIMAL(10,2) NOT NULL,
    status      ENUM('ACTIVE', 'CANCELLED') NOT NULL DEFAULT 'ACTIVE',
    is_deleted  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_showtimes_movie
        FOREIGN KEY (movie_id) REFERENCES Movies(movie_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_showtimes_room
        FOREIGN KEY (room_id) REFERENCES Rooms(room_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT chk_showtime_price CHECK (base_price >= 0),
    CONSTRAINT chk_showtime_time  CHECK (end_time > start_time),

    -- Tối ưu kiểm tra trùng lịch phòng chiếu & lấy lịch theo phim
    INDEX idx_showtimes_lookup (room_id, start_time, end_time),
    INDEX idx_showtimes_movie (movie_id, status)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 6. Bảng Tickets (vé đã đặt)
-- ---------------------------------------------------------------------
CREATE TABLE Tickets (
    ticket_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL,
    showtime_id INT NOT NULL,
    seat_id     INT NOT NULL,
    price       DECIMAL(10,2) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_tickets_user
        FOREIGN KEY (user_id) REFERENCES Users(user_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_tickets_showtime
        FOREIGN KEY (showtime_id) REFERENCES Showtimes(showtime_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_tickets_seat
        FOREIGN KEY (seat_id) REFERENCES Seats(seat_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    -- Chốt chặn: không cho 2 người đặt trùng 1 ghế trong cùng 1 suất chiếu
    CONSTRAINT uq_showtime_seat UNIQUE (showtime_id, seat_id),
    CONSTRAINT chk_ticket_price CHECK (price >= 0),
    INDEX idx_tickets_user (user_id)
) ENGINE=InnoDB;

-- =====================================================================
-- DỮ LIỆU MẪU
-- =====================================================================

-- ---------------------------------------------------------------------
-- Users: 1 tài khoản admin để đăng nhập thử
--   email: admin@cinema.com | mật khẩu: 123456 (SHA-256, khớp PasswordUtil)
-- ---------------------------------------------------------------------
INSERT INTO Users (full_name, email, password_hash, role)
VALUES ('Admin', 'admin@cinema.com',
        '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'ADMIN');

-- ---------------------------------------------------------------------
-- Rooms: 4 phòng, mỗi phòng 50 ghế (5 hàng A-E x 10 ghế)
-- ---------------------------------------------------------------------
INSERT INTO Rooms (room_name, total_seats)
VALUES
('Room 01', 50),
('Room 02', 50),
('Room 03', 50),
('Room 04', 50);

-- ---------------------------------------------------------------------
-- Seats: tự sinh ghế cho mọi phòng; ghế VIP là hàng B, C, số 3-8
-- ---------------------------------------------------------------------
INSERT INTO Seats (room_id, seat_row, seat_number, seat_type, status)
SELECT
    r.room_id,
    rows_data.seat_row,
    s.seat_number,
    CASE
        WHEN rows_data.seat_row IN ('B', 'C')
             AND s.seat_number BETWEEN 3 AND 8
        THEN 'VIP'
        ELSE 'NORMAL'
    END AS seat_type,
    'AVAILABLE' AS status
FROM Rooms r
CROSS JOIN (
    SELECT 'A' AS seat_row
    UNION ALL SELECT 'B'
    UNION ALL SELECT 'C'
    UNION ALL SELECT 'D'
    UNION ALL SELECT 'E'
) AS rows_data
CROSS JOIN (
    SELECT 1 AS seat_number
    UNION ALL SELECT 2
    UNION ALL SELECT 3
    UNION ALL SELECT 4
    UNION ALL SELECT 5
    UNION ALL SELECT 6
    UNION ALL SELECT 7
    UNION ALL SELECT 8
    UNION ALL SELECT 9
    UNION ALL SELECT 10
) AS s;

-- ---------------------------------------------------------------------
-- Movies: 10 phim (7 đang chiếu, 1 đã dừng, 2 sắp chiếu)
-- movie_id sinh theo thứ tự 1..10, bảng Showtimes bên dưới dựa vào thứ tự này
-- ---------------------------------------------------------------------
INSERT INTO Movies (title, description, duration_mins, poster_url, status)
VALUES
('Avengers: Endgame',
 'The Avengers assemble for one final battle to restore the universe.',
 181, 'https://example.com/avengers.jpg', 'SHOWING'),

('Spider-Man: No Way Home',
 'Peter Parker faces the consequences of revealing his identity.',
 148, 'https://example.com/spiderman.jpg', 'SHOWING'),

('Interstellar',
 'A team of explorers travels beyond this galaxy to discover a future for humanity.',
 169, 'https://example.com/interstellar.jpg', 'SHOWING'),

('Inception',
 'A thief who steals corporate secrets through dream-sharing technology.',
 148, 'https://example.com/inception.jpg', 'SHOWING'),

('The Dark Knight',
 'Batman faces the Joker, a criminal mastermind threatening Gotham City.',
 152, 'https://example.com/darkknight.jpg', 'SHOWING'),

('Dune: Part Two',
 'Paul Atreides unites with the Fremen to seek revenge.',
 166, 'https://example.com/dune2.jpg', 'SHOWING'),

('Inside Out 2',
 'Riley enters adolescence and experiences new emotions.',
 96, 'https://example.com/insideout2.jpg', 'SHOWING'),

('The Batman',
 'Batman investigates corruption in Gotham City.',
 176, 'https://example.com/thebatman.jpg', 'STOPPED'),

('Avatar 3',
 'A new adventure continues the story of Pandora.',
 190, 'https://example.com/avatar3.jpg', 'UPCOMING'),

('Fantastic Four',
 'A new superhero team faces a threat beyond imagination.',
 115, 'https://example.com/fantasticfour.jpg', 'UPCOMING');

-- ---------------------------------------------------------------------
-- Showtimes: end_time = start_time + duration_mins của phim
-- ---------------------------------------------------------------------
INSERT INTO Showtimes (movie_id, room_id, start_time, end_time, base_price, status)
VALUES
-- 23/11 - Room 01
(1, 1, '2026-11-23 09:00:00', '2026-11-23 12:01:00',  90000, 'ACTIVE'),
(2, 1, '2026-11-23 13:00:00', '2026-11-23 15:28:00',  95000, 'ACTIVE'),
(3, 1, '2026-11-23 16:30:00', '2026-11-23 19:19:00', 100000, 'ACTIVE'),
-- 23/11 - Room 02
(4, 2, '2026-11-23 09:00:00', '2026-11-23 11:28:00',  85000, 'ACTIVE'),
(5, 2, '2026-11-23 13:00:00', '2026-11-23 15:32:00',  90000, 'ACTIVE'),
(6, 2, '2026-11-23 16:30:00', '2026-11-23 19:16:00', 110000, 'ACTIVE'),
-- 23/11 - Room 03
(7, 3, '2026-11-23 09:00:00', '2026-11-23 10:36:00',  80000, 'ACTIVE'),
(1, 3, '2026-11-23 13:00:00', '2026-11-23 16:01:00',  90000, 'ACTIVE'),
(2, 3, '2026-11-23 17:00:00', '2026-11-23 19:28:00',  95000, 'ACTIVE'),
-- 23/11 - Room 04
(3, 4, '2026-11-23 09:00:00', '2026-11-23 11:49:00', 100000, 'ACTIVE'),
(4, 4, '2026-11-23 13:00:00', '2026-11-23 15:28:00',  85000, 'ACTIVE'),
(5, 4, '2026-11-23 17:00:00', '2026-11-23 19:32:00',  90000, 'ACTIVE'),
-- 24/11 và 25/11
(6, 1, '2026-11-24 09:00:00', '2026-11-24 11:46:00', 110000, 'ACTIVE'),
(7, 2, '2026-11-24 13:00:00', '2026-11-24 14:36:00',  80000, 'ACTIVE'),
(1, 3, '2026-11-25 18:00:00', '2026-11-25 21:01:00', 100000, 'ACTIVE');

-- ---------------------------------------------------------------------
-- Kiểm tra nhanh sau khi chạy
-- ---------------------------------------------------------------------
SELECT 'Users'     AS bang, COUNT(*) AS so_dong FROM Users
UNION ALL SELECT 'Rooms',     COUNT(*) FROM Rooms
UNION ALL SELECT 'Seats',     COUNT(*) FROM Seats        -- mong đợi: 200
UNION ALL SELECT 'Movies',    COUNT(*) FROM Movies       -- mong đợi: 10
UNION ALL SELECT 'Showtimes', COUNT(*) FROM Showtimes;   -- mong đợi: 15
