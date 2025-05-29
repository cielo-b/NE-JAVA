-- Initialize deductions with updated rates and UUID generation
INSERT INTO deduction (id, code, deduction_name, percentage) VALUES
                                                                 (gen_random_uuid(), 'TAX', 'TAX', 30.0),
                                                                 (gen_random_uuid(), 'PENSION', 'PENSION', 6.0),
                                                                 (gen_random_uuid(), 'MEDICAL', 'MEDICAL', 5.0),
                                                                 (gen_random_uuid(), 'HOUSE', 'HOUSE', 14.0),
                                                                 (gen_random_uuid(), 'TRANSPORT', 'TRANSPORT', 14.0),
                                                                 (gen_random_uuid(), 'OTHERS', 'OTHERS', 5.0)
ON CONFLICT (deduction_name) DO UPDATE
    SET percentage = EXCLUDED.percentage;