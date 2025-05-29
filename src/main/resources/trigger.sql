-- Create trigger function
CREATE OR REPLACE FUNCTION generate_payment_message()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status = 'PAID' AND OLD.status = 'PENDING' THEN
        INSERT INTO messages (employee_code, message, month, year, sent_at, sent)
        VALUES (
            NEW.employee_code,
            'Dear ' || (SELECT firstname FROM employee WHERE code = NEW.employee_code) ||
            ', your salary payment for ' || NEW.month || '/' || NEW.year || ' has been processed.',
            NEW.month,
            NEW.year,
            NOW(),
            false
        );
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger
DROP TRIGGER IF EXISTS payment_message_trigger ON pay_slips;
CREATE TRIGGER payment_message_trigger
    AFTER UPDATE ON pay_slips
    FOR EACH ROW
    EXECUTE FUNCTION generate_payment_message(); 