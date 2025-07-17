-- V6__initial_schema.sql - Consolidación de migraciones iniciales

-- V1__init.sql: Creación de tablas iniciales
-- Tabla de roles
CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS users (
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

-- Relación usuarios - roles
CREATE TABLE IF NOT EXISTS user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Tabla de permisos
CREATE TABLE IF NOT EXISTS permission (
    id SERIAL PRIMARY KEY,
    resource_path VARCHAR(255) NOT NULL,
    http_method VARCHAR(10) NOT NULL
);

-- Relación rol-permiso
CREATE TABLE IF NOT EXISTS role_permission (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (permission_id) REFERENCES permission(id)
);

-- Tabla de categorías
CREATE TABLE IF NOT EXISTS category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    image_url VARCHAR(500)
);

-- Insertar categorías de ejemplo
INSERT INTO category (name, image_url) VALUES
('Nails', 'https://i.pinimg.com/736x/e0/5c/0d/e05c0d3094cbeab36f7badfe0ae520bd.jpg'),
('Cabello', 'https://i.pinimg.com/736x/10/be/e4/10bee46a5007eb998cc4e8e3db8309c5.jpg'),
('Spa', 'https://i.pinimg.com/736x/0e/d4/74/0ed474b14f6761fb5934be2cf48537ac.jpg');

-- Tabla de servicios
CREATE TABLE IF NOT EXISTS service (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    duration INT NOT NULL,
    price REAL NOT NULL,
    description VARCHAR(500),
    descripcion_extend VARCHAR(1000),
    category_id BIGINT,
    image_url VARCHAR(500)
);

-- Agregar la foreign key de categoría
ALTER TABLE service ADD CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES category(id);

-- Insertar datos de servicios según lo solicitado
-- NAILS (category_id = 1)
INSERT INTO service (name, duration, price, description, descripcion_extend, category_id, image_url) VALUES
('Pintado con Gel', 60, 18.00, 'Pintado de uñas con gel de larga duración.', 'Incluye limado, preparación de la uña y aplicación de gel de color a elección. Acabado brillante y duradero.', 1, 'https://i.pinimg.com/1200x/c9/ca/6b/c9ca6b1f52c2e3c3a2170fef35fa3abb.jpg'),
('Pedicura', 75, 25.00, 'Cuidado completo de uñas y pies.', 'Incluye remojo, exfoliación, corte y limado de uñas, tratamiento de cutículas y esmaltado.', 1, 'https://i.pinimg.com/1200x/e2/7d/f8/e27df8891987398442d302040182b42d.jpg');

-- CABELLO (category_id = 2)
INSERT INTO service (name, duration, price, description, descripcion_extend, category_id, image_url) VALUES
('Corte de cabello', 40, 20.00, 'Corte profesional para todo tipo de cabello.', 'Asesoría personalizada, lavado y corte según tu estilo y preferencia.', 2, 'https://i.pinimg.com/1200x/32/df/31/32df3101c0aa2b9d5e7b3564aa74556e.jpg'),
('Coloración completa', 120, 60.00, 'Coloración completa o retoque de raíces.', 'Incluye diagnóstico capilar, aplicación de color y tratamiento post-color para proteger tu cabello.', 2, 'https://i.pinimg.com/736x/02/4e/d7/024ed7fd0e0537eac7e549a59a166434.jpg');

-- SPA (category_id = 3)
INSERT INTO service (name, duration, price, description, descripcion_extend, category_id, image_url) VALUES
('Tratamiento Facial', 50, 35.00, 'Limpieza y revitalización facial profunda.', 'Incluye limpieza, exfoliación, mascarilla y masaje facial para una piel radiante.', 3, 'https://i.pinimg.com/736x/36/72/2c/36722cbe08b0e0a13384ccc6b41450b0.jpg'),
('Masaje', 60, 40.00, 'Masaje relajante de cuerpo completo.', 'Técnicas de relajación para aliviar el estrés y mejorar tu bienestar general.', 3, 'https://i.pinimg.com/736x/3f/2b/ca/3f2bcac04e5cf9da459b01a25b402f08.jpg');

-- Tabla de citas
CREATE TABLE IF NOT EXISTS appointment (
   id SERIAL PRIMARY KEY,
    service_id INT NOT NULL,
    user_id INT NOT NULL,
    date DATE NOT NULL,
    time TIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    FOREIGN KEY (service_id) REFERENCES service(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Horario
CREATE TABLE IF NOT EXISTS schedule (
    id SERIAL PRIMARY KEY,
    day VARCHAR(255) NOT NULL,
    start_time VARCHAR(255) NOT NULL,
    end_time VARCHAR(255) NOT NULL
);

-- V2__alter_columns_to_bigint.sql: Alterar tipos de columnas e insertar datos iniciales
ALTER TABLE roles ALTER COLUMN id TYPE BIGINT;
ALTER TABLE users ALTER COLUMN id TYPE BIGINT;
ALTER TABLE user_roles ALTER COLUMN user_id TYPE BIGINT;
ALTER TABLE user_roles ALTER COLUMN role_id TYPE BIGINT;
ALTER TABLE permission ALTER COLUMN id TYPE BIGINT;
ALTER TABLE role_permission ALTER COLUMN role_id TYPE BIGINT;
ALTER TABLE role_permission ALTER COLUMN permission_id TYPE BIGINT;
ALTER TABLE service ALTER COLUMN id TYPE BIGINT;
ALTER TABLE appointment ALTER COLUMN id TYPE BIGINT;
ALTER TABLE appointment ALTER COLUMN service_id TYPE BIGINT;
ALTER TABLE appointment ALTER COLUMN user_id TYPE BIGINT;
ALTER TABLE appointment ADD COLUMN email VARCHAR(255) NOT NULL DEFAULT 'placeholder@example.com';
ALTER TABLE schedule ALTER COLUMN id TYPE BIGINT;
INSERT INTO roles (name) VALUES ('CLIENT');
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('EMPLOYEE');

-- Insertar datos inventados en la tabla de horarios (schedule)
INSERT INTO schedule (day, start_time, end_time) VALUES ('MONDAY', '09:00:00', '17:00:00');
INSERT INTO schedule (day, start_time, end_time) VALUES ('TUESDAY', '09:00:00', '17:00:00');
INSERT INTO schedule (day, start_time, end_time) VALUES ('WEDNESDAY', '09:00:00', '17:00:00');
INSERT INTO schedule (day, start_time, end_time) VALUES ('THURSDAY', '09:00:00', '17:00:00');
INSERT INTO schedule (day, start_time, end_time) VALUES ('FRIDAY', '09:00:00', '17:00:00');
INSERT INTO schedule (day, start_time, end_time) VALUES ('SATURDAY', '10:00:00', '15:00:00');
INSERT INTO schedule (day, start_time, end_time) VALUES ('SUNDAY', '11:00:00', '15:00:00');

-- V3__rename_appointment_columns.sql: Renombrar columnas de cita
ALTER TABLE appointment RENAME COLUMN date TO fecha;
ALTER TABLE appointment RENAME COLUMN time TO hora; 

-- V4__add_phone_and_employee_to_appointment.sql: Añadir teléfono y empleado a la cita
ALTER TABLE appointment
ADD COLUMN phone VARCHAR(255) NOT NULL DEFAULT '';

ALTER TABLE appointment
ADD COLUMN employee_id BIGINT NOT NULL DEFAULT 0;

ALTER TABLE appointment
ADD CONSTRAINT fk_employee
FOREIGN KEY (employee_id)
REFERENCES users(id);

-- Actualizar las citas existentes con un valor predeterminado para 'phone' y 'employee_id'
UPDATE appointment SET phone = '', employee_id = (SELECT id FROM users LIMIT 1) WHERE phone = ''; 

-- V5__create_rating_table.sql: Crear tabla de valoraciones
CREATE TABLE rating (
    id BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment VARCHAR(500),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_appointment FOREIGN KEY (appointment_id) REFERENCES appointment(id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_employee FOREIGN KEY (employee_id) REFERENCES users(id)
);

-- V7__add_reminder_sent_to_appointment.sql: Añadir columna reminder_sent a la tabla appointment
ALTER TABLE appointment
ADD COLUMN reminder_sent BOOLEAN NOT NULL DEFAULT FALSE; 

-- V8__ create promotion table.sql: Crear tabla de promociones
CREATE TABLE promotion (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    conditions VARCHAR(2000),
    image_url VARCHAR(1000) -- Nueva columna para imagen de fondo
); 

-- V9__ add user id to schedule.sql: Añadir columna user_id a la tabla schedule
ALTER TABLE schedule
ADD COLUMN user_id BIGINT;
 
ALTER TABLE schedule
ADD CONSTRAINT fk_user
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE; 

-- V10__ create employee service specialty table.sql: Crear tabla de especialidades de servicios de empleados
CREATE TABLE employee_service_specialty (
    employee_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    PRIMARY KEY (employee_id, service_id),
    FOREIGN KEY (employee_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES service(id) ON DELETE CASCADE
); 

-- v11__ add password reset token table.sql: Crear tabla de tokens de restablecimiento de contraseña
CREATE TABLE IF NOT EXISTS password_reset_token (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiration_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
); 