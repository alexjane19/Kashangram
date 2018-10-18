CREATE TABLE tag
                (
                  photoid character varying(20),
                  userid character varying(50),
                  date timestamp,
                  CONSTRAINT photo_pkey_tag PRIMARY KEY (photoid,userid),
                  CONSTRAINT phototag_fkey FOREIGN KEY (photoid)
                      REFERENCES photo (photoid)
                      ON UPDATE NO ACTION ON DELETE NO ACTION
                )
