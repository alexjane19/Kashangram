CREATE TABLE profile
                    (
                      userid character varying(50) NOT NULL,
                      fname character varying(20),
                      lname character varying(20),
                      email character varying(80),
                      password text,
                      phonenumber character varying(14),
                      stno character varying(12),
                      photoid character varying(20),
                      date timestamp,
                      CONSTRAINT profile_pkey PRIMARY KEY (userid)
                    )
