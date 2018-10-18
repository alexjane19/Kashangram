var pg = require('pg');
var path = require('path');

function DatabaseHandler () {
    this.connectionString = process.env.DATABASE_URL || 'postgres://postgres:110595@localhost:5432/kashangram';
    this.color = "red";
    this.getInfo = function() {
        return this.color + ' ' + this.type + ' apple';
    };

    this.insertProfile = function (data,callback) {
        pg.connect(this.connectionString, function(err, client, done) {
            // Handle connection errors
            if(err) {
                done();
                callback({success: false , msg: "fail"});
                return console.error(err.message);
            }
            client.query("INSERT INTO profile(userid, fname, lname, email, password, phonenumber, stno, photoid) values($1, $2, $3, $4, $5, $6, $7, $8)",
                [data.userid, data.fname, data.lname, data.email, data.password, data.phonenumber, data.stno, data.photoid], function(err, result) {
                    if (err) {
                        callback({success: false , msg: "register failed! username does exist"});
                        return console.error(err.message);

                    }
                    callback({success: true , msg: "register successful."});
                });

        });
    }
    this.registerProfile = function (data,callback) {
        pg.connect(this.connectionString, function(err, client, done) {
            // Handle connection errors
            if(err) {
                done();
                callback({success: false , msg: "fail"});
                return console.error(err.message);
            }
            client.query("INSERT INTO profile(userid, email, password, stno) values($1, $2, $3, $4)",
                [data.userid, data.email, data.password, data.stno], function(err, result) {
                    if (err) {
                        callback({success: false , msg: "register failed! username does exist"});
                        return console.error(err.message);

                    }
                    callback({success: true , msg: "register successful."});
                });

        });
    }
    this.updateProfile = function (data,callback) {
        pg.connect(this.connectionString, function(err, client, done) {

            if(err) {
                done();
                callback({success: false , msg: "fail"});
                return console.error(err.message);
            }
            client.query("Update profile Set fname=$1, lname=$2, phonenumber=$3, photoid=$4 where userid=$5",
                [data.fname, data.lname, data.phonenumber, data.photoid, data.userid], function(err, result) {
                    if (err) {
                        callback({success: false , msg: "update failed! username does not exist"});
                        return console.error(err.message);

                    }
                    callback({success: true , msg: "update successful."});
                });

        });
    }

    this.selectProfile = function (data,callback) {
        pg.connect(this.connectionString, function(err, client, done) {
            // Handle connection errors
            if(err) {
                done();
                callback({success: false , msg: "fail"});
                return console.error(err.message);
            }

            client.query('SELECT * FROM profile where userid = $1 and password=$2',
            [data.userid, data.password],function (err,result) {
                    if(err){
                    callback({success: false, msg: "login failed! database unavailable." , data: []})
                        return console.error(err.message);
                    }
                    if(result.rowCount> 0){
                        callback({success: true, msg: "login successful.", data: result.rows})
                        return true;}
                    else{
                        callback({success: false, msg: "login failed! username and password is wrong." , data: result.rows})
                        return false;}

                });
        });
    }

    this.selectNumPhotos = function (data,callback) {
        pg.connect(this.connectionString, function(err, client, done) {
            // Handle connection errors
            if(err) {
                done();
                callback({success: false , msg: "fail"});
                return console.error(err.message);
            }

            client.query('SELECT count(photoid) FROM photo where userid = $1',
                [data.userid],function (err,result) {
                    if(err){
                        callback({success: false, msg: "request number photos failed! database unavailable." , data: []})
                        return console.error(err.message);
                    }
                        callback({success: true, msg: "request number photos successful.", data: result.rows})
                        return true;
                });
        });
    }

    this.selectNumFollowing = function (data,callback) {
        pg.connect(this.connectionString, function(err, client, done) {
            // Handle connection errors
            if(err) {
                done();
                callback({success: false , msg: "fail"});
                return console.error(err.message);
            }

            client.query('SELECT count(*) FROM follow where userid = $1',
                [data.userid],function (err,result) {
                    if(err){
                        callback({success: false, msg: "request number following failed! database unavailable." , data: []})
                        return console.error(err.message);
                    }
                    callback({success: true, msg: "request number following successful.", data: result.rows})
                    return true;
                });
        });
    }
    this.selectNumFollower = function (data,callback) {
        pg.connect(this.connectionString, function(err, client, done) {
            // Handle connection errors
            if(err) {
                done();
                callback({success: false , msg: "fail"});
                return console.error(err.message);
            }

            client.query('SELECT count(*) FROM follow where fuserid = $1',
                [data.userid],function (err,result) {
                    if(err){
                        callback({success: false, msg: "request number follower failed! database unavailable." , data: []})
                        return console.error(err.message);
                    }
                    callback({success: true, msg: "request number follower successful.", data: result.rows})
                    return true;
                });
        });
    }


    this.selectPhotos = function (data,callback) {
        pg.connect(this.connectionString, function(err, client, done) {
            // Handle connection errors
            if(err) {
                done();
                callback({success: false , msg: "fail"});
                return console.error(err.message);
            }

            client.query('SELECT * FROM photo where userid = $1 order by date desc',
                [data.userid],function (err,result) {
                    if(err){
                        callback({success: false, msg: "get photos failed! database unavailable." , data: []})
                        return console.error(err.message);
                    }
                    callback({success: true, msg: "get photos successful.", data: result.rows})
                    return true;
                });
        });
    }

    this.showFeed = function (data,callback) {
        pg.connect(this.connectionString, function(err, client, done) {
            // Handle connection errors
            if(err) {
                done();
                callback({success: false , msg: "fail"});
                return console.error(err.message);
            }

            client.query("SELECT feed.userfeed, feed.userid, feed.photoid, feed.writing, feed.accesslevel, feed.picture, feed.date, feed.nlike, " +
                "feed.liked, profile.photoid as prophoto FROM feed JOIN profile on feed.userid = profile.userid " +
                "WHERE userfeed = $1 and (liked = $1 or liked = '')",
                [data.userid],function (err,result) {
                    if(err){
                        callback({success: false, msg: "get feed failed! database unavailable." , data: []})
                        return console.error(err.message);
                    }
                    callback({success: true, msg: "get feed successful.", data: result.rows})
                    return true;
                });
        });
    }

    this.insertPhoto = function (data,callback) {
        pg.connect(this.connectionString, function(err, client, done) {
            // Handle connection errors
            if(err) {
                done();
                callback({success: false , msg: "fail"});
                return console.error(err.message);
            }
            client.query("INSERT INTO photo(userid, photoid, writing, accesslevel, picture) values($1, $2, $3, $4, $5)",
                [data.userid, data.photoid, data.writing, data.accesslevel, data.picture], function(err, result) {
                    if (err) {
                        callback({success: false , msg: "insert photo failed! photoid does exist"});
                        return console.error(err.message);

                    }
                    callback({success: true , msg: "insert photo successful."});
                });

        });
    }
}


module.exports = DatabaseHandler;