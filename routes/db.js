var express = require('express');
var pg = require('pg');
var path = require('path');
const connectionString = process.env.DATABASE_URL || 'postgres://postgres:110595@localhost:5432/kashangram';
var router = express.Router();

router.post('/', function(req, res, next) {
    const results = [];
// Grab data from http request
    //const data = {text: req.body.text, complete: false};
    const data = {text: req.param("text"), complete: false};
    console.log(req.body.text)

// Get a Postgres client from the connection pool
    pg.connect(connectionString, function(err, client, done) {
    // Handle connection errors
    if(err) {
        console.log("enter")
        done();
        console.log(err);
        return res.status(500).json({success: false, data: err});
    }
    // SQL Query > Insert Data
        console.log("before")

        client.query("INSERT INTO items(text, complete) values($1, $2)",
    [data.text, data.complete]);
        console.log("after")

// SQL Query > Select Data
const query = client.query('SELECT * FROM items ORDER BY id ASC');
// Stream results back one row at a time
query.on('row', function(row)  {
    results.push(row);
});
// After all data is returned, close connection and return results
query.on('end', function()  {
    done();
return res.json(results);
});
});
});

module.exports = router;
