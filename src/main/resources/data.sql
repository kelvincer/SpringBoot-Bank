INSERT INTO users (email, password) VALUES ('john@example.com', 'john123');
INSERT INTO users (email, password) VALUES ('jane@example.com', 'jane123');
INSERT INTO users (email, password) VALUES ('mike@example.com', 'mike123');

INSERT INTO credits (title, identifier, status, balance, monthly_fee, expiration, rate, init_date, total_term, user_id) VALUES ('Credito Hipotecario', 'CR-001', 'Activo', 120000.00, 850.50, '2027-06-15', 8.5, '2023-06-15', 144, 1);
INSERT INTO credits (title, identifier, status, balance, monthly_fee, expiration, rate, init_date, total_term, user_id) VALUES ('Credito Vehicular', 'CR-002', 'Activo', 25000.00, 520.25, '2026-11-10', 11.2, '2023-03-10', 60, 1);
INSERT INTO credits (title, identifier, status, balance, monthly_fee, expiration, rate, init_date, total_term, user_id) VALUES ('Credito Negocio', 'CR-007', 'Activo', 15000.00, 650.00, '2027-08-20', 13.0, '2025-08-20', 36, 1);
INSERT INTO credits (title, identifier, status, balance, monthly_fee, expiration, rate, init_date, total_term, user_id) VALUES ('Credito Personal', 'CR-003', 'Inactivo', 0.00, 0.00, '2026-12-20', 15.0, '2023-12-20', 24, 2);
INSERT INTO credits (title, identifier, status, balance, monthly_fee, expiration, rate, init_date, total_term, user_id) VALUES ('Tarjeta de Credito', 'CR-004', 'Activo', 4500.75, 150.00, '2026-12-01', 28.5, '2024-01-01', 72, 2);
INSERT INTO credits (title, identifier, status, balance, monthly_fee, expiration, rate, init_date, total_term, user_id) VALUES ('Credito de Estudio', 'CR-005', 'Activo', 8000.00, 220.75, '2027-09-05', 9.8, '2024-09-05', 60, 3);
INSERT INTO credits (title, identifier, status, balance, monthly_fee, expiration, rate, init_date, total_term, user_id) VALUES ('Credito Empresarial', 'CR-006', 'Inactivo', 0.00, 0.00, '2026-12-30', 12.0, '2022-05-30', 48, 3);