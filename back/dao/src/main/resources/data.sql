INSERT INTO PARAM (ID, NAME, VALUE)
SELECT nextval('SEQ_PARAM'), 'registration_enable', 'false'
WHERE NOT EXISTS (SELECT 1
                  FROM PARAM
                  WHERE NAME = 'registration_enable');

COMMIT;
