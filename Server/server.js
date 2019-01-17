// EISWS1819MayerSchoemaker by Johanna Mayer & Julian Schoemaker

// to push this subtree to heroku: git subtree push --prefix Server heroku master
// running on https://eisws1819mayerschoemaker.herokuapp.com/

/*********************************************************************************************************
 * Inits & Modules
 *********************************************************************************************************/

// Init Firestore
// TODO Firebase Key has to be deleted before making this Repo public
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

// Init cron-job module for Server Application Logic
const schedule = require('node-schedule');

// Init Server Application Logic (Serverseitige Anwendungslogik)
const applicationlogic = require('./applicationlogic');

/*********************************************************************************************************
 * Server Methodes
 *********************************************************************************************************/

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


    /**
     * Cronjob for the Serverseitige Anwendungslogik
     * Run it every minute (just for debugging and presentation of the Anwendungslogik)
     */
    /*
    schedule.scheduleJob("* /1 * * * *", function() {
        // main function for Serverseitige Anwendungslogik
        anwendungslogik.setMainTopic();
        console.log("------------------ FINISHED CRONJOB");
    });
    */

    /**
     * WITHOUT Cronjob for the Serverseitige Anwendungslogik
     * Because of frequence that would be scheduled on Heroku
     */
    applicationlogic.setMainTopic();

}

/**
 * Init Routes
 */

function initRoutes() {
    app.use('/users',usersRoute);
    app.use('/topics',topicsRoute);
}