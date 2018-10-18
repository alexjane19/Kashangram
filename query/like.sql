CREATE TABLE likes
                (
                  photoid character varying(20),
                  userid character varying(50),
                  date timestamp,
                  CONSTRAINT photo_pkey_like PRIMARY KEY (photoid,userid),
                  CONSTRAINT photolike_fkey FOREIGN KEY (photoid)
                      REFERENCES photo (photoid)
                      ON UPDATE NO ACTION ON DELETE NO ACTION
)
