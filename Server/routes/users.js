// EISWS1819MayerSchoemaker - Server with node.js

/**
 * Init modules
 */

// Init Firestore
const admin = require("firebase-admin");
const db = admin.firestore();

// Init Express
const express = require('express');
const router = express.Router(null);

/**
 * REST Methods
 */

// Post user into firebase
router.get('/', function (req, res) {

});

//Update existing instance
router.post('/', function (req, res) {
    req.body.id = data.users.length;
    data.users.push(req.body);
    res.status(200).json({ uri: req.protocol+"://"+req.headers.host + "/users/" + req.body.id });
});

//Export as Module
module.exports = router;