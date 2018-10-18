var express = require('express');
var fs = require('fs');
var crypto = require('crypto');
var DatabaseHandler = require('../routes/database_handler');
var router = express.Router();

router.post('/api/login', function(req, res, next) {
    var db_handler = new DatabaseHandler();
    exports.db_handler = db_handler;
    var hashpwd = crypto.createHash('sha256').update(req.param("password")).digest('base64');
    var query = {userid: req.param("userid"), password: hashpwd};
    db_handler.selectProfile(query, function (callback) {
        res.json(callback);
    });
});

router.post('/api/register', function(req, res, next) {
    var db_handler = new DatabaseHandler();
    exports.db_handler = db_handler;
    var hashpwd = crypto.createHash('sha256').update(req.param("password")).digest('base64');
    var query = {userid: req.param("userid"), password: hashpwd , email: req.param("email") ,stno: req.param("stno")};
    db_handler.registerProfile(query,function (callback) {
        res.json(callback);
    });
});

router.post('/api/register/complete', function(req, res, next) {
    var db_handler = new DatabaseHandler();
    exports.db_handler = db_handler;
    var query = {userid: req.param("userid"), fname: req.param("fname"), lname: req.param("lname"), phonenumber: req.param("phonenumber") ,photoid: req.param("photoid")};
    db_handler.updateProfile(query,function (callback) {
        res.json(callback);
    });
});

router.post('/api/upload', function(req, res) {
    var d = new Date();
    var text = makeRandomCharacters(7) + d.getTime();
    console.log(req.files.image.originalFilename);
    // console.log(req.files.image.path);
    console.log(text);
    fs.readFile(req.files.image.path, function (err, data){
        var dirname = "./uploads";
        var newPath = dirname + "/image/" + text + ".jpg";    //req.files.image.originalFilename;
        fs.writeFile(newPath, data, function (err) {
            if(err){
                res.json({'success': false, 'msg': "Error"});
            }else {
                res.json({'success': true, 'msg':"Saved", 'photoid': text});
            }
        });
    });
});

router.get('/api/image/:file', function (req, res){
    // file = req.params.file;
    // var options = {
    //     root: 'uploads/image/',
    //     dotfiles: 'allow', // allow dot in file name
    // };
    // res.sendFile(file, options, function (err) {
    //     if (err) {
    //         console.log(err);
    //         res.status(err.status).end();
    //     }else {
    //         console.log('Sent:', file);
    //     }
    // });

    file = req.params.file;
    console.log("getfile: " + file);

    var img = fs.readFileSync('uploads/image/' + file);
    console.log("getfile: img");

    res.writeHead(200, {'Content-Type': 'image/jpg' });
    console.log("getfile: writeHead");

    res.end(img, 'binary');
    console.log("getfile: end");


    //  var img = fs.readFileSync(dirname + "/image/" + file);
   //  res.writeHead(200, {'Content-Type': 'image/jpg' });
   // // res.setHeader('Content-Length', img.length);
   //  res.write(img, 'binary');
    //res.end(img, 'binary');

});

router.post('/api/photo/num', function(req, res, next) {
    var db_handler = new DatabaseHandler();
    exports.db_handler = db_handler;
    var query = {userid: req.param("userid")};
    db_handler.selectNumPhotos(query, function (callback) {
        res.json(callback);
    });
});

router.post('/api/follow/num', function(req, res, next) {
    var db_handler = new DatabaseHandler();
    exports.db_handler = db_handler;
    var query = {userid: req.param("userid")};
    db_handler.selectNumFollowing(query, function (callback) {
        var num_following = callback.data[0];
        console.log(callback);
        if(callback.success){
            db_handler.selectNumFollower(query, function (callback) {
                console.log(callback);
                if(callback.success){
                    var num_follower = callback.data[0];
                    res.json({success: true , msg: "number follows successful.",
                        data: {following: num_following.count , follower: num_follower.count}});
                }else {
                    res.json({success: false , msg: "number follows failed!", data: []});
                }
            });
        }else{
            res.json({success: false , msg: "number follows failed!", data: []});
        }
    });
});


router.post('/api/photo', function(req, res, next) {
    var db_handler = new DatabaseHandler();
    exports.db_handler = db_handler;
    var query = {userid: req.param("userid")};
    db_handler.selectPhotos(query, function (callback) {
        res.json(callback);
    });
});

router.post('/api/feed', function(req, res, next) {
    var db_handler = new DatabaseHandler();
    exports.db_handler = db_handler;
    var query = {userid: req.param("userid")};
    db_handler.showFeed(query, function (callback) {
        res.json(callback);
    });
});

router.post('/api/post', function(req, res, next) {
    var db_handler = new DatabaseHandler();
    exports.db_handler = db_handler;
    var query = {userid: req.param("userid"), photoid: req.param("photoid") , writing: req.param("writing") ,accesslevel: req.param("accesslevel"), picture: req.param("picture")};
    console.log(query)
    db_handler.insertPhoto(query,function (callback) {
        res.json(callback);
    });
});



function makeRandomCharacters(limit)
{
    var text = "";
    var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    for( var i=0; i < limit; i++ )
        text += possible.charAt(Math.floor(Math.random() * possible.length));

    return text;
}

module.exports = router;
