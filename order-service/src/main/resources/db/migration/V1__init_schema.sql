CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        status VARCHAR(50) NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        user_id BIGINT NOT NULL
);

CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,
                             order_id BIGINT NOT NULL REFERENCES orders(id),
                             product_name VARCHAR(255) NOT NULL,
                             price DECIMAL(19, 2) NOT NULL,
                             quantity INT NOT NULL
);

CREATE TABLE outbox_messages (
                                 id UUID PRIMARY KEY,
                                 type VARCHAR(255) NOT NULL,
                                 payload JSONB NOT NULL,
                                 status VARCHAR(50) NOT NULL,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE inbox_messages (
                                event_id VARCHAR(255) PRIMARY KEY,
                                processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);