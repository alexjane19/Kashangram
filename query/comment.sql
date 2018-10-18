CREATE TABLE comment
                    (
                      userid character varying(50),
                      photoid character varying(20),
                      writing text,
                      date timestamp,
                      CONSTRAINT photo_pkey_comment PRIMARY KEY (photoid,userid),
                      CONSTRAINT photocomment_fkey FOREIGN KEY (photoid)
                          REFERENCES photo (photoid)
                          ON UPDATE NO ACTION ON DELETE NO ACTION
                    )
