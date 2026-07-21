ALTER TABLE trainer_reviews DROP FOREIGN KEY fk_trainer_reviews_reservation;
ALTER TABLE trainer_reviews DROP INDEX uk_trainer_reviews_reservation;
ALTER TABLE trainer_reviews DROP COLUMN pt_reservation_id;
