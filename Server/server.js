// EISWS1819MayerSchoemaker by Johanna Mayer & Julian Schoemaker

// to push this subtree to heroku: git subtree push --prefix Server heroku master
// running on https://eisws1819mayerschoemaker.herokuapp.com/

/************************************************************************
 * Inits & Modules
 ************************************************************************/

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
const bodyParser = require('body-parser');
const settings = {
    port: process.env.PORT || 3000
};

// Init Route
const usersRoute = require('./routes/users');
const topicsRoute = require('./routes/topics');

/************************************************************************
 * Server Methodes
 ************************************************************************/

initServer();
initRoutes();

/**
 * Init Server
 */

function initServer() {
    app.use( bodyParser.json() );

    app.get('/', function (req, res) {
        res.send('<h1>Entwicklungsprojekt interaktive Systeme</h1>\n' +
            '<h2>Wintersemester 2018/2019 an der TH Köln</h2>\n' +
            '<h3>Meet & Remind - Johanna Mayer und Julian Schoemaker</h3>'
        );
    });

    app.listen(settings.port, function(){
        console.log("Dienstgeber ist nun auf Port " + settings.port + " verfügbar");
    });

}

/**
 * Init Routes
 */

function initRoutes() {
    app.use('/users',usersRoute);
    app.use('/topics',topicsRoute);
}