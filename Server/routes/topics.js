// EISWS1819MayerSchoemaker - Server with node.js

/************************************************************************
 * Inits & Modules
 ************************************************************************/

// Init Firestore
const admin = require("firebase-admin");
const db = admin.firestore();

// Init Express
const express = require('express');
const router = express.Router(null);

// Init Routes
const TOPICS = "topics";

/************************************************************************
 * REST Methodes
 ************************************************************************/

/************************************************************************
 * Topics
 ************************************************************************/

/**
 * GET all topics
 */
router.get('/', function (req, res) {
    getCollectionAsJSON(TOPICS).then(result => res.json(result))
});


/************************************************************************
 * Functions
 * Used from WBA2 Project
 * https://github.com/Fasust/WBA2SS18FaustTissenSchoemaker
 ************************************************************************/

/**
 * Returns a Promise that is to be resolved as a JSON and represents a specific collection (GET)
 * @param collectionName naem of the collecetion
 * @returns {Promise<JSON>} Promise that resolves as JSON
 */
getCollectionAsJSON =  function(collectionName) {
    return new Promise(function (resolve) {
        let json = {};

        let collection = db.collection(collectionName);
        collection.get()
            .then(snapshot => {
                snapshot.forEach(doc => {

                    json[doc.id] = doc.data();
                });
            }).then(function () {
            resolve(json);
        });
    });
};

//Export as Module
module.exports = router;