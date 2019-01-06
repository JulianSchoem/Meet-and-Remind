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
    getDocumentAsJSON(USERS,userID).then(result => res.json(result));
});

/**
 * POST one user
 * NOT NECESSARY BECAUSE WE CREATE A USER WHEN ADDING FIRST CONTACT
 */
router.post('/', function (req, res) {
    // get data out of body and url
    let user = req.body;
    let userID = user.userID;

    // safe user into firebase
    db.collection(USERS).doc(userID).set({});

    // generate URI
    let userURI = req.protocol + '://' + req.get('host') + req.originalUrl + '/' + userID;

    // set URI and finish POST
    res.set('location', userURI);
    res.status(200);
    res.json(user);
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

    getDocumentAsJSON(USERS + '/' + userID + '/' + CONTACTS, contactID).then(result => res.json(result));
});

/**
 * POST one contact to a user
 */
router.post('/:uid/contacts', function (req, res) {
    // get data out of body and url
    let contact = req.body;
    let contactID = contact.contactID;
    let userID = req.params.uid;

    // safe contact into firebase
    db.collection(USERS).doc(userID).collection(CONTACTS).doc(contactID).set({});

    // generate URI
    let contactURI = req.protocol + '://' + req.get('host') + '/contacts/' + contactID;

    // set URI and finish POST
    res.set('location', contactURI);
    res.status(200);
    res.json(contact);
});

/**
 * POST name of contact to a user
 */
router.post('/:uid/contacts/:cid', function (req, res) {
    // get data out of body and url
    let name = req.body;
    let userID = req.params.uid;
    let contactID = req.params.cid;

    // safe contact into firebase
    db.collection(USERS).doc(userID).collection(CONTACTS).doc(contactID).set(name, {merge: true});

    // generate URI
    let contactURI = req.protocol + '://' + req.get('host') + '/contacts/' + contactID;

    // set URI and finish POST
    res.set('location', contactURI);
    res.status(200);
    res.json(name);
});

/**
 * POST topic of contact to a user
 */
router.post('/:uid/contacts/:cid', function (req, res) {
    // get data out of body and url
    let topic = req.body;
    let userID = req.params.uid;
    let contactID = req.params.cid;

    // safe contact into firebase
    db.collection(USERS).doc(userID).collection(CONTACTS).doc(contactID).set(topic, {merge: true});

    // generate URI
    let contactURI = req.protocol + '://' + req.get('host') + '/contacts/' + contactID;

    // set URI and finish POST
    res.set('location', contactURI);
    res.status(200);
    res.json(topic);
});

/************************************************************************
 * Reminder
 ************************************************************************/

/**
 * GET all reminder for one contact
 */
router.get('/:uid/contacts/:cid/reminder' ,function (req, res) {
    let userID = req.params.uid;
    let contactID = req.params.cid;

    getCollectionAsJSON(USERS + '/' + userID + '/' + CONTACTS + '/' + contactID + '/' + REMINDER).then(result => res.json(result));
});

/**
 * GET one reminder for one contact
 */
router.get('/:uid/contacts/:cid/reminder/:rid' ,function (req, res) {
    let userID = req.params.uid;
    let contactID = req.params.cid;
    let reminderID = req.params.rid;

    getDocumentAsJSON(USERS + '/' + userID + '/' + CONTACTS + '/' + contactID + '/' + REMINDER, reminderID).then(result => res.json(result));
});

/**
 * POST reminder to contact
 */
router.post('/:uid/contacts/:cid/reminder', function (req, res) {
    // get data out of body and url
    let reminder = req.body;
    let userID = req.params.uid;
    let contactID = req.params.cid;
    let reminderID = getIdInCollection(USERS + '/' + userID + '/' + CONTACTS + '/' + contactID  + '/' + REMINDER);

    // safe contact into firebase
    db.collection(USERS).doc(userID)
        .collection(CONTACTS).doc(contactID)
        .collection(REMINDER).doc(reminderID).set(reminder);

    // generate URI
    let reminderURI = req.protocol + '://' + req.get('host') + '/contacts/' + contactID + '/reminder/' + reminderID ;

    console.log('reminderID  ' + reminderID);

    // set URI and finish POST
    res.set('location', reminderURI);
    res.status(200);
    res.json(reminder);
});

/**
 * PUT reminder to contact (update on reminder)
 */
router.put('/:uid/contacts/:cid/reminder/:rid', function (req, res) {
    // get data out of body and url
    let reminder = req.body;
    let userID = req.params.uid;
    let contactID = req.params.cid;
    let reminderID = req.params.rid;

    // safe contact into firebase
    db.collection(USERS).doc(userID)
        .collection(CONTACTS).doc(contactID)
        .collection(REMINDER).doc(reminderID).set(reminder, {merge: true});

    // generate URI
    let reminderURI = req.protocol + '://' + req.get('host') + '/contacts/' + contactID + '/reminder/' + reminderID ;

    // set URI and finish POST
    res.set('location', reminderURI);
    res.status(200);
    res.json(reminder);
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
};
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
getDocumentAsJSON = function(collectionName,docName) {
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

//Export as Module
module.exports = router;