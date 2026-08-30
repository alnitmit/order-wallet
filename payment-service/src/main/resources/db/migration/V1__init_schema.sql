CREATE TABLE wallets (
                         user_id BIGINT PRIMARY KEY,
                         balance DECIMAL(15, 2) NOT NULL
);

CREATE TABLE transactions (
                              id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
                              order_id BIGINT NOT NULL,
                              user_id BIGINT NOT NULL,
                              amount DECIMAL(15, 2) NOT NULL,
                              status VARCHAR(50) NOT NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);