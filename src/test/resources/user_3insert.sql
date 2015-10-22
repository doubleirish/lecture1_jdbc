INSERT INTO ADDRESS (ID,   ZIP,      STATE,  CITY,        STREET )
             values (1,   '98052',  'WA',   'Seattle',    '9999 Belview Ave');

INSERT INTO ADDRESS (ID,   ZIP,      STATE,  CITY,        STREET )
             values (2,    '98034',  'WA',   'Kirkland',    '123 Main St');


INSERT INTO USERS (USER_NAME,   FIRST_NAME, LAST_NAME,  ACTIVE_SINCE, ADDRESS_ID)
            values('credmond', 'Conor'  , 'Redmond', '2014-12-31',  1);

INSERT INTO USERS (USER_NAME,   FIRST_NAME, LAST_NAME,  ACTIVE_SINCE, ADDRESS_ID)
            values('jsmith', 'John'  ,    'Smith',   '2014-02-28',  2 );


INSERT INTO USERS (USER_NAME,   FIRST_NAME, LAST_NAME,  ACTIVE_SINCE)
            values('pdiddy', 'Puffy'  ,    'Combs',   '2014-07-04');                  


INSERT INTO PHONE (USER_ID,   LABEL,      PHONE )
           values (1,         'HOME',     '123-555-6789' );  
              
INSERT INTO PHONE (USER_ID,   LABEL,      PHONE )
           values (1,         'CELL',     '555-555-1212' );  
