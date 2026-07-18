ALTER TABLE meal_analysis
    ADD COLUMN carbohydrate_g DECIMAL(8, 2) NULL AFTER kcal,
    ADD COLUMN protein_g      DECIMAL(8, 2) NULL AFTER carbohydrate_g,
    ADD COLUMN fat_g          DECIMAL(8, 2) NULL AFTER protein_g;
