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

/**
 * get all users and push them into JSON
 * @returns {Promise<*>} added await for waiting for the promise
 */
getUser = async function() {
    let userArray = [];
    usersCollection = db.collection(USERS);

    return new Promise(function(resolve) {

        usersCollection.get()
            .then(snapshot => {
                snapshot.forEach(user => {
                    let userTmp = user.id;
                    userArray.push({ userID : userTmp });
                });
            })
            .then(function () {
                //console.log("Get User: " + JSON.stringify(userArray, null, 2));
                resolve(userArray);
            });
    });
};

/**
 * get all contacts from Firebase and push them to the user
 * @param user
 * @returns {Promise<*>} added await for waiting for the promise
 */
getContactsFromFb = async function(user) {
    user.contacts = [];
    contactCollection = db.collection(USERS).doc(user.userID).collection(CONTACTS);

    return new Promise(function(resolve) {

        contactCollection.get()
            .then(snapshot => {
                snapshot.forEach(contact => {
                    let contactTmp = contact.id;
                    user.contacts.push({ contactID : contactTmp});

                });
            })
            .then(function () {
                resolve(user);
            });
    });
};

/**
 * iterate all contacts
 * @param userArray
 * @returns {Promise<*>} added await for waiting for the promise
 */
getContacts = async function(userArray) {
    // we cant use .forEach here because of asynchronous complications
    for (let user of userArray) {
        await getContactsFromFb(user);
    }

    return userArray;
};

/**
 * get all labels from Firebase and push them to one contact of a user
 * @param user
 * @param contact
 * @returns {Promise<*>} added await for waiting for the promise
 */
getLabelsFromFb = async function(user, contact) {
    contact.labels = [];

    reminderCollection = db.collection(USERS).doc(user.userID).collection(CONTACTS).doc(contact.contactID).collection(REMINDER);

    return new Promise(function(resolve) {

        reminderCollection.get()
            .then(snapshot => {
                snapshot.forEach(reminder => {
                    let reminderLabel = reminder.data().label;

                    contact.labels.push(reminderLabel);

                });
            })
            .then(function () {
                resolve(user);
            });
    });

};

/**
 * iterate all reminder
 * @param user
 * @returns {Promise<*>} added await for waiting for the promise
 */
getLabelsOfContact = async function(user) {
    // we cant use .forEach here because of asynchronous complications
    for (let contact of user.contacts) {
        await getLabelsFromFb(user, contact);
    }

    return user;
};

/**
 * iterate all user
 * @param userArray
 * @returns {Promise<*>} added await for waiting for the promise
 */
getLabels = async function(userArray) {
    // we cant use .forEach here because of asynchronous complications
    for (let user of userArray) {
        await getLabelsOfContact(user);
    }

    return userArray;
};

/**
 * Main function to collect all labels of users contacts to get a final JSON with the labels
 * @returns {Promise<void>}
 */
getAllLabelsInFB = async function() {

    /**
     * get all users and push them into JSON
     */
    let resultWithUsers = await getUser();
    /**
     * get all contacts from Firebase and push them to the user
     */
    let resultWithContacts = await getContacts(resultWithUsers);

    /**
     * get all labels from Firebase and push them to one contact of a user
     */
    let resultWithLabels = await getLabels(resultWithContacts);

    return resultWithLabels;
};

// TODO: die functionen mit await mit try catch umschließen
// TODO: beim ersten durchlaufen user -> contact ein flag(field) bei beiden setzen, dass sie bereits gehandled wurden

iterateLabels = function(contact) {

    // compare stored value
    let compare = 0;

    for (let label of contact.labels) {

        // count occurrence of labels here
        let counts = {};


        // if count[labelCount] doesn't exist
        if (counts[label] === undefined) {
            // set count[labelCount] value to 1
            counts[label] = 1;
        } else {
            // increment existing value
            counts[label] = counts[label] + 1;
        }

        // counts[labelCount] > 0
        if (counts[label] > compare) {
            // set compare to counts[labelCount]
            compare = counts[label];
            // set most frequent value
            mainTopic = label;
        }
    }

    // TODO Maintopic : mainTopic usw
    return {mainTopic, compare};
};

/**
 * Match the user with his contact
 */

getUserByID = function(labelInfo, userID) {
    for (let user of labelInfo) {
        if (user.userID === userID) {
            return user;
        }
    }
};

getContactByID = function(contactUser, userID) {
    for (let neededUser of contactUser.contacts) {
        if ( neededUser.contactID === userID) {
            return neededUser;
        }
    }
};

iterateContacts = async function(user, labelInfo) {

    for (let contact of user.contacts) {

        let mainTopicUser = iterateLabels(contact);

        // den gegenüber finden
        let contactUser = getUserByID(labelInfo, contact.contactID);

        let contactLabels = getContactByID(contactUser, user.userID);

        let mainTopicContact = iterateLabels(contactLabels);

        if (mainTopicUser.compare > mainTopicContact.compare) {
            mainTopic = mainTopicUser.mainTopic;
        } else {
            mainTopic = mainTopicContact.mainTopic;

        }

        /**
         * wenn gleiche anzahl dann nimmt er das erste
         */

        //Set the main topic into the contact on Firebase
        if (mainTopic) {
            await db.collection(USERS).doc(user.userID).collection(CONTACTS).doc(contact.contactID).set({topic : mainTopic}, {merge: true});
            await db.collection(USERS).doc(contact.contactID).collection(CONTACTS).doc(user.userID).set({topic : mainTopic}, {merge: true});
        }
    }
};

getLabelCount = async function(labelInfo) {
    // iterate the generated JSON to get the most frequent label
    for (let user of labelInfo) {
        await iterateContacts(user, labelInfo);
    }
};
/**
 * Set the main topic into firebase
 * @returns {Promise<void>}
 */
setMainTopic = async function() {
    /**
     * Get all labels of reminders that users set to their contacts
     */
    let labelInfo = await getAllLabelsInFB();

    /**
     * Count which label is the most used
     */
    await getLabelCount(labelInfo);

    console.log("------------------ ENDE ------------------------");
};

/**
 * Run the Serverseitige Anwendungslogik
 */
setMainTopic();


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