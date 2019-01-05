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
const USERS = "users";
const CONTACTS = "contacts";
const REMINDER = "reminder";

/************************************************************************
 * REST Methodes
 ************************************************************************/

/************************************************************************
 * Users
 ************************************************************************/

/**
 * GET all users
 */
router.get('/', function (req, res) {
    getCollectionAsJSON(USERS).then(result => res.json(result))
});

/**
 * GET one user
 */
router.get('/:uid', function (req, res) {
    let userID = req.params.uid;
    getDokumentAsJSON(USERS,userID).then(result => res.json(result));
});

// TODO POST funktioniert nicht
/**
 * POST one user
 * NOT NECESSARY BECAUSE WE CREATE A USER WHEN ADDING FIRST CONTACT
 */
router.post('/', function (req, res) {
    let userID = req.body;

    db.collection(USERS).doc(userID).collection(CONTACTS);
});

/************************************************************************
 * Contacts
 ************************************************************************/

/**
 * GET all contacts of a user
 */
router.get('/:uid/contacts' ,function (req, res) {
    let userID = req.params.uid;

    getCollectionAsJSON(USERS + '/' + userID + '/' + CONTACTS).then(result => res.json(result));
});

/**
 * GET one contact of a user
 */
router.get('/:uid/contacts/:cid' ,function (req, res) {
    let userID = req.params.uid;
    let contactID = req.params.cid;

    getDokumentAsJSON(USERS + '/' + userID + '/' + CONTACTS, contactID).then(result => res.json(result));
});

/**
 * POST one contact of a user
 */
router.post('/:uid/contacts', function (req, res) {
    let contact = req.body;
    let contactID = contact.contact;
    let userID = req.params.uid;

    db.collection(USERS).doc(userID).collection(CONTACTS).doc(contactID).set(contact);

});

/************************************************************************
 * Reminder
 ************************************************************************/

/**
 * GET all reminder for one contact of a user
 */
router.get('/:uid/contacts/:cid/reminder' ,function (req, res) {
    let userID = req.params.uid;
    let contactID = req.params.cid;

    getCollectionAsJSON(USERS + '/' + userID + '/' + CONTACTS + '/' + contactID + '/' + REMINDER).then(result => res.json(result));
});



/************************************************************************
 * Functions
 * Used from WBA2 Project
 * https://github.com/Fasust/WBA2SS18FaustTissenSchoemaker
 ************************************************************************/

/**
 * Returns a unigue ID in a specific collection
 * @param collectionName name of the collection that a id is to be generated for
 * @returns int id unique ID
 */
getIdInCollection = function(collectionName) {
    let ref = db.collection(collectionName).doc();
    let id = ref.id;

    return id;
}
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
/**
 * Returns a Promise that is to be resolved as a JSON and represents a specific document in a collection (GET)
 * @param collectionName name of the collection
 * @param docName name of the documeten
 * @returns {Promise<JSON>} Promise that resolves as JSON
 */
getDokumentAsJSON = function(collectionName,docName) {
    return new Promise(function (resolve) {
        let json = {};

        let document = db.collection(collectionName).doc(docName);
        document.get()
            .then(doc => {
                json = doc.data();

            }).then(function () {
            resolve(json);
        });
    });
};
/**
 * Returns a Promise that is to be resolved as a boolean that shows us if a ID exists in a collection
 * @param collectionName name of the collection
 * @param docName name of the document
 * @returns {Promise<boolean>} Promise that resolves as bool
 */
checkIfDocInCollection = function(collectionName, docName) {
    return new Promise(function (resolve) {
        // Test for the existence of certain keys within a DataSnapshot;
        db.collection(collectionName).doc(docName).get()
            .then(function(snapshot) {
                let idExists = snapshot.exists;
                resolve(idExists);
            });
    });
};

//Export as Module
module.exports = router;