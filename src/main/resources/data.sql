-- Inserimento dei Ruoli base
INSERT INTO roles (id, name) VALUES (1, 'ROLE_USER');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_ADMIN');
INSERT INTO roles (id, name) VALUES (3, 'ROLE_ORGANIZER');
INSERT INTO roles (id, name) VALUES (4, 'ROLE_JUDGE');

-- Inserimento Utenti di test (Password "password" criptata o in chiaro a seconda del tuo encoder)
-- Nota: se non hai ancora attivato BCrypt, metti pure 'password'
INSERT INTO users (id, username, email, password) VALUES 
(1, 'admin', 'admin@hackhub.it', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DM99S..60Z2.'),
(2, 'user1', 'mario@rossi.it', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DM99S..60Z2.');

-- Collegamento Utenti-Ruoli
INSERT INTO user_roles (user_id, role_id) VALUES (1, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 1);

-- Inserimento Hackathon (Solo i dati fondamentali)
INSERT INTO hackathon (id, nome, luogo, premio_in_denaro) VALUES 
(1, 'Global Game Jam 2026', 'Roma', 5000.0),
(2, 'Spring Boot Hack', 'Milano', 3500.0),
(3, 'AI for Good', 'Online', 10000.0);