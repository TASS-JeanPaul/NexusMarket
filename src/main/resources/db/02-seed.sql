INSERT INTO app_user (id, full_name, identity_document, email, phone, role, status, registered_at) VALUES
    (1, 'Claudia Rivera',  '1010101010', 'claudia.rivera@nexusmarket.co', '3001112233', 'ADMINISTRATOR',      'ACTIVE', '2026-08-01 08:00:00'),
    (2, 'Hernan Pulido',   '1020202020', 'hernan.pulido@nexusmarket.co',  '3002223344', 'SUPERVISOR',         'ACTIVE', '2026-08-01 08:30:00'),
    (3, 'Marcela Osorio',  '1030303030', 'marcela.osorio@nexusmarket.co', '3003334455', 'LOGISTICS_OPERATOR', 'ACTIVE', '2026-08-01 09:00:00'),
    (4, 'Julian Mesa',     '1040404040', 'julian.mesa@tecnoandina.co',    '3004445566', 'SELLER',             'ACTIVE', '2026-08-05 10:00:00'),
    (5, 'Paola Guerrero',  '1050505050', 'paola.guerrero@estudiodig.co',  '3005556677', 'SELLER',             'ACTIVE', '2026-08-06 11:00:00'),
    (6, 'Laura Betancur',  '1060606060', 'laura.betancur@correo.co',      '3006667788', 'BUYER',              'ACTIVE', '2026-08-10 14:00:00'),
    (7, 'Andres Quintero', '1070707070', 'andres.quintero@correo.co',     '3007778899', 'BUYER',              'ACTIVE', '2026-08-12 16:00:00');

INSERT INTO seller (id, business_name, tax_id, registered_by_id) VALUES
    (4, 'TecnoAndina S.A.S.',   '900123456-1', 1),
    (5, 'Estudio Digital Ltda', '900654321-7', 1);

INSERT INTO buyer (id, commercial_status) VALUES
    (6, 'ENABLED'),
    (7, 'ENABLED');

INSERT INTO address (id, buyer_id, street, city, state, notes, is_primary) VALUES
    (1, 6, 'Carrera 45 # 26-30', 'Bogota',   'Cundinamarca', 'Apartamento 502', TRUE),
    (2, 6, 'Calle 10 # 5-18',    'Bogota',   'Cundinamarca', 'Oficina',         FALSE),
    (3, 7, 'Avenida 33 # 70-12', 'Medellin', 'Antioquia',    NULL,              TRUE);

INSERT INTO category (id, name, description) VALUES
    (1, 'Electronica', 'Dispositivos y accesorios electronicos'),
    (2, 'Hogar',       'Articulos para el hogar'),
    (3, 'Software',    'Licencias y productos digitales');

INSERT INTO product (id, code, name, description, price, type, status, seller_id, category_id, created_at) VALUES
    (1, 'TEC-001', 'Audifonos inalambricos', 'Audifonos con cancelacion de ruido',     180000.00, 'PHYSICAL', 'PUBLISHED',     4, 1, '2026-08-07 09:00:00'),
    (2, 'TEC-002', 'Teclado mecanico',       'Teclado mecanico retroiluminado',        240000.00, 'PHYSICAL', 'PUBLISHED',     4, 1, '2026-08-07 09:30:00'),
    (3, 'HOG-001', 'Cafetera programable',   'Cafetera de 12 tazas con temporizador',  320000.00, 'PHYSICAL', 'PUBLISHED',     4, 2, '2026-08-08 10:00:00'),
    (4, 'SOF-001', 'Licencia de diseno',     'Licencia anual de software de diseno',   450000.00, 'DIGITAL',  'PUBLISHED',     5, 3, '2026-08-09 11:00:00'),
    (5, 'TEC-003', 'Mouse vertical',         'Mouse ergonomico descontinuado',         95000.00,  'PHYSICAL', 'DISCONTINUED',  4, 1, '2026-08-10 12:00:00');

INSERT INTO variant (id, product_id, sku, name, value, additional_price) VALUES
    (1, 1, 'TEC-001-NEG', 'Color', 'Negro',  0.00),
    (2, 1, 'TEC-001-BLA', 'Color', 'Blanco', 15000.00);

INSERT INTO warehouse (id, code, name, street, city, type, seller_id, active) VALUES
    (1, 'BOD-CENTRAL', 'Bodega central del marketplace', 'Zona Industrial Montevideo', 'Bogota',   'MARKETPLACE', NULL, TRUE),
    (2, 'BOD-TEC',     'Bodega TecnoAndina',             'Carrera 50 # 10-20',         'Medellin', 'SELLER',      4,    TRUE);

INSERT INTO inventory (id, product_id, variant_id, warehouse_id, available_quantity, reserved_quantity, status, updated_at) VALUES
    (1, 1, 1,    1, 39, 0, 'AVAILABLE', '2026-09-20 10:00:00'),
    (2, 1, 2,    1, 15, 0, 'AVAILABLE', '2026-08-15 10:00:00'),
    (3, 2, NULL, 1, 24, 0, 'AVAILABLE', '2026-09-20 10:00:00'),
    (4, 3, NULL, 2, 10, 0, 'AVAILABLE', '2026-08-15 10:00:00');

INSERT INTO inventory_movement (id, inventory_id, type, quantity, user_id, notes, date) VALUES
    (1,  1, 'INBOUND',       40, 3, 'Initial inventory registration', '2026-08-15 10:00:00'),
    (2,  2, 'INBOUND',       15, 3, 'Initial inventory registration', '2026-08-15 10:00:00'),
    (3,  3, 'INBOUND',       25, 3, 'Initial inventory registration', '2026-08-15 10:00:00'),
    (4,  4, 'INBOUND',       10, 3, 'Initial inventory registration', '2026-08-15 10:00:00'),
    (5,  1, 'RESERVATION',    2, 6, 'Reservation for purchase',       '2026-09-10 15:00:00'),
    (6,  3, 'RESERVATION',    1, 6, 'Reservation for purchase',       '2026-09-10 15:00:00'),
    (7,  1, 'SALE_OUTBOUND',  2, 3, 'Outbound for sale',              '2026-09-11 09:00:00'),
    (8,  3, 'SALE_OUTBOUND',  1, 3, 'Outbound for sale',              '2026-09-11 09:00:00'),
    (9,  1, 'RETURN',         1, 3, 'Inbound from a return',          '2026-09-20 10:00:00');

INSERT INTO cart (id, buyer_id, total, active, created_at) VALUES
    (1, 6, 0.00,      FALSE, '2026-09-10 14:30:00'),
    (2, 7, 320000.00, TRUE,  '2026-10-12 18:00:00');

INSERT INTO cart_item (id, cart_id, product_id, variant_id, quantity, unit_price, subtotal) VALUES
    (1, 2, 3, NULL, 1, 320000.00, 320000.00);

INSERT INTO orders (id, order_number, buyer_id, delivery_address_id, status, total, created_at, completed_at) VALUES
    (1, 'ORD-20260910-A1B2C3', 6, 1,    'COMPLETED',       600000.00, '2026-09-10 15:00:00', '2026-09-18 12:00:00'),
    (2, 'ORD-20260925-D4E5F6', 7, NULL, 'COMPLETED',       450000.00, '2026-09-25 10:00:00', '2026-09-25 10:05:00'),
    (3, 'ORD-20261014-G7H8I9', 7, 3,    'PENDING_PAYMENT', 240000.00, '2026-10-14 09:00:00', NULL);

INSERT INTO order_detail (id, order_id, product_id, variant_id, product_name, quantity, unit_price, subtotal) VALUES
    (1, 1, 1, 1,    'Audifonos inalambricos', 2, 180000.00, 360000.00),
    (2, 1, 2, NULL, 'Teclado mecanico',       1, 240000.00, 240000.00),
    (3, 2, 4, NULL, 'Licencia de diseno',     1, 450000.00, 450000.00),
    (4, 3, 2, NULL, 'Teclado mecanico',       1, 240000.00, 240000.00);

INSERT INTO invoice (id, invoice_number, order_id, subtotal, taxes, total, status, issued_at) VALUES
    (1, 'INV-20260910-J1K2L3', 1, 600000.00, 114000.00, 714000.00, 'PAID', '2026-09-10 15:10:00'),
    (2, 'INV-20260925-M4N5O6', 2, 450000.00, 85500.00,  535500.00, 'PAID', '2026-09-25 10:05:00');

INSERT INTO shipment (id, order_id, source_warehouse_id, logistics_operator_id, tracking_number, carrier, delivery_address, status, dispatched_at, delivered_at) VALUES
    (1, 1, 1, 3, 'SHP-20260911-P1Q2R3', 'Servientrega', 'Carrera 45 # 26-30, Bogota', 'DELIVERED', '2026-09-11 09:30:00', '2026-09-15 14:00:00');

INSERT INTO product_return (id, return_number, order_id, buyer_id, reason, status, reviewed_by_id, requested_at, closed_at) VALUES
    (1, 'RET-20260916-S1T2U3', 1, 6, 'Una de las dos unidades llego con un audifono sin sonido', 'COMPLETED', 2, '2026-09-16 11:00:00', '2026-09-20 10:30:00');

INSERT INTO product_return_detail (id, return_id, order_detail_id, returned_quantity, notes) VALUES
    (1, 1, 1, 1, 'Unidad defectuosa');

INSERT INTO refund (id, return_id, amount, method, status, authorized_by_id, requested_at, paid_at) VALUES
    (1, 1, 180000.00, 'BANK_TRANSFER', 'PAID', 1, '2026-09-20 11:00:00', '2026-09-22 09:00:00');

SELECT setval('app_user_id_seq',              (SELECT MAX(id) FROM app_user));
SELECT setval('address_id_seq',               (SELECT MAX(id) FROM address));
SELECT setval('category_id_seq',              (SELECT MAX(id) FROM category));
SELECT setval('product_id_seq',               (SELECT MAX(id) FROM product));
SELECT setval('variant_id_seq',               (SELECT MAX(id) FROM variant));
SELECT setval('warehouse_id_seq',             (SELECT MAX(id) FROM warehouse));
SELECT setval('inventory_id_seq',             (SELECT MAX(id) FROM inventory));
SELECT setval('inventory_movement_id_seq',    (SELECT MAX(id) FROM inventory_movement));
SELECT setval('cart_id_seq',                  (SELECT MAX(id) FROM cart));
SELECT setval('cart_item_id_seq',             (SELECT MAX(id) FROM cart_item));
SELECT setval('orders_id_seq',                (SELECT MAX(id) FROM orders));
SELECT setval('order_detail_id_seq',          (SELECT MAX(id) FROM order_detail));
SELECT setval('invoice_id_seq',               (SELECT MAX(id) FROM invoice));
SELECT setval('shipment_id_seq',              (SELECT MAX(id) FROM shipment));
SELECT setval('product_return_id_seq',        (SELECT MAX(id) FROM product_return));
SELECT setval('product_return_detail_id_seq', (SELECT MAX(id) FROM product_return_detail));
SELECT setval('refund_id_seq',                (SELECT MAX(id) FROM refund));
