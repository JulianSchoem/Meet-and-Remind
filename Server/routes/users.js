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
    res.status(201);
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

    // safe contact into firebase
    db.collection(USERS).doc(userID).collection(CONTACTS).doc(contactID).set({});

    // generate URI
    let contactURI = req.protocol + '://' + req.get('host') + '/contacts/' + contactID;

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

    // safe name into firebase
    db.collection(USERS).doc(userID).collection(CONTACTS).doc(contactID).set(name, {merge: true});

    // generate URI
    let contactURI = req.protocol + '://' + req.get('host') + '/contacts/' + contactID;

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

    // safe name into firebase
    db.collection(USERS).doc(userID).collection(CONTACTS).doc(contactID).update(name);

    // generate URI
    let contactURI = req.protocol + '://' + req.get('host') + '/contacts/' + contactID;

    // set URI and finish POST
    res.set('location', contactURI);
    res.status(200);
    res.json(name);
});

/**
 * POST field 'topic' of contact to a user
 */
router.post('/:uid/contacts/:cid', function (req, res) {
    // get data out of body and url
    let topic = req.body;
    let userID = req.params.uid;
    let contactID = req.params.cid;

    // safe topic into firebase
    db.collection(USERS).doc(userID).collection(CONTACTS).doc(contactID).set(topic, {merge: true});

    // generate URI
    let contactURI = req.protocol + '://' + req.get('host') + '/contacts/' + contactID;

    // set URI and finish POST
    res.set('location', contactURI);
    res.status(201);
    res.json(topic);
});

/************************************************************************
 * Reminder
 ************************************************************************/

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
 * GET priority of one reminder to increase it in 'Clientseitige Anwendungslogik'
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

    // safe reminder into firebase
    db.collection(USERS).doc(userID)
        .collection(CONTACTS).doc(contactID)
        .collection(REMINDER).doc(reminderID).set(reminder);

    // generate URI
    let reminderURI = req.protocol + '://' + req.get('host') + '/contacts/' + contactID + '/reminder/' + reminderID ;

    // set URI and finish POST
    res.set('location', reminderURI);
    res.status(201);
    res.json(reminder);
});

/**
 * PUT one reminder of a contact (update on reminder)
 * PUT priority of one reminder after increase in 'Clientseitige Anwendungslogik'
 */
router.put('/:uid/contacts/:cid/reminder/:rid', function (req, res) {
    // get data out of body and url
    let reminder = req.body;
    let userID = req.params.uid;
    let contactID = req.params.cid;
    let reminderID = req.params.rid;

    // safe reminder into firebase
    db.collection(USERS).doc(userID)
        .collection(CONTACTS).doc(contactID)
        .collection(REMINDER).doc(reminderID).update(reminder);

    // generate URI
    let reminderURI = req.protocol + '://' + req.get('host') + '/contacts/' + contactID + '/reminder/' + reminderID ;

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

/************************************************************************
 * Serverseitige Anwendungslogik
 ************************************************************************/

getAllUsers = function() {
    userArray = [];
    usersCollection = db.collection(USERS);

    return promise = new Promise(function(resolve) {

        usersCollection.get()
            .then(snapshot => {
                snapshot.forEach(user => {
                    let userTmp = user.id;
                    userArray.push({ userID : userTmp });
                });
            })
            .then(function () {
                resolve(userArray);
            });
    });
};

getLabels = function() {
    getAllUsers().then(function(res) {

        userArray.forEach(user => {

            user.contacts = [];

            contactCollection = db.collection(USERS).doc(user.userID).collection(CONTACTS);

            return promise = new Promise(function(resolve) {

                contactCollection.get()
                    .then(snapshot => {
                        snapshot.forEach(contact => {
                            let contactTmp = contact.id;
                            user.contacts.push({ contactID : contactTmp});

                        });
                    })
                    .then(function () {
                        //console.log("TEST TEST TEST: " + JSON.stringify(user.contacts));
                        resolve(user);
                    });
            });

        })

    }).then(function (user) {

        console.log("Reminder Part: " + JSON.stringify(user));

        user.contacts.forEach(contact => {

            user.contacts.reminder = [];

            reminderCollection = db.collection(USERS).doc(user.userID).collection(CONTACTS).doc(contact.contactID).collection(REMINDER);

            return promise = new Promise(function(resolve) {

                reminderCollection.get()
                    .then(snapshot => {
                        snapshot.forEach(reminder => {
                            let reminderTmp = reminder.id.label;
                            user.contacts.reminder.push({ reminderLabel : reminderTmp});

                            console.log("TEST TEST TEST: " + user.contacts.reminder);
                        });
                    })
                    .then(function () {
                        resolve(user);
                    });
            });

        })

    });
};

getLabels();

/************************************************************************
 * Helper Functions
 * Implemented to avoid redundancy
 * From Firebase 'Cloud Firestore' Documentation https://firebase.google.com/docs/firestore
 ************************************************************************/

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

//Export as Module
module.exports = router;