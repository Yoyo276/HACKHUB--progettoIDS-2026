DELETE FROM user_roles;
DELETE FROM hackathon_mentori; -- Modifica il nome se la tua tabella di join si chiama diversamente
UPDATE users SET team_id = NULL; -- Svincola gli utenti dai team prima di eliminarli
DELETE FROM team;
DELETE FROM users;
DELETE FROM roles;
DELETE FROM hackathon;

-- Inserimento Ruoli Standard
INSERT INTO roles (id, name) VALUES (1, 'ROLE_ORGANIZZATORE');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_UTENTE');
INSERT INTO roles (id, name) VALUES (3, 'ROLE_GIUDICE');
INSERT INTO roles (id, name) VALUES (4, 'ROLE_MENTORE');
INSERT INTO roles (id, name) VALUES (5, 'ROLE_MEMBRO_TEAM');

-- ==========================================
-- 1. INSERIMENTO UTENTI (Tutte le pwd: password123)
-- ==========================================
INSERT INTO users (id, username, password, email) VALUES (100, 'admin', 'password123', 'admin@hack.it');

-- Due Giudici
INSERT INTO users (id, username, password, email) VALUES (101, 'giudice1', 'password123', 'g1@hack.it');
INSERT INTO users (id, username, password, email) VALUES (104, 'giudice2', 'password123', 'g2@hack.it');

-- Due Mentori
INSERT INTO users (id, username, password, email) VALUES (103, 'mentore1', 'password123', 'm1@hack.it');
INSERT INTO users (id, username, password, email) VALUES (105, 'mentore2', 'password123', 'm2@hack.it');

-- Tre Utenti (Uno libero, due già nei team)
INSERT INTO users (id, username, password, email) VALUES (102, 'user1', 'password123', 'u1@hack.it');
INSERT INTO users (id, username, password, email) VALUES (106, 'user2', 'password123', 'u2@hack.it');
INSERT INTO users (id, username, password, email) VALUES (107, 'user3', 'password123', 'u3@hack.it');
INSERT INTO users (id, username, password, email) VALUES (108, 'user4', 'password123', 'u4@hack.it');

-- Assegnazione Ruoli
INSERT INTO user_roles (user_id, role_id) VALUES (100, 1); -- admin -> ORGANIZZATORE
INSERT INTO user_roles (user_id, role_id) VALUES (101, 3); -- giudice1 -> GIUDICE
INSERT INTO user_roles (user_id, role_id) VALUES (104, 3); -- giudice2 -> GIUDICE
INSERT INTO user_roles (user_id, role_id) VALUES (103, 4); -- mentore1 -> MENTORE
INSERT INTO user_roles (user_id, role_id) VALUES (105, 4); -- mentore2 -> MENTORE
INSERT INTO user_roles (user_id, role_id) VALUES (102, 2); -- user1 -> UTENTE (Libero per testare la tua iscrizione da zero)
INSERT INTO user_roles (user_id, role_id) VALUES (106, 5); -- user2 -> MEMBRO_TEAM (Già iscritto)
INSERT INTO user_roles (user_id, role_id) VALUES (107, 5); -- user3 -> MEMBRO_TEAM (Già iscritto)
INSERT INTO user_roles (user_id, role_id) VALUES (108, 2); -- user4 -> ROLE_UTENTE (Libero)

-- ==========================================
-- 2. INSERIMENTO HACKATHON (3 Stati diversi)
-- ==========================================
-- HACK 1000: "IN ISCRIZIONE" (Assegnato a giudice1 e mentore1). Pronto per accogliere 'user1'.
INSERT INTO hackathon (id, nome, luogo, premio_in_denaro, nome_stato, giudice_assegnato_id, organizzatore_id, data_scadenza_iscrizioni, data_inizio, data_fine, dimensione_massima_team) 
VALUES (1000, 'Global Hack 2026', 'Roma', 5000.0, 'IN ISCRIZIONE', 101, 100, '2026-04-15', '2026-04-20', '2026-04-22', 5);

-- HACK 1001: "IN CORSO" (Assegnato a giudice2 e mentore2). Pronto per ricevere o aggiornare link github.
INSERT INTO hackathon (id, nome, luogo, premio_in_denaro, nome_stato, giudice_assegnato_id, organizzatore_id, data_scadenza_iscrizioni, data_inizio, data_fine, dimensione_massima_team) 
VALUES (1001, 'CyberSec Challenge', 'Milano', 3000.0, 'IN CORSO', 104, 100, '2025-01-01', '2025-01-10', '2026-12-31', 4);

-- HACK 1002: "IN VALUTAZIONE" (Assegnato a giudice1). Pronto per essere votato!
INSERT INTO hackathon (id, nome, luogo, premio_in_denaro, nome_stato, giudice_assegnato_id, organizzatore_id, data_scadenza_iscrizioni, data_inizio, data_fine, dimensione_massima_team) 
VALUES (1002, 'AI Innovators', 'Web', 7500.0, 'IN VALUTAZIONE', 101, 100, '2024-01-01', '2024-02-01', '2024-02-28', 3);

-- Assegnazione Mentori 
INSERT INTO hackathon_mentori (hackathon_id, mentori_id) VALUES (1000, 103);
INSERT INTO hackathon_mentori (hackathon_id, mentori_id) VALUES (1001, 105);
INSERT INTO hackathon_mentori (hackathon_id, mentori_id) VALUES (1002, 103);

-- ==========================================
-- 3. INSERIMENTO TEAM PRE-ESISTENTI (Spostati a ID 100+)
-- ==========================================

-- Team 100 in Hack 1000 
INSERT INTO team (id, nome, hackathon_id, messaggio_supporto, invito_accettato, premio_erogato) 
VALUES (100, 'Team Alpha', 1000, 'Abbiamo un problema col DB, SOS!', true, false);

-- Team 101 in Hack 1001 
INSERT INTO team (id, nome, hackathon_id, link_sottomissione, invito_accettato, premio_erogato) 
VALUES (101, 'Team Beta', 1001, 'https://github.com/beta/project', true, false);

-- Team 102 in Hack 1002 
INSERT INTO team (id, nome, hackathon_id, link_sottomissione, invito_accettato, premio_erogato) 
VALUES (102, 'Team Gamma', 1002, 'https://github.com/gamma/ai-core', true, false);

-- Colleghiamo gli utenti ai team (Aggiornati con i nuovi ID)
UPDATE users SET team_id = 100 WHERE id = 106; 
UPDATE users SET team_id = 101 WHERE id = 107;
