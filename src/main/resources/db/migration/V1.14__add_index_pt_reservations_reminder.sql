CREATE INDEX idx_pt_reservations_reminder
    ON pt_reservations (status, reserved_start_at);
