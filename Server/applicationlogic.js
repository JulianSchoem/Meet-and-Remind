/*********************************************************************************************************
 * Inits & Modules
 *********************************************************************************************************/

// Init Firestore/Firebase
const admin = require('firebase-admin');
const db = admin.firestore();

// Init Routes
const USERS = 'users';
const CONTACTS = 'contacts';
const REMINDER = 'reminder';

/*********************************************************************************************************
 * Server Application Logic
 *********************************************************************************************************/

/**
 * get all users and push them into JSON
 * @returns {Promise<*>} added await for waiting for the promise
 */
getUser = async function() {
    let userArray = [];
    let usersCollection = db.collection(USERS);

    return new Promise(function(resolve) {

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

/**
 * get all contacts from Firebase and push them to the user
 * @param user
 * @returns {Promise<*>} added await for waiting for the promise
 */
getContactsFromFb = async function(user) {
    user.contacts = [];
    let contactCollection = db.collection(USERS).doc(user.userID).collection(CONTACTS);

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

    let reminderCollection = db.collection(USERS).doc(user.userID).collection(CONTACTS).doc(contact.contactID).collection(REMINDER);

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
    // get all users and push them into JSON
    let resultWithUsers = await getUser();

    // get all contacts from Firebase and push them to the user
    let resultWithContacts = await getContacts(resultWithUsers);

    // get all labels from Firebase and push them to one contact of a user
    let resultWithLabels = await getLabels(resultWithContacts);

    return resultWithLabels;
};

/**
 * iterate the labels and count how often they are set
 * if this is the opposite user we merge them with the previousLabels array
 * @param contact
 * @param previousLabels
 * @returns {*}
 */
iterateLabels = function(contact, previousLabels) {
    // counts will be the array with the counter of the labels
    let counts;
    if (previousLabels !== undefined) {
        // this case will run if the contact labels are iterated (second run of function)
        counts = previousLabels;
    } else {
        // this case will run if the user labels are iterated (first run of function)
        counts = {};
    }

    for (let label of contact.labels) {

        // if count[labelCount] doesn't exist
        if (counts[label] === undefined) {
            // set count[labelCount] value to 1
            counts[label] = 1;
        } else {
            // increment existing label with previous value
            counts[label] = counts[label] + 1;
        }

    }

    return counts;
};

/**
 * get the opposite of the user that needs to be analysed by its labels
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
 * get the opposite user with all his ID and labels
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

/**
 * analyse the merged labels of user and opposite which label is the most used
 * @param array
 * @returns {*}
 */
getMainTopic = function(array) {
    let highestLabelCounter = 0;
    let highestLabel;

    // set the counter to the value of the most frequent and also set a variable to the label that is most frequent
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
        // save current userID and contactID to use it later for setting the data into Firebase
        let userID = user.userID;
        let contactID = contact.contactID;

        // get the mainTopic of the user itself (later get it from the opposite)
        let mainTopicUser = iterateLabels(contact, undefined);
        // array of labels of the user

        // find the opposite of the user
        let contactUser = getUserByID(labelInfo, contactID);
        // String ID of the opposite

        // get the labels of the opposite
        let contactLabels = getContactByID(contactUser, userID);
        // array with labels of the opposite user

        // also iterate the labels of the opposite and add them to the users labels
        let mergedLabels = iterateLabels(contactLabels, mainTopicUser);
        // array with labels of both user and opposite

        // get most frequent label of both users
        let mainTopic = getMainTopic(mergedLabels);
        // string of the most frequent label that is set contact as a property

        // set the main topic into both user and opposite on Firebase
        if (mainTopic) {
            await db.collection(USERS).doc(userID).collection(CONTACTS).doc(contactID).set({topic : mainTopic}, {merge: true});
            await db.collection(USERS).doc(contactID).collection(CONTACTS).doc(userID).set({topic : mainTopic}, {merge: true});
        }
    }
};

/**
 * iterate the generated JSON to get the most frequent label in the end
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
    // array with all user -> their contacts -> chosen labels of reminder

    // count which label is the most used for user and his opposite and set it to Firebase
    await getLabelCount(labelInfo);
    // now the most frequent label of all user and their opposite is set to Firebase

    console.log('------------------ FINISHED SERVERSEITIGE ANWENDUNGSLOGIK');
};

// export function for server.js
module.exports = {setMainTopic};