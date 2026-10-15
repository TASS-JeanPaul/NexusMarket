CREATE TABLE app_user (
    id                BIGSERIAL    PRIMARY KEY,
    full_name         VARCHAR(150) NOT NULL,
    identity_document VARCHAR(30)  NOT NULL UNIQUE,
    email             VARCHAR(150) NOT NULL UNIQUE,
    phone             VARCHAR(30),
    role              VARCHAR(20)  NOT NULL
        CHECK (role IN ('BUYER', 'SELLER', 'LOGISTICS_OPERATOR', 'ADMINISTRATOR', 'SUPERVISOR')),
    status            VARCHAR(20)  NOT NULL
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED')),
    registered_at     TIMESTAMP    NOT NULL
);

CREATE TABLE buyer (
    id                BIGINT      PRIMARY KEY REFERENCES app_user (id),
    commercial_status VARCHAR(20) NOT NULL CHECK (commercial_status IN ('ENABLED', 'SUSPENDED'))
);

CREATE TABLE seller (
    id                BIGINT       PRIMARY KEY REFERENCES app_user (id),
    business_name     VARCHAR(150) NOT NULL,
    tax_id            VARCHAR(30)  NOT NULL UNIQUE,
    registered_by_id  BIGINT       NOT NULL REFERENCES app_user (id)
);

CREATE TABLE address (
    id         BIGSERIAL    PRIMARY KEY,
    buyer_id   BIGINT       NOT NULL REFERENCES buyer (id),
    street     VARCHAR(200) NOT NULL,
    city       VARCHAR(100) NOT NULL,
    state      VARCHAR(100),
    notes      VARCHAR(300),
    is_primary BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE category (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(300)
);

CREATE TABLE product (
    id          BIGSERIAL      PRIMARY KEY,
    code        VARCHAR(40)    NOT NULL UNIQUE,
    name        VARCHAR(150)   NOT NULL,
    description VARCHAR(500),
    price       NUMERIC(14, 2) NOT NULL CHECK (price > 0),
    type        VARCHAR(10)    NOT NULL CHECK (type IN ('PHYSICAL', 'DIGITAL')),
    status      VARCHAR(20)    NOT NULL
        CHECK (status IN ('PUBLISHED', 'SUSPENDED', 'DISCONTINUED')),
    seller_id   BIGINT         NOT NULL REFERENCES seller (id),
    category_id BIGINT         NOT NULL REFERENCES category (id),
    created_at  TIMESTAMP      NOT NULL
);

CREATE TABLE variant (
    id               BIGSERIAL      PRIMARY KEY,
    product_id       BIGINT         NOT NULL REFERENCES product (id),
    sku              VARCHAR(40)    NOT NULL UNIQUE,
    name             VARCHAR(100)   NOT NULL,
    value            VARCHAR(100)   NOT NULL,
    additional_price NUMERIC(14, 2) NOT NULL DEFAULT 0 CHECK (additional_price >= 0)
);

CREATE TABLE warehouse (
    id        BIGSERIAL    PRIMARY KEY,
    code      VARCHAR(40)  NOT NULL UNIQUE,
    name      VARCHAR(150) NOT NULL,
    street    VARCHAR(200),
    city      VARCHAR(100) NOT NULL,
    type      VARCHAR(20)  NOT NULL CHECK (type IN ('MARKETPLACE', 'SELLER')),
    seller_id BIGINT       REFERENCES seller (id),
    active    BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT warehouse_ownership CHECK (
        (type = 'SELLER' AND seller_id IS NOT NULL)
        OR (type = 'MARKETPLACE' AND seller_id IS NULL)
    )
);

CREATE TABLE inventory (
    id                 BIGSERIAL   PRIMARY KEY,
    product_id         BIGINT      NOT NULL REFERENCES product (id),
    variant_id         BIGINT      REFERENCES variant (id),
    warehouse_id       BIGINT      NOT NULL REFERENCES warehouse (id),
    available_quantity INTEGER     NOT NULL DEFAULT 0 CHECK (available_quantity >= 0),
    reserved_quantity  INTEGER     NOT NULL DEFAULT 0 CHECK (reserved_quantity >= 0),
    status             VARCHAR(20) NOT NULL CHECK (status IN ('AVAILABLE', 'RESERVED', 'DAMAGED')),
    updated_at         TIMESTAMP   NOT NULL,
    CONSTRAINT inventory_unique_location
        UNIQUE NULLS NOT DISTINCT (product_id, variant_id, warehouse_id)
);

CREATE TABLE inventory_movement (
    id           BIGSERIAL   PRIMARY KEY,
    inventory_id BIGINT      NOT NULL REFERENCES inventory (id),
    type         VARCHAR(20) NOT NULL
        CHECK (type IN ('INBOUND', 'RESERVATION', 'SALE_OUTBOUND', 'ADJUSTMENT', 'RETURN')),
    quantity     INTEGER     NOT NULL CHECK (quantity >= 0),
    user_id      BIGINT      NOT NULL REFERENCES app_user (id),
    notes        VARCHAR(300),
    date         TIMESTAMP   NOT NULL
);

CREATE TABLE cart (
    id         BIGSERIAL      PRIMARY KEY,
    buyer_id   BIGINT         NOT NULL REFERENCES buyer (id),
    total      NUMERIC(14, 2) NOT NULL DEFAULT 0 CHECK (total >= 0),
    active     BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP      NOT NULL
);

CREATE TABLE cart_item (
    id         BIGSERIAL      PRIMARY KEY,
    cart_id    BIGINT         NOT NULL REFERENCES cart (id),
    product_id BIGINT         NOT NULL REFERENCES product (id),
    variant_id BIGINT         REFERENCES variant (id),
    quantity   INTEGER        NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(14, 2) NOT NULL CHECK (unit_price >= 0),
    subtotal   NUMERIC(14, 2) NOT NULL CHECK (subtotal >= 0)
);

CREATE TABLE orders (
    id                  BIGSERIAL      PRIMARY KEY,
    order_number        VARCHAR(40)    NOT NULL UNIQUE,
    buyer_id            BIGINT         NOT NULL REFERENCES buyer (id),
    delivery_address_id BIGINT         REFERENCES address (id),
    status              VARCHAR(20)    NOT NULL CHECK (status IN
        ('CART', 'PENDING_PAYMENT', 'PAID', 'SHIPPED', 'DELIVERED', 'COMPLETED', 'CANCELLED')),
    total               NUMERIC(14, 2) NOT NULL CHECK (total >= 0),
    created_at          TIMESTAMP      NOT NULL,
    completed_at        TIMESTAMP
);

CREATE TABLE order_detail (
    id           BIGSERIAL      PRIMARY KEY,
    order_id     BIGINT         NOT NULL REFERENCES orders (id),
    product_id   BIGINT         NOT NULL REFERENCES product (id),
    variant_id   BIGINT         REFERENCES variant (id),
    product_name VARCHAR(150)   NOT NULL,
    quantity     INTEGER        NOT NULL CHECK (quantity > 0),
    unit_price   NUMERIC(14, 2) NOT NULL CHECK (unit_price >= 0),
    subtotal     NUMERIC(14, 2) NOT NULL CHECK (subtotal >= 0)
);

CREATE TABLE invoice (
    id             BIGSERIAL      PRIMARY KEY,
    invoice_number VARCHAR(40)    NOT NULL UNIQUE,
    order_id       BIGINT         NOT NULL UNIQUE REFERENCES orders (id),
    subtotal       NUMERIC(14, 2) NOT NULL CHECK (subtotal >= 0),
    taxes          NUMERIC(14, 2) NOT NULL CHECK (taxes >= 0),
    total          NUMERIC(14, 2) NOT NULL CHECK (total >= 0),
    status         VARCHAR(20)    NOT NULL CHECK (status IN ('ISSUED', 'PAID', 'VOIDED')),
    issued_at      TIMESTAMP      NOT NULL
);

CREATE TABLE shipment (
    id                     BIGSERIAL    PRIMARY KEY,
    order_id               BIGINT       NOT NULL UNIQUE REFERENCES orders (id),
    source_warehouse_id    BIGINT       NOT NULL REFERENCES warehouse (id),
    logistics_operator_id  BIGINT       NOT NULL REFERENCES app_user (id),
    tracking_number        VARCHAR(40)  NOT NULL UNIQUE,
    carrier                VARCHAR(100) NOT NULL,
    delivery_address       VARCHAR(300) NOT NULL,
    status                 VARCHAR(20)  NOT NULL CHECK (status IN
        ('PENDING', 'IN_PREPARATION', 'SHIPPED', 'IN_TRANSIT', 'DELIVERED', 'FAILED')),
    dispatched_at          TIMESTAMP,
    delivered_at           TIMESTAMP
);

CREATE TABLE product_return (
    id              BIGSERIAL    PRIMARY KEY,
    return_number   VARCHAR(40)  NOT NULL UNIQUE,
    order_id        BIGINT       NOT NULL REFERENCES orders (id),
    buyer_id        BIGINT       NOT NULL REFERENCES buyer (id),
    reason          VARCHAR(500) NOT NULL,
    status          VARCHAR(20)  NOT NULL CHECK (status IN
        ('REQUESTED', 'APPROVED', 'REJECTED', 'RECEIVED', 'COMPLETED')),
    reviewed_by_id  BIGINT       REFERENCES app_user (id),
    requested_at    TIMESTAMP    NOT NULL,
    closed_at       TIMESTAMP
);

CREATE TABLE product_return_detail (
    id                BIGSERIAL    PRIMARY KEY,
    return_id         BIGINT       NOT NULL REFERENCES product_return (id),
    order_detail_id   BIGINT       NOT NULL REFERENCES order_detail (id),
    returned_quantity INTEGER      NOT NULL CHECK (returned_quantity > 0),
    notes             VARCHAR(300)
);

CREATE TABLE refund (
    id                BIGSERIAL      PRIMARY KEY,
    return_id         BIGINT         NOT NULL UNIQUE REFERENCES product_return (id),
    amount            NUMERIC(14, 2) NOT NULL CHECK (amount > 0),
    method            VARCHAR(30)    NOT NULL
        CHECK (method IN ('BANK_TRANSFER', 'CARD', 'MARKETPLACE_CREDIT')),
    status            VARCHAR(20)    NOT NULL
        CHECK (status IN ('PENDING', 'APPROVED', 'PAID', 'REJECTED')),
    authorized_by_id  BIGINT         REFERENCES app_user (id),
    requested_at      TIMESTAMP      NOT NULL,
    paid_at           TIMESTAMP
);

CREATE INDEX idx_product_seller ON product (seller_id);
CREATE INDEX idx_product_category ON product (category_id);
CREATE INDEX idx_product_status ON product (status);
CREATE INDEX idx_variant_product ON variant (product_id);
CREATE INDEX idx_address_buyer ON address (buyer_id);
CREATE INDEX idx_inventory_product ON inventory (product_id);
CREATE INDEX idx_inventory_warehouse ON inventory (warehouse_id);
CREATE INDEX idx_movement_inventory ON inventory_movement (inventory_id);
CREATE INDEX idx_movement_date ON inventory_movement (date);
CREATE INDEX idx_cart_buyer_active ON cart (buyer_id, active);
CREATE INDEX idx_cart_item_cart ON cart_item (cart_id);
CREATE INDEX idx_orders_buyer ON orders (buyer_id);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_orders_created ON orders (created_at);
CREATE INDEX idx_order_detail_order ON order_detail (order_id);
CREATE INDEX idx_order_detail_product ON order_detail (product_id);
CREATE INDEX idx_shipment_status ON shipment (status);
CREATE INDEX idx_return_order ON product_return (order_id);
CREATE INDEX idx_return_buyer ON product_return (buyer_id);
CREATE INDEX idx_return_detail_return ON product_return_detail (return_id);
CREATE INDEX idx_return_detail_order_detail ON product_return_detail (order_detail_id);
