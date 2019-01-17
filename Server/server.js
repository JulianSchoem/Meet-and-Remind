/**
 * EISWS1819MayerSchoemaker
 * by Johanna Mayer & Julian Schoemaker
 * Server to System "Meet & Remind"
 *
 * Running on https://eisws1819mayerschoemaker.herokuapp.com/
 * (to push this subtree to heroku: git subtree push --prefix Server heroku master)
  */

/*********************************************************************************************************
 * Inits & Modules
 *********************************************************************************************************/

// Init Firestore/Firebase
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

// Init cron-job module for Server Applicationlogic
const schedule = require('node-schedule');

// Init Server Applicationlogic
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
            '<h3>Meet & Remind - Johanna Mayer und Julian Schoemaker</h3>' +
            '<a href="/users">List of users in Meet & Remind</a><br>' +
            '<a href="/topics">List of Topics that are available for reminders in Meet & Remind</a>'
        );
    });

    app.listen(settings.port, function(){
        console.log("Server ist nun auf Port " + settings.port + " verfügbar");
    });

    /**
     * WITH Cronjob for the Server Applicationlogic
     * Run it every minute
     * dont want to use it on Heroku because of the frequency we would have Heroku app online
     * would run every day on something like 23:59 with cron job in production
     */

    /**
    schedule.scheduleJob("* /1 * * * *", function() {
        // main function for Server Applicationlogic
        console.log("TEST SCHEDULE");
        applicationlogic.setMainTopic();
        console.log("------------------ FINISHED CRONJOB");
    });
    **/

    /**
     * WITHOUT Cronjob for the Server Applicationlogic
     * for debugging and presentation of the Applicationlogic
     * would run every day on something like 23:59 with cron job in production
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