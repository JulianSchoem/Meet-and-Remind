// EISWS1819MayerSchoemaker - Server with node.js

/************************************************************************
 * Init modules
 ************************************************************************/

// Init Express
const express = require('express');
const app = express();
const PORT = 3000;

/************************************************************************
 * Routes
 ************************************************************************/

app.get('/', function (req, res) {
    res.send('Server available!')
});

app.listen(PORT, function () {
    console.log('Server listening on Port 3000')
});