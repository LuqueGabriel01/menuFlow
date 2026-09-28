-- =====================================================================
-- menuFlow - datos de prueba
-- =====================================================================
-- Requisitos antes de ejecutar este script:
--   1. La app tiene que haber arrancado al menos una vez en modo dev
--      (SPRING_PROFILES_ACTIVE=dev), para que existan las 5 filas de
--      "roles" y el usuario admin (los crea el DevDataSeeder).
--   2. Pensado para ejecutarse UNA VEZ sobre una base de datos de
--      desarrollo recién creada. Si ya tienes datos de pruebas
--      anteriores y quieres repetir, borra antes las tablas de menú/
--      pedidos o arranca de cero con `docker compose down -v`.
--
-- Uso:
--   mysql -h 127.0.0.1 -P 3307 -u gabriel -p restaurant_db < database/test-data.sql
--   (o pégalo en tu cliente de MySQL favorito, con esa misma conexión)
-- =====================================================================

START TRANSACTION;

-- ---------------------------------------------------------------------
-- 1. Usuarios de prueba, uno por rol (el admin ya lo crea el seeder)
-- ---------------------------------------------------------------------
-- Contraseña en claro -> hash BCrypt ya calculado, listo para insertar:
--   chef     / Chef123!     -> $2b$10$F.nIHN/IAdq7xckwYLaVneEIa.EAw6BpVASj4VUGYeiadNJJAKkFC
--   cashier  / Cashier123!  -> $2b$10$4FydOS/2RSRnBFKdGDUM7.K7wNAMPKt6Kvne572.EQNwca0bLNHPq
--   kitchen  / Kitchen123!  -> $2b$10$ZRjxJrsoi1GdYNjq/ikHW.Jts1Jdle.v9Y4wz1YgLmCBbATfSN9yG

INSERT INTO users (username, email, password, enabled, created_at, updated_at)
SELECT 'chef', 'chef@menuflow.local', '$2b$10$F.nIHN/IAdq7xckwYLaVneEIa.EAw6BpVASj4VUGYeiadNJJAKkFC', TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'chef');

INSERT INTO users (username, email, password, enabled, created_at, updated_at)
SELECT 'cashier', 'cashier@menuflow.local', '$2b$10$4FydOS/2RSRnBFKdGDUM7.K7wNAMPKt6Kvne572.EQNwca0bLNHPq', TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'cashier');

INSERT INTO users (username, email, password, enabled, created_at, updated_at)
SELECT 'kitchen', 'kitchen@menuflow.local', '$2b$10$ZRjxJrsoi1GdYNjq/ikHW.Jts1Jdle.v9Y4wz1YgLmCBbATfSN9yG', TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'kitchen');

INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'chef' AND r.name = 'ROLE_CHEF'
  AND NOT EXISTS (SELECT 1 FROM user_role WHERE user_id = u.id AND role_id = r.id);

INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'cashier' AND r.name = 'ROLE_CASHIER'
  AND NOT EXISTS (SELECT 1 FROM user_role WHERE user_id = u.id AND role_id = r.id);

INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'kitchen' AND r.name = 'ROLE_KITCHEN'
  AND NOT EXISTS (SELECT 1 FROM user_role WHERE user_id = u.id AND role_id = r.id);

-- ---------------------------------------------------------------------
-- 2. Categorías (con su traducción en español)
-- ---------------------------------------------------------------------
INSERT INTO category (id) VALUES (NULL); SET @cat_entrantes = LAST_INSERT_ID();
INSERT INTO category (id) VALUES (NULL); SET @cat_principales = LAST_INSERT_ID();
INSERT INTO category (id) VALUES (NULL); SET @cat_postres = LAST_INSERT_ID();
INSERT INTO category (id) VALUES (NULL); SET @cat_bebidas = LAST_INSERT_ID();

INSERT INTO category_translation (category_id, lang, name) VALUES
    (@cat_entrantes, 'es', 'Entrantes'),
    (@cat_principales, 'es', 'Principales'),
    (@cat_postres, 'es', 'Postres'),
    (@cat_bebidas, 'es', 'Bebidas');

-- ---------------------------------------------------------------------
-- 3. Ingredientes
-- ---------------------------------------------------------------------
INSERT INTO ingredient (id) VALUES (NULL); SET @ing_tomate = LAST_INSERT_ID();
INSERT INTO ingredient (id) VALUES (NULL); SET @ing_mozzarella = LAST_INSERT_ID();
INSERT INTO ingredient (id) VALUES (NULL); SET @ing_albahaca = LAST_INSERT_ID();
INSERT INTO ingredient (id) VALUES (NULL); SET @ing_pollo = LAST_INSERT_ID();
INSERT INTO ingredient (id) VALUES (NULL); SET @ing_lechuga = LAST_INSERT_ID();
INSERT INTO ingredient (id) VALUES (NULL); SET @ing_jamon = LAST_INSERT_ID();
INSERT INTO ingredient (id) VALUES (NULL); SET @ing_salmon = LAST_INSERT_ID();
INSERT INTO ingredient (id) VALUES (NULL); SET @ing_limon = LAST_INSERT_ID();
INSERT INTO ingredient (id) VALUES (NULL); SET @ing_arroz = LAST_INSERT_ID();
INSERT INTO ingredient (id) VALUES (NULL); SET @ing_gambas = LAST_INSERT_ID();
INSERT INTO ingredient (id) VALUES (NULL); SET @ing_chocolate = LAST_INSERT_ID();
INSERT INTO ingredient (id) VALUES (NULL); SET @ing_huevo = LAST_INSERT_ID();
INSERT INTO ingredient (id) VALUES (NULL); SET @ing_leche = LAST_INSERT_ID();

INSERT INTO ingredient_translation (ingredient_id, lang, name) VALUES
    (@ing_tomate, 'es', 'Tomate'),
    (@ing_mozzarella, 'es', 'Mozzarella'),
    (@ing_albahaca, 'es', 'Albahaca'),
    (@ing_pollo, 'es', 'Pollo'),
    (@ing_lechuga, 'es', 'Lechuga'),
    (@ing_jamon, 'es', 'Jamón'),
    (@ing_salmon, 'es', 'Salmón'),
    (@ing_limon, 'es', 'Limón'),
    (@ing_arroz, 'es', 'Arroz'),
    (@ing_gambas, 'es', 'Gambas'),
    (@ing_chocolate, 'es', 'Chocolate'),
    (@ing_huevo, 'es', 'Huevo'),
    (@ing_leche, 'es', 'Leche');

-- ---------------------------------------------------------------------
-- 4. Alérgenos
-- ---------------------------------------------------------------------
INSERT INTO allergen (id) VALUES (NULL); SET @alg_gluten = LAST_INSERT_ID();
INSERT INTO allergen (id) VALUES (NULL); SET @alg_lactosa = LAST_INSERT_ID();
INSERT INTO allergen (id) VALUES (NULL); SET @alg_huevo = LAST_INSERT_ID();
INSERT INTO allergen (id) VALUES (NULL); SET @alg_marisco = LAST_INSERT_ID();
INSERT INTO allergen (id) VALUES (NULL); SET @alg_frutos_secos = LAST_INSERT_ID();

INSERT INTO allergen_translation (allergen_id, lang, name) VALUES
    (@alg_gluten, 'es', 'Gluten'),
    (@alg_lactosa, 'es', 'Lactosa'),
    (@alg_huevo, 'es', 'Huevo'),
    (@alg_marisco, 'es', 'Marisco'),
    (@alg_frutos_secos, 'es', 'Frutos secos');

-- ---------------------------------------------------------------------
-- 5. Platos (uno de cada categoría, con precios, disponibilidad,
--    ingredientes y alérgenos variados)
-- ---------------------------------------------------------------------
SET @chef_id = (SELECT id FROM users WHERE username = 'chef');

INSERT INTO dish (category_id, price, available, created_by, created_at, updated_at) VALUES
    (@cat_entrantes, 8.50, TRUE, @chef_id, NOW(), NOW());
SET @dish_ensalada = LAST_INSERT_ID();

INSERT INTO dish (category_id, price, available, created_by, created_at, updated_at) VALUES
    (@cat_entrantes, 6.90, TRUE, @chef_id, NOW(), NOW());
SET @dish_croquetas = LAST_INSERT_ID();

INSERT INTO dish (category_id, price, available, created_by, created_at, updated_at) VALUES
    (@cat_principales, 9.50, TRUE, @chef_id, NOW(), NOW());
SET @dish_pizza = LAST_INSERT_ID();

INSERT INTO dish (category_id, price, available, created_by, created_at, updated_at) VALUES
    (@cat_principales, 14.90, TRUE, @chef_id, NOW(), NOW());
SET @dish_salmon = LAST_INSERT_ID();

INSERT INTO dish (category_id, price, available, created_by, created_at, updated_at) VALUES
    (@cat_principales, 22.00, TRUE, @chef_id, NOW(), NOW());
SET @dish_paella = LAST_INSERT_ID();

INSERT INTO dish (category_id, price, available, created_by, created_at, updated_at) VALUES
    (@cat_postres, 5.50, TRUE, @chef_id, NOW(), NOW());
SET @dish_tarta = LAST_INSERT_ID();

INSERT INTO dish (category_id, price, available, created_by, created_at, updated_at) VALUES
    (@cat_postres, 4.50, TRUE, @chef_id, NOW(), NOW());
SET @dish_flan = LAST_INSERT_ID();

INSERT INTO dish (category_id, price, available, created_by, created_at, updated_at) VALUES
    (@cat_bebidas, 1.80, TRUE, @chef_id, NOW(), NOW());
SET @dish_agua = LAST_INSERT_ID();

INSERT INTO dish (category_id, price, available, created_by, created_at, updated_at) VALUES
    (@cat_bebidas, 2.50, TRUE, @chef_id, NOW(), NOW());
SET @dish_refresco = LAST_INSERT_ID();

-- Plato marcado como NO disponible a propósito, para probar el filtro/toggle
INSERT INTO dish (category_id, price, available, created_by, created_at, updated_at) VALUES
    (@cat_bebidas, 3.50, FALSE, @chef_id, NOW(), NOW());
SET @dish_vino = LAST_INSERT_ID();

INSERT INTO dish_translation (dish_id, lang, name, description) VALUES
    (@dish_ensalada, 'es', 'Ensalada César', 'Lechuga, pollo a la plancha y salsa césar'),
    (@dish_croquetas, 'es', 'Croquetas de jamón', 'Seis croquetas caseras de jamón ibérico'),
    (@dish_pizza, 'es', 'Pizza margarita', 'Tomate, mozzarella y albahaca fresca'),
    (@dish_salmon, 'es', 'Salmón a la plancha', 'Salmón fresco con un toque de limón'),
    (@dish_paella, 'es', 'Paella de gambas', 'Arroz meloso con gambas, para compartir'),
    (@dish_tarta, 'es', 'Tarta de chocolate', 'Bizcocho húmedo de chocolate con nata'),
    (@dish_flan, 'es', 'Flan casero', 'Flan de huevo con leche, receta tradicional'),
    (@dish_agua, 'es', 'Agua mineral', 'Botella de 500ml'),
    (@dish_refresco, 'es', 'Refresco de cola', 'Lata de 330ml'),
    (@dish_vino, 'es', 'Copa de vino tinto', 'Actualmente sin stock');

INSERT INTO ingredient_dish (ingredient_id, dish_id) VALUES
    (@ing_lechuga, @dish_ensalada), (@ing_pollo, @dish_ensalada),
    (@ing_jamon, @dish_croquetas),
    (@ing_tomate, @dish_pizza), (@ing_mozzarella, @dish_pizza), (@ing_albahaca, @dish_pizza),
    (@ing_salmon, @dish_salmon), (@ing_limon, @dish_salmon),
    (@ing_arroz, @dish_paella), (@ing_gambas, @dish_paella),
    (@ing_chocolate, @dish_tarta), (@ing_huevo, @dish_tarta),
    (@ing_huevo, @dish_flan), (@ing_leche, @dish_flan);

INSERT INTO dish_allergen (dish_id, allergen_id) VALUES
    (@dish_croquetas, @alg_gluten), (@dish_croquetas, @alg_lactosa),
    (@dish_pizza, @alg_gluten), (@dish_pizza, @alg_lactosa),
    (@dish_paella, @alg_marisco),
    (@dish_tarta, @alg_huevo), (@dish_tarta, @alg_lactosa), (@dish_tarta, @alg_frutos_secos),
    (@dish_flan, @alg_huevo), (@dish_flan, @alg_lactosa);

-- ---------------------------------------------------------------------
-- 6. Mesas (una inactiva a propósito, para probar ese filtro)
-- ---------------------------------------------------------------------
INSERT INTO dining_tables (number, is_active, qr_code) VALUES
    (1, TRUE, 'QR-TABLE-01');
SET @table1 = LAST_INSERT_ID();

INSERT INTO dining_tables (number, is_active, qr_code) VALUES
    (2, TRUE, 'QR-TABLE-02');
SET @table2 = LAST_INSERT_ID();

INSERT INTO dining_tables (number, is_active, qr_code) VALUES
    (3, FALSE, 'QR-TABLE-03');
SET @table3 = LAST_INSERT_ID();

-- ---------------------------------------------------------------------
-- 7. Sesiones abiertas (para poder pedir en ellas ya mismo)
-- ---------------------------------------------------------------------
SET @cashier_id = (SELECT id FROM users WHERE username = 'cashier');

INSERT INTO table_session (table_id, status, opened_by, opened_at) VALUES
    (@table1, 'OPEN', @cashier_id, NOW());
SET @session1 = LAST_INSERT_ID();

INSERT INTO table_session (table_id, status, opened_by, opened_at) VALUES
    (@table2, 'OPEN', @cashier_id, NOW());
SET @session2 = LAST_INSERT_ID();

-- ---------------------------------------------------------------------
-- 8. Pedidos, en distintos puntos del flujo
-- ---------------------------------------------------------------------

-- Pedido A: recién creado, para verlo aparecer en /api/kitchen/queue
INSERT INTO orders (session_id, status, total_amount, created_at, updated_at) VALUES
    (@session1, 'PENDING', 0, NOW(), NOW());
SET @order_a = LAST_INSERT_ID();

INSERT INTO order_item (order_id, dish_id, quantity, price, status) VALUES
    (@order_a, @dish_pizza, 2, 19.00, 'PENDING'),
    (@order_a, @dish_agua, 2, 3.60, 'PENDING');

UPDATE orders SET total_amount = 22.60 WHERE id = @order_a;

-- Pedido B: ya en cocina (IN_PROGRESS)
INSERT INTO orders (session_id, status, total_amount, created_at, updated_at) VALUES
    (@session1, 'IN_PROGRESS', 0, NOW(), NOW());
SET @order_b = LAST_INSERT_ID();

INSERT INTO order_item (order_id, dish_id, quantity, price, status) VALUES
    (@order_b, @dish_ensalada, 1, 8.50, 'IN_PROGRESS'),
    (@order_b, @dish_croquetas, 1, 6.90, 'IN_PROGRESS');

UPDATE orders SET total_amount = 15.40 WHERE id = @order_b;

-- Pedido C: servido, pendiente de cobrar (para probar POST /api/invoices)
INSERT INTO orders (session_id, status, total_amount, created_at, updated_at) VALUES
    (@session2, 'SERVED', 17.40, NOW(), NOW());
SET @order_c = LAST_INSERT_ID();

INSERT INTO order_item (order_id, dish_id, quantity, price, status) VALUES
    (@order_c, @dish_salmon, 1, 14.90, 'SERVED'),
    (@order_c, @dish_refresco, 1, 2.50, 'SERVED');

-- Pedido D: ya cobrado y cerrado, con su factura (para probar los GET de facturación)
INSERT INTO orders (session_id, status, total_amount, created_at, updated_at, closed_at) VALUES
    (@session2, 'CLOSED', 26.50, NOW(), NOW(), NOW());
SET @order_d = LAST_INSERT_ID();

INSERT INTO order_item (order_id, dish_id, quantity, price, status) VALUES
    (@order_d, @dish_paella, 1, 22.00, 'CLOSED'),
    (@order_d, @dish_flan, 1, 4.50, 'CLOSED');

INSERT INTO invoice (order_id, total_amount, paid_at, created_by, payment_method) VALUES
    (@order_d, 26.50, NOW(), @cashier_id, 'CASH');

COMMIT;
