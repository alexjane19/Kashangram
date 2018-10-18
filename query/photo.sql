CREATE TABLE photo
                (
                  userid character varying(50),
                  photoid character varying(20) NOT NULL,
                  writing text,
                  accesslevel BOOLEAN,
                  date TIMESTAMP,
                  CONSTRAINT photo_pkey PRIMARY KEY (photoid),
                  CONSTRAINT profilephoto_fkey FOREIGN KEY (userid)
                      REFERENCES profile (userid)
                      ON UPDATE NO ACTION ON DELETE NO ACTION
                )
