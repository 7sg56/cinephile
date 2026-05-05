-- ==========================================
-- CINEPHILE DBA QUERIES & MAINTENANCE
-- ==========================================

-- 1. ADD NEW USERS
-- Add a new Admin user
INSERT INTO users (full_name, email, password_hash, role) 
VALUES ('Super Admin', 'admin2@cinephile.com', 'securepass123', 'ADMIN');

-- Add a new Customer user
INSERT INTO users (full_name, email, password_hash, role) 
VALUES ('Jane Smith', 'jane.smith@example.com', 'pwd456', 'CUSTOMER');

-- 2. ADD NEW MOVIES
-- Add a new movie
INSERT INTO movies (title, genre, duration_minutes, rating, language, description, poster_path, is_active) 
VALUES ('The Matrix', 'Sci-Fi', 136, 'R', 'English', 'A computer hacker learns from mysterious rebels about the true nature of his reality.', '/posters/matrix.jpg', TRUE);

-- Update a movie's status to inactive (e.g. if it is no longer showing)
UPDATE movies 
SET is_active = FALSE 
WHERE title = 'Inception';

-- 3. ADD NEW SHOWS
-- Add a new show for a specific movie (Assuming movie_id = 1)
INSERT INTO shows (movie_id, show_date, show_time, base_price, hall_name) 
VALUES (1, CURRENT_DATE + INTERVAL 2 DAY, '20:00:00', 16.00, 'Screen IMAX');


-- View all bookings and their totals
SELECT b.booking_reference, u.full_name, m.title, s.show_date, s.show_time, b.status, b.grand_total 
FROM bookings b
JOIN users u ON b.user_id = u.id
JOIN shows s ON b.show_id = s.id
JOIN movies m ON s.movie_id = m.id
ORDER BY b.created_at DESC;

-- View total revenue from SUCCESSFUL payments
SELECT SUM(amount) AS total_revenue 
FROM payments 
WHERE payment_status = 'SUCCESS';
