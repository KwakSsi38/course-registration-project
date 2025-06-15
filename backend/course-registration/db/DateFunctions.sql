CREATE OR REPLACE FUNCTION Date2EnrollYear(dDate IN DATE)
    RETURN NUMBER
    IS
    vYear  NUMBER;
BEGIN
    vYear := TO_NUMBER(TO_CHAR(dDate, 'YYYY'));
    RETURN vYear;
END;
/


CREATE OR REPLACE FUNCTION Date2EnrollSemester(dDate IN DATE)
    RETURN NUMBER
    IS
    vMonth NUMBER;
BEGIN
    vMonth := TO_NUMBER(TO_CHAR(dDate, 'MM'));

    IF vMonth BETWEEN 3 AND 8 THEN
        RETURN 1;
    ELSIF vMonth BETWEEN 9 AND 12 THEN
        RETURN 2;
    ELSE -- 1, 2
        RETURN 2;
    END IF;
END;
/
