INSERT INTO PARAM (ID, NAME, VALUE, COMMENT)
SELECT nextval('SEQ_PARAM'),
       'registration_enable',
       'false',
       'Доступна ли регистрация, true - доступна, false - недоступна'
WHERE NOT EXISTS (SELECT 1
                  FROM PARAM
                  WHERE NAME = 'registration_enable');

INSERT INTO PARAM (ID, NAME, VALUE, COMMENT)
SELECT nextval('SEQ_PARAM'),
       'parsing_relative_list',
       'брат мужа, муж, отец, брат, племянник, дядя, отчим, дед, мать, свекр, брат двоюродный, брат сводный, сестра близнец, дядя по мужу',
       'Список отношений через запятую, на которые будет проверятся relative при парсинге для разделения строки, например, ' ||
       'если будет присутствовать "отец", то "Елена Ивановна отец Иван Петрович" поделится на "Елена Ивановна" и "отец Иван Петрович"'
WHERE NOT EXISTS (SELECT 1
                  FROM PARAM
                  WHERE NAME = 'parsing_relative_list');

COMMIT;
