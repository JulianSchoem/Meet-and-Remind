/*********************************************************************************************************
 * Inits & Modules
 *********************************************************************************************************/

// Init Firestore/Firebase
const admin = require("firebase-admin");
const db = admin.firestore();

// Init Express
const express = require('express');
const router = express.Router(null);

// Init jsonschema for JSON Schema validation
const Validator = require('jsonschema').Validator;
const v = new Validator();
const fs = require("fs");
const schema = JSON.parse(fs.readFileSync('./json-schema.json','utf8'));

// Init Routes
const USERS = "users";
const CONTACTS = "contacts";
const REMINDER = "reminder";

/*********************************************************************************************************
 * REST Methodes
 *********************************************************************************************************/

/*********************************************************************************************************
 * Users
 *********************************************************************************************************/

/**
 * GET all users
 */
router.get('/', function (req, res) {
    getCollection(USERS).then(result => res.json(result))
});

/**
 * GET one user
 */
router.get('/:uid', function (req, res) {
    let userID = req.params.uid;
    getDocument(USERS,userID).then(result => res.json(result));
});

/**
 * POST one user
 * Not currently necessary because we create an user when adding first contact
 */
router.post('/', function (req, res) {
    // get data out of body and url
    let user = req.body;
    let userID = user.userID;

    // Error handler
    if(req.body == {}) {
        res.status(400).send('Missing Body in this POST!');
        return;
    }

    // safe user into firebase
    db.collection(USERS).doc(userID).set({});

    // generate URI
    let contactURI = req.protocol + '://' + req.get('host') + "/users/" + userID;

    // set URI and finish POST
    res.set('location', userURI);
    res.status(201);
    res.json(user);
});

/*********************************************************************************************************
 * Contacts
 *********************************************************************************************************/

/**
 * GET all contacts of a user
 */
router.get('/:uid/contacts' ,function (req, res) {
    let userID = req.params.uid;

    getCollection(USERS + '/' + userID + '/' + CONTACTS).then(result => res.json(result));
});

/**
 * GET one contact of a user
 */
router.get('/:uid/contacts/:cid' ,function (req, res) {
    let userID = req.params.uid;
    let contactID = req.params.cid;

    getDocument(USERS + '/' + userID + '/' + CONTACTS, contactID).then(result => res.json(result));
});

/**
 * POST one contact to a user
 */
router.post('/:uid/contacts', function (req, res) {
    // get data out of body and url
    let contact = req.body;
    let contactID = contact.contactID;
    let userID = req.params.uid;

    // Error handler
    if(req.body == {}) {
        res.status(400).send('Missing Body in this POST!');
        return;
    }

    // safe contact into firebase
    db.collection(USERS).doc(userID).collection(CONTACTS).doc(contactID).set({});

    // generate URI
    let contactURI = req.protocol + '://' + req.get('host') + "/users/" + userID +  '/contacts/' + contactID;

    // set URI and finish POST
    res.set('location', contactURI);
    res.status(201);
    res.json(contact);
});

/**
 * POST field 'name' of contact to a user
 */
router.post('/:uid/contacts/:cid', function (req, res) {
    // get data out of body and url
    let name = req.body;
    let userID = req.params.uid;
    let contactID = req.params.cid;

    // Error handler
    if(req.body == {}) {
        res.status(400).send('Missing Body in this POST!');
        return;
    }

    if(!req.body.hasOwnProperty('name')){
        res.status(400).send('Missing Variable in Body of this POST!');
        return;
    }

    // safe name into firebase
    db.collection(USERS).doc(userID).collection(CONTACTS).doc(contactID).set(name, {merge: true});

    // generate URI
    let contactURI = req.protocol + '://' + req.get('host') + "/users/" + userID +  '/contacts/' + contactID;

    // set URI and finish POST
    res.set('location', contactURI);
    res.status(201);
    res.json(name);
});

/**
 * PUT field 'name' of contact to a user (update)
 */
router.put('/:uid/contacts/:cid', function (req, res) {
    // get data out of body and url
    let name = req.body;
    let userID = req.params.uid;
    let contactID = req.params.cid;

    // Error handler
    if(req.body == {}) {
        res.status(400).send('Missing Body in this PUT!');
        return;
    }

    if(!req.body.hasOwnProperty('name')){
        res.status(400).send('Missing Variable in Body of this PUT!');
        return;
    }

    // safe name into firebase
    db.collection(USERS).doc(userID).collection(CONTACTS).doc(contactID).update(name);

    // generate URI
    let contactURI = req.protocol + '://' + req.get('host') + "/users/" + userID +  '/contacts/' + contactID;

    // set URI and finish POST
    res.set('location', contactURI);
    res.status(200);
    res.json(name);
});

/*********************************************************************************************************
 * Reminder
 *********************************************************************************************************/

/**
 * GET all reminder for one contact
 */
router.get('/:uid/contacts/:cid/reminder' ,function (req, res) {
    // get data out of body and url
    let userID = req.params.uid;
    let contactID = req.params.cid;

    getCollection(USERS + '/' + userID + '/' + CONTACTS + '/' + contactID + '/' + REMINDER).then(result => res.json(result));
});

/**
 * GET one reminder for one contact
 * GET priority of one reminder to increase it in Client Applicationlogic
 */
router.get('/:uid/contacts/:cid/reminder/:rid' ,function (req, res) {
    // get data out of body and url
    let userID = req.params.uid;
    let contactID = req.params.cid;
    let reminderID = req.params.rid;

    getDocument(USERS + '/' + userID + '/' + CONTACTS + '/' + contactID + '/' + REMINDER, reminderID).then(result => res.json(result));
});

/**
 * POST reminder to contact
 */
router.post('/:uid/contacts/:cid/reminder', function (req, res) {
    // get data out of body and url
    let reminder = req.body;
    let userID = req.params.uid;
    let contactID = req.params.cid;
    let reminderID = getCollectionId(USERS + '/' + userID + '/' + CONTACTS + '/' + contactID  + '/' + REMINDER);

    // Error handler
    if(req.body == {}) {
        res.status(400).send('Missing Body in this POST!');
        return;
    }

    if(!req.body.hasOwnProperty('title')){
        res.status(400).send('Missing Variable in Body of this POST!');
        return;
    }

    if(!req.body.hasOwnProperty('description')){
        res.status(400).send('Missing Variable in Body of this POST!');
        return;
    }

    // JSON Schema Validation - start
    if ( v.validate(req.body, schema).errors.length > 0 ) {
        res.status(400).send("JSON Schema Validation failed on reminder POST");
        return;
    }
    // JSON Schema Validation - end

    // safe reminder into firebase
    db.collection(USERS).doc(userID)
        .collection(CONTACTS).doc(contactID)
        .collection(REMINDER).doc(reminderID).set(reminder);

    // generate URI
    let reminderURI = req.protocol + '://' + req.get('host') + "/users/" + userID +  '/contacts/' + contactID + '/reminder/' + reminderID ;

    // set URI and finish POST
    res.set('location', reminderURI);
    res.status(201);
    res.json(reminder);
});

/**
 * PUT one reminder of a contact (update on reminder)
 * PUT priority of one reminder after increase in Client Applicationlogic
 */
router.put('/:uid/contacts/:cid/reminder/:rid', function (req, res) {
    // get data out of body and url
    let reminder = req.body;
    let userID = req.params.uid;
    let contactID = req.params.cid;
    let reminderID = req.params.rid;

    // Error handler
    if(req.body == {}) {
        res.status(400).send('Missing Body in this PUT!');
        return;
    }

    if(!req.body.hasOwnProperty('title')){
        res.status(400).send('Missing Variable in Body of this PUT!');
        return;
    }

    if(!req.body.hasOwnProperty('description')){
        res.status(400).send('Missing Variable in Body of this PUT!');
        return;
    }

    // safe reminder into firebase
    db.collection(USERS).doc(userID)
        .collection(CONTACTS).doc(contactID)
        .collection(REMINDER).doc(reminderID).update(reminder);

    // generate URI
    let reminderURI = req.protocol + '://' + req.get('host') + "/users/" + userID +  '/contacts/' + contactID + '/reminder/' + reminderID ;

    // set URI and finish POST
    res.set('location', reminderURI);
    res.status(200);
    res.json(reminder);
});

/**
 * DELETE one reminder of a contact
 */
router.delete('/:uid/contacts/:cid/reminder/:rid', function (req, res) {
    // get data out of body and url
    let userID = req.params.uid;
    let contactID = req.params.cid;
    let reminderID = req.params.rid;

    db.collection(USERS).doc(userID).collection(CONTACTS).doc(contactID).collection(REMINDER).doc(reminderID).delete();
    res.status(200);
    res.send('Deleted: ' + reminderID);
});

/*********************************************************************************************************
 * Helper Functions
 * Implemented to avoid redundancy
 * From Firebase 'Cloud Firestore' Documentation https://firebase.google.com/docs/firestore
 *********************************************************************************************************/

/**
 * Returns the ID of a document in a collection
 */
getCollectionId = function(col) {
    return db.collection(col).doc().id;
};

/**
 * Returns a Promise that represents one collection in a document
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

/**
 * Returns a Promise that represents one document in a collection
 */
getDocument = function(col,doc) {
    return new Promise(function (resolve) {
        let json = {};

        let document = db.collection(col).doc(doc);
        document.get()
            .then(doc => {
                json = doc.data();
            }).then(function () {resolve(json);
        });
    });
};

// export function for server.js
module.exports = router;