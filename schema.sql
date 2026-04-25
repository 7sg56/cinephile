-- ==========================================
-- CINEPHILE DATABASE SCHEMA
-- ==========================================

CREATE DATABASE IF NOT EXISTS cinephile_db;
USE cinephile_db;

-- 1. USERS TABLE (Covers both regular clients and admins)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'CUSTOMER') DEFAULT 'CUSTOMER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. MOVIES TABLE
CREATE TABLE IF NOT EXISTS movies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    genre VARCHAR(100),
    duration_minutes INT NOT NULL,
    rating VARCHAR(10), -- e.g. PG-13, R, U/A
    language VARCHAR(50),
    description TEXT,
    poster_path VARCHAR(255), -- local file path to the image
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. SHOWS TABLE (Specific screening instances of a movie)
CREATE TABLE IF NOT EXISTS shows (
    id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    show_date DATE NOT NULL,
    show_time TIME NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    hall_name VARCHAR(50) NOT NULL, -- e.g. Screen 1, Screen 2
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
);

-- 4. BOOKINGS TABLE (Main reservation record)
CREATE TABLE IF NOT EXISTS bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    show_id INT NOT NULL,
    booking_reference VARCHAR(20) UNIQUE NOT NULL, -- e.g. CINE-XYZ123
    status ENUM('CONFIRMED', 'CANCELLED') DEFAULT 'CONFIRMED',
    total_tickets INT NOT NULL,
    tickets_subtotal DECIMAL(10,2) NOT NULL,
    food_subtotal DECIMAL(10,2) DEFAULT 0.00,
    taxes DECIMAL(10,2) NOT NULL,
    grand_total DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (show_id) REFERENCES shows(id)
);

-- 5. SEATS TABLE (Tracks individual booked seats for a show)
CREATE TABLE IF NOT EXISTS seats (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    show_id INT NOT NULL,
    seat_number VARCHAR(10) NOT NULL, -- e.g. A1, A2, B5
    is_lounge BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (show_id) REFERENCES shows(id) ON DELETE CASCADE,
    UNIQUE KEY unique_seat_per_show (show_id, seat_number)
);

-- 6. FOOD & AMENITIES CATALOG (Admin managed items)
CREATE TABLE IF NOT EXISTS amenities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL, -- Popcorn, Coke, 3D Glasses
    category ENUM('FOOD', 'UPGRADE', 'MERCH') DEFAULT 'FOOD',
    price DECIMAL(10,2) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

-- 7. BOOKING ITEMS FOOD (Mapping bookings to purchased amenities)
CREATE TABLE IF NOT EXISTS booking_amenities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    amenity_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (amenity_id) REFERENCES amenities(id)
);

-- 8. PAYMENTS TABLE (Tracks the transaction)
CREATE TABLE IF NOT EXISTS payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    payment_method VARCHAR(50) NOT NULL, -- CARD, UPI, WALLET
    transaction_id VARCHAR(100) UNIQUE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_status ENUM('SUCCESS', 'FAILED') DEFAULT 'SUCCESS',
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
);

-- ==========================================
-- SEED SAMPLE DATA
-- ==========================================

INSERT INTO users (full_name, email, password_hash, role) VALUES 
('Admin User', 'admin@cinephile.com', 'admin123', 'ADMIN'),
('John Doe', 'john@example.com', 'pwd123', 'CUSTOMER');

INSERT INTO amenities (name, category, price) VALUES 
('Large Popcorn', 'FOOD', 150.00),
('Coke 500ml', 'FOOD', 80.00),
('3D Glasses', 'UPGRADE', 50.00),
('Lounge Upgrade', 'UPGRADE', 200.00);

-- End of schema
