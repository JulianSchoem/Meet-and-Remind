// EISWS1819MayerSchoemaker - Server with node.js

/*********************************************************************************************************
 * Inits & Modules
 *********************************************************************************************************/

// Init Firestore
const admin = require("firebase-admin");
const db = admin.firestore();

// Init Express
const express = require('express');

// Init Routes
const USERS = "users";
const CONTACTS = "contacts";
const REMINDER = "reminder";

/*********************************************************************************************************
 * Serverseitige Anwendungslogik
 *********************************************************************************************************/

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

    //get all users and push them into JSON
    let resultWithUsers = await getUser();

    //get all contacts from Firebase and push them to the user
    let resultWithContacts = await getContacts(resultWithUsers);

    // get all labels from Firebase and push them to one contact of a user
    let resultWithLabels = await getLabels(resultWithContacts);

    return resultWithLabels;
};

// TODO: die functionen mit await mit try catch umschließen
// TODO: beim ersten durchlaufen user -> contact ein flag(field) bei beiden setzen, dass sie bereits gehandled wurden

/**
 * iterate the labels to count which label is the most frequent one
 * @param contact
 * @returns {{mainTopic: (*|Node), compare: number}}
 */
iterateLabels = function(contact, previousLabels) {
    let counts;
    if (previousLabels !== undefined) {
        counts = previousLabels;
    } else {
        counts = {};
    }

    for (let label of contact.labels) {

        // if count[labelCount] doesn't exist
        if (counts[label] === undefined) {
            // set count[labelCount] value to 1
            counts[label] = 1;
        } else {
            // increment existing value
            counts[label] = counts[label] + 1;
        }

    }

    return counts;
};

/**
 * get the user of the contact that needs to be analysed by its labels
 * @param labelInfo
 * @param userID
 */
getUserByID = function(labelInfo, userID) {
    for (let user of labelInfo) {
        if (user.userID === userID) {
            return user;
        }
    }
};

/**
 * get the labels of the contacts user
 * @param contactUser
 * @param userID
 * @returns {*}
 */
getContactByID = function(contactUser, userID) {
    for (let neededUser of contactUser.contacts) {
        if ( neededUser.contactID === userID) {
            return neededUser;
        }
    }
};
/*
concatLabels = function(array1, array2) {
    let comparedArray = {};

        // look which array is longer so it can be used in the first for-query
    let arrayLong = {};
    let arrayShort = {};
    if (array1.length > array2.length) {
        arrayLong = array1;
        arrayShort = array2;
    } else {
        arrayLong = array2;
        arrayShort = array1;
    }

    console.log("arrayLong " + JSON.stringify(arrayLong));
    console.log("arrayShort " + JSON.stringify(arrayShort));


    for (var aLong in arrayLong) {
        for (var aShort in arrayShort) {
            if (aLong === aShort) {
                let counter = arrayLong[aLong] + arrayShort[aShort];

                comparedArray = {al : counter}
            } else {
                comparedArray = {as : counter}
            }



        }
    }

    return comparedArray;
};
*/
getMainTopic = function(array) {
    let highestLabelCounter = 0;
    let highestLabel;

    /** Example array
     * array = {"Studium":3},{"Beruf":2},{"Sport":2}
     */

    for (var label in array) {
        if (array[label] > highestLabelCounter) {
            highestLabelCounter = array[label];
            highestLabel = label;
        }
    }

    return highestLabel;
};

/**
 * iterate the contact with the labels and the user of the contact
 * @param user
 * @param labelInfo
 * @returns {Promise<void>}
 */
iterateContacts = async function(user, labelInfo) {
    for (let contact of user.contacts) {
        let userID = user.userID;
        let contactID = contact.contactID;

        // get the mainTopic of the user itself
        let mainTopicUser = iterateLabels(contact, undefined);

        // find the contact of the user
        let contactUser = getUserByID(labelInfo, contactID);

        // get the labels of the contactUser
        let contactLabels = getContactByID(contactUser, userID);

        // now also iterate the labels of the contact to get both mainTopic values
        let mergedLabels = iterateLabels(contactLabels, mainTopicUser);

        // merge both arrays to one
        //let fullTopicArray = concatLabels(mainTopicUser, mainTopicContact);

        // get most frequent label
        let mainTopic = getMainTopic(mergedLabels);

        console.log("MAIN TOPIC " + JSON.stringify(mainTopic));

        /**
         * Hier müssen wir jetzt die beiden COUNTS ARRAYs zusammenführen
         */
        /**
         * Danach das gemergete COUNTS ARRAY auf hächstes label prüfen
         */

        //Set the main topic into both user and contact on Firebase
        if (mainTopic) {
            await db.collection(USERS).doc(userID).collection(CONTACTS).doc(contactID).set({topic : mainTopic}, {merge: true});
            await db.collection(USERS).doc(contactID).collection(CONTACTS).doc(userID).set({topic : mainTopic}, {merge: true});
        }
    }
};

/**
 * iterate the generated JSON to get the most frequent label
 * @param labelInfo
 * @returns {Promise<void>}
 */
getLabelCount = async function(labelInfo) {
    for (let user of labelInfo) {
        await iterateContacts(user, labelInfo);
    }
};
/**
 * Set the main topic of users and contacts into Firebase
 */
setMainTopic = async function() {
    // get all labels of reminders that users set to their contacts
    let labelInfo = await getAllLabelsInFB();

    // count which label is the most used and set it to Firebase
    await getLabelCount(labelInfo);

    console.log("------------------ FINISHED SERVERSEITIGE ANWENDUNGSLOGIK");
};

// export function for server.js
module.exports = {setMainTopic};