CREATE OR REPLACE FUNCTION Date2EnrollYear(dDate IN DATE)
    RETURN NUMBER
    IS
    vMonth NUMBER;
    vYear  NUMBER;
BEGIN
    vMonth := TO_NUMBER(TO_CHAR(dDate, 'MM'));
    vYear := TO_NUMBER(TO_CHAR(dDate, 'YYYY'));

    IF vMonth IN (11, 12) THEN
        RETURN vYear + 1;
    ELSE
        RETURN vYear;
    END IF;
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
    ELSE -- 11, 12월
        RETURN 2;
    END IF;
END;
/
