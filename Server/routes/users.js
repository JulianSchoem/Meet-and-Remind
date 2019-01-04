// EISWS1819MayerSchoemaker - Server with node.js

/**
 * Init modules
 */

// Init Firestore
const admin = require("firebase-admin");
const db = admin.firestore();

// Init Express
const express = require('express');
const router = express.Router(null);

/**
 * REST Methods
 */

// Post user into firebase
app.get('/', function (req, res) {

    console.log("HTTP Get Request");
    var userReference = firebase.database().ref("/Users/");

    //Attach an asynchronous callback to read the data
    userReference.on("value",
        function(snapshot) {
            console.log(snapshot.val());
            res.json(snapshot.val());
            userReference.off("value");
        },
        function (errorObject) {
            console.log("The read failed: " + errorObject.code);
            res.send("The read failed: " + errorObject.code);
        });
});

//Create new instance
app.put('/', function (req, res) {

    console.log("HTTP Put Request");

    var userName = req.body.UserName;
    var name = req.body.Name;
    var age = req.body.Age;

    var referencePath = '/Users/'+userName+'/';
    var userReference = firebase.database().ref(referencePath);
    userReference.set({Name: name, Age: age},
        function(error) {
            if (error) {
                res.send("Data could not be saved." + error);
            }
            else {
                res.send("Data saved successfully.");
            }
        });
});

//Update existing instance
app.post('/', function (req, res) {

    console.log("HTTP POST Request");

    var userName = req.body.UserName;
    var name = req.body.Name;
    var age = req.body.Age;

    var referencePath = '/Users/'+userName+'/';
    var userReference = firebase.database().ref(referencePath);
    userReference.update({Name: name, Age: age},
        function(error) {
            if (error) {
                res.send("Data could not be updated." + error);
            }
            else {
                res.send("Data updated successfully.");
            }
        });
});

//Delete an instance
app.delete('/', function (req, res) {

    console.log("HTTP DELETE Request");
    //todo
});