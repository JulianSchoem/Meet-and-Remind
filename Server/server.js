// EISWS1819MayerSchoemaker - Server with node.js

/**
 * Init modules
 */

// Init Firestore
const admin = require("firebase-admin");
const serviceAccount = require("./eisws1819mayerschoemaker-firebase.json");
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://eisws1819mayerschoemaker.firebaseio.com"
});
const db = admin.firestore();

// Init Express
const express = require('express');
const app = express();
const settings = {
    port: process.env.PORT || 3000
};

// Init Route
const usersRoute = require('./routes/users');

/**
 * Main
 */

initServer();
initRoutes();

/**
 * Init Server
 */

function initServer() {

    app.get('/', function (req, res) {
        res.send('Hello World!');
    });

    app.listen(settings.port, function(){
        console.log("Dienstgeber ist nun auf Port " + settings.port+ " verfügbar");
    });

}

/**
 * Init Routes
 */

function initRoutes() {
    app.use('/users',usersRoute);
}