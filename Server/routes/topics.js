// EISWS1819MayerSchoemaker - Server with node.js

/*********************************************************************************************************
 * Inits & Modules
 *********************************************************************************************************/

// Init Firestore
const admin = require("firebase-admin");
const db = admin.firestore();

// Init Express
const express = require('express');
const router = express.Router(null);

// Init Routes
const TOPICS = "topics";

/*********************************************************************************************************
 * REST Methodes
 *********************************************************************************************************/

/*********************************************************************************************************
 * Topics
 *********************************************************************************************************/

/**
 * GET all topics
 */
router.get('/', function (req, res) {
    getCollection(TOPICS).then(result => res.json(result))
});


/*********************************************************************************************************
 * Helper Functions
 * Implemented to avoid redundancy
 * From Firebase 'Cloud Firestore' Documentation https://firebase.google.com/docs/firestore
 *********************************************************************************************************/

/**
 * Returns a Promise that represents a specific collection in a document
 */
getCollection =  function(col) {
    return new Promise(function (resolve) {
        let json = {};

        let collection = db.collection(col);
        collection.get()
            .then(snapshot => {
                snapshot.forEach(doc => {
                    json[doc.id] = doc.data();
                });
            }).then(function () {resolve(json);
        });
    });
};

//Export as Module
module.exports = router;