CREATE TABLE tb_subscription (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    subscription_id VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL, -- PAID, PENDING, CANCELED
    payment_method VARCHAR(50) NOT NULL, -- credit_card, pix
    current_period_end TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);