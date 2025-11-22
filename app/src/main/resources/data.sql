-- ========== TIPOS DE USUARIO (TypeUser) ==========

INSERT INTO type_users (id, type, description) VALUES 
(1, 'STUDENT', 'Estudiante - Máximo 3 libros por 14 días'),
(2, 'TEACHER', 'Docente - Máximo 5 libros por 30 días'),
(3, 'EXTERNAL', 'Usuario Externo - Máximo 2 libros por 7 días'),
(4, 'ADMIN', 'Administrador del sistema');

-- ========== USUARIOS ==========

INSERT INTO users (id, name, email, password, type_user_id, is_active, created_at, updated_at) VALUES
(1, 'Admin User', 'admin@biblioteca.com', 'hashed_password_admin', 4, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Juan Pérez', 'juan.perez@student.edu', 'hashed_password_juan', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'María García', 'maria.garcia@teacher.edu', 'hashed_password_maria', 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Carlos López', 'carlos.lopez@external.com', 'hashed_password_carlos', 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ========== AUTORES ==========

INSERT INTO authors (id, name, nationality) VALUES
(1, 'Gabriel García Márquez', 'Colombiano'),
(2, 'J.K. Rowling', 'Británica'),
(3, 'George Orwell', 'Británico'),
(4, 'Jane Austen', 'Británica'),
(5, 'Miguel de Cervantes', 'Español');

-- ========== CATEGORÍAS ==========

INSERT INTO categories (id, name, description) VALUES
(1, 'Ficción', 'Novelas y cuentos de ficción'),
(2, 'Fantasía', 'Literatura fantástica y de aventuras'),
(3, 'Clásicos', 'Obras literarias clásicas'),
(4, 'Ciencia Ficción', 'Literatura de ciencia ficción y distopías');

-- ========== LIBROS ==========

INSERT INTO books (id, title, isbn, year, author_id, category_id) VALUES
(1, 'Cien Años de Soledad', '978-0307474728', 1967, 1, 1),
(2, 'Harry Potter y la Piedra Filosofal', '978-0439708180', 1997, 2, 2),
(3, '1984', '978-0451524935', 1949, 3, 4),
(4, 'Orgullo y Prejuicio', '978-0141439518', 1813, 4, 3),
(5, 'Don Quijote de la Mancha', '978-8491050551', 1605, 5, 3);

-- ========== STOCK (Inventario de libros) ==========
-- Creamos múltiples copias de cada libro

INSERT INTO stock (id, book_id, user_id, availability, departure_date, delivery_date, status) VALUES
-- Cien Años de Soledad (3 copias)
(1, 1, NULL, true, NULL, NULL, 'AVAILABLE'),
(2, 1, NULL, true, NULL, NULL, 'AVAILABLE'),
(3, 1, NULL, true, NULL, NULL, 'AVAILABLE'),

-- Harry Potter (2 copias)
(4, 2, NULL, true, NULL, NULL, 'AVAILABLE'),
(5, 2, NULL, true, NULL, NULL, 'AVAILABLE'),

-- 1984 (2 copias)
(6, 3, NULL, true, NULL, NULL, 'AVAILABLE'),
(7, 3, NULL, true, NULL, NULL, 'AVAILABLE'),

-- Orgullo y Prejuicio (1 copia)
(8, 4, NULL, true, NULL, NULL, 'AVAILABLE'),

-- Don Quijote (3 copias)
(9, 5, NULL, true, NULL, NULL, 'AVAILABLE'),
(10, 5, NULL, true, NULL, NULL, 'AVAILABLE'),
(11, 5, NULL, true, NULL, NULL, 'AVAILABLE');

-- ========== PRÉSTAMOS ACTIVOS (Ejemplos) ==========

-- Juan Pérez (estudiante) tiene 2 libros prestados
UPDATE stock SET 
    user_id = 2,
    availability = false,
    departure_date = CURRENT_TIMESTAMP - INTERVAL '5 days',
    delivery_date = CURRENT_TIMESTAMP + INTERVAL '9 days',
    status = 'ACTIVE'
WHERE id = 1;

UPDATE stock SET 
    user_id = 2,
    availability = false,
    departure_date = CURRENT_TIMESTAMP - INTERVAL '3 days',
    delivery_date = CURRENT_TIMESTAMP + INTERVAL '11 days',
    status = 'ACTIVE'
WHERE id = 4;

-- María García (docente) tiene 1 libro prestado
UPDATE stock SET 
    user_id = 3,
    availability = false,
    departure_date = CURRENT_TIMESTAMP - INTERVAL '10 days',
    delivery_date = CURRENT_TIMESTAMP + INTERVAL '20 days',
    status = 'ACTIVE'
WHERE id = 6;

-- Ejemplo de préstamo RETRASADO (Carlos López - externo)
UPDATE stock SET 
    user_id = 4,
    availability = false,
    departure_date = CURRENT_TIMESTAMP - INTERVAL '10 days',
    delivery_date = CURRENT_TIMESTAMP - INTERVAL '3 days',  -- Ya pasó la fecha límite
    status = 'ACTIVE'
WHERE id = 9;
