CREATE TABLE subscriptions
(
    ai_subscription_id BIGINT      NOT NULL AUTO_INCREMENT,
    user_id            BIGINT      NOT NULL,
    plan_type          ENUM('MONTHLY','YEARLY') NOT NULL,
    price              BIGINT      NOT NULL,
    status             ENUM('ACTIVE','CANCELLED','EXPIRED') NOT NULL DEFAULT 'ACTIVE',
    started_at         DATETIME(6) NOT NULL,
    expired_at         DATETIME(6) NOT NULL,
    created_at         DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at         DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_subscriptions PRIMARY KEY (ai_subscription_id),
    CONSTRAINT fk_subscriptions_user FOREIGN KEY (user_id) REFERENCES users (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE payments
(
    payment_id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id            BIGINT       NOT NULL,
    pt_reservations_id BIGINT       NULL COMMENT 'PT 결제일 때만 연결',
    ai_subscription_id BIGINT       NULL COMMENT '구독 결제일 때만 연결',
    order_id           VARCHAR(50)  NOT NULL COMMENT '우리가 생성한 주문번호 (UUID)',
    portone_payment_id VARCHAR(50)  NULL COMMENT '포트원 발급 결제번호 (웹훅 transactionId)',
    amount             INT          NOT NULL,
    status             ENUM('PENDING','PAID','CANCELLED','FAILED') NOT NULL DEFAULT 'PENDING',
    product_type       ENUM('PT','SUBSCRIPTIONS') NOT NULL,
    paid_at            DATETIME(6)  NULL,
    cancelled_at       DATETIME(6)  NULL,
    failed_at          DATETIME(6)  NULL,
    fail_reason        VARCHAR(255) NULL,
    created_at         DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at         DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_payments PRIMARY KEY (payment_id),
    CONSTRAINT uk_payments_order_id UNIQUE (order_id),
    CONSTRAINT fk_payments_user FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_payments_pt_reservation FOREIGN KEY (pt_reservations_id) REFERENCES pt_reservations (pt_reservation_id),
    CONSTRAINT fk_payments_subscription FOREIGN KEY (ai_subscription_id) REFERENCES subscriptions (ai_subscription_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
