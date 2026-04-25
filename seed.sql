INSERT INTO movies (title, genre, duration_minutes, rating, language, description, poster_path, is_active) VALUES 
('Inception', 'Sci-Fi', 148, 'PG-13', 'English', 'Dream within a dream', '/posters/inception.jpg', TRUE),
('The Dark Knight', 'Action', 152, 'PG-13', 'English', 'Batman vs Joker', '/posters/tdk.jpg', TRUE),
('Interstellar', 'Sci-Fi', 169, 'PG-13', 'English', 'Space exploration', '/posters/interstellar.jpg', TRUE);

INSERT INTO shows (movie_id, show_date, show_time, base_price, hall_name) VALUES 
(1, CURRENT_DATE, '18:00:00', 12.50, 'Screen 1'),
(1, CURRENT_DATE, '21:00:00', 14.00, 'Screen 1'),
(2, CURRENT_DATE, '19:30:00', 12.50, 'Screen 2'),
(3, CURRENT_DATE, '20:15:00', 15.00, 'Screen IMAX');
