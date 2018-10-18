CREATE TABLE comment
(
    userid VARCHAR(50) NOT NULL,
    photoid VARCHAR(20) NOT NULL,
    writing TEXT,
    date TIMESTAMP,
    CONSTRAINT photo_pkey_comment PRIMARY KEY (photoid, userid),
    CONSTRAINT photocomment_fkey FOREIGN KEY (photoid) REFERENCES photo (photoid)
);
CREATE TABLE follow
(
    userid VARCHAR(50) NOT NULL,
    fuserid VARCHAR(50) NOT NULL,
    date TIMESTAMP DEFAULT now(),
    CONSTRAINT photo_pkey_follow PRIMARY KEY (userid, fuserid),
    CONSTRAINT profilefollow_fkey FOREIGN KEY (userid) REFERENCES profile (userid),
    CONSTRAINT profilefollow_fkey_1 FOREIGN KEY (fuserid) REFERENCES profile (userid)
);
CREATE TABLE likes
(
    photoid VARCHAR(20) NOT NULL,
    userid VARCHAR(50) NOT NULL,
    date TIMESTAMP,
    CONSTRAINT photo_pkey_like PRIMARY KEY (photoid, userid),
    CONSTRAINT photolike_fkey FOREIGN KEY (photoid) REFERENCES photo (photoid)
);
CREATE TABLE photo
(
    userid VARCHAR(50),
    photoid VARCHAR(20) PRIMARY KEY NOT NULL,
    writing TEXT,
    accesslevel BOOLEAN,
    date TIMESTAMP DEFAULT now(),
    picture BOOLEAN,
    CONSTRAINT profilephoto_fkey FOREIGN KEY (userid) REFERENCES profile (userid)
);
CREATE TABLE profile
(
    userid VARCHAR(50) PRIMARY KEY NOT NULL,
    fname VARCHAR(20),
    lname VARCHAR(20),
    email VARCHAR(80),
    password TEXT,
    phonenumber VARCHAR(14),
    stno VARCHAR(12),
    photoid VARCHAR(20),
    date TIMESTAMP DEFAULT now()
);
CREATE TABLE tag
(
    photoid VARCHAR(20) NOT NULL,
    userid VARCHAR(50) NOT NULL,
    date TIMESTAMP,
    CONSTRAINT photo_pkey_tag PRIMARY KEY (photoid, userid),
    CONSTRAINT phototag_fkey FOREIGN KEY (photoid) REFERENCES photo (photoid)
);


CREATE TABLE feed
(
    userfeed VARCHAR(50),
    userid VARCHAR(50),
    photoid VARCHAR(20),
    writing TEXT,
    accesslevel BOOLEAN,
    picture BOOLEAN,
    date TIMESTAMP,
    nlike BIGINT,
    liked VARCHAR
);

