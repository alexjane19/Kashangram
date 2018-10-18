CREATE TABLE follow
                (
                  userid character varying(50),
                  fuserid character varying(50),
                  date timestamp,
                  CONSTRAINT photo_pkey_follow PRIMARY KEY (userid,fuserid),
                  CONSTRAINT profilefollow_fkey FOREIGN KEY (userid)
                      REFERENCES profile (userid)
                      ON UPDATE NO ACTION ON DELETE NO ACTION,
                  CONSTRAINT profilefollow_fkey_1 FOREIGN KEY (fuserid)
                      REFERENCES profile (userid)
                      ON UPDATE NO ACTION ON DELETE NO ACTION
                )

