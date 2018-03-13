const express = require('express');
const bodyParser = require('body-parser');
const crypto = require('crypto');
const fetch = require('node-fetch');

const PORT = process.env.PORT || 5000;

// the public key used for verifying the signature signed by the corresponding private key.
const publicKey = process.env.PUBLIC_KEY;
// the API key to call API key to publish push notification
const taplytics_api_token = process.env.TAPLYTICS_API_TOKEN;
/*
A signature contains a time stamp and a hash value, in this format:
t=1504742008,v=dVLlVxcw1O/7m4GxeeaxyBxsj9AJpTeSmrdCywD2VsvIxRsB7AqBS9MNscuMYCuXs2/0TUnXgkzVPvWGQw73Jg==
The hash is computed as: hash = HMAC512(timestamp + '.' + payload)
*/
const verifySignature = (signature, publicKey, payload) => {
    if (!signature) {
        throw new Error('Empty signature');
    }
    const parts = signature.split(',');
    if (parts.length !== 2) {
        throw new Error('Invalid format');
    }
    const timestamp = parts[0];
    const digest = parts[1];
    if (timestamp.substr(0, 2) !== 't=') {
        throw new Error('Invalid timestamp');
    }
    const t = Number(timestamp.substr(2, timestamp.length));
    if (isNaN(t)) {
        throw new Error('Invalid timestamp');
    }
    // reject signature if it's too old, decide what is too 'old'
    // const now = new Date().getTime();
    // if (t * 1000 >= now) {
    //     throw new Error('Timestamp validation error');
    // }

    const verifier = crypto.createVerify('sha512');
    verifier.update(t + ".");
    verifier.update(payload);
    const sigToVerify = digest.substr(2);
    if (!verifier.verify(publicKey, sigToVerify)) {
        throw new Error('Invalid signature');
    }

    return true;
};
const verifyCallback = (req, res, buf, encoding) => {
    if (!publicKey) {
        throw new Error('Unable to verify: public key is not available');
    }
    const signature = req.headers['x-payload-signature'];
    verifySignature(signature, publicKey, buf.toString());
};

const app = express();
app.use(express.static('public'))
app.use(bodyParser.json({
    verifyCallback,
}));
app.get('/', (req, res) => res.send('Gentrack Platform Push Notification Demo - webhook'));
app.post('/webhook', (req, res) => {
    console.info('Received billReady: ', req.body);

    if (req.body.eventType !== 'bill-ready') {
        res.status(400).send('Unknown event type ' + req.body.eventType);
        return;
    }
    if (!taplytics_api_token) {
        console.error('Taplytics api token is not available');
        res.status(500).json({
            error: 'Taplytics api token is not available'
        });
        return;
    }
    const pushUrl = 'https://api.taplytics.com/v2/push?api_token=' + taplytics_api_token;
    // We could *retrieve* some consumption data from CRM, so we can display a nice chart
    // to a customer when he receives the notification
    const recentConsumptions = [];
    for (var i = 0; i < 5; ++i) {
        recentConsumptions.push(Math.random() * 80 + 80);
    }
    const customData = Object.assign({
        'tl_silent': true,
        'tl_priority': 1,
        'tl_title': 'Energise Ltd',
        recentConsumptions: recentConsumptions
    }, req.body.data);

    var image = {
        'url': 'https://'+ req.get('host') + '/Energise.png',
        'tl_cdn': true
    };

    const pushPayload = {
        'name': 'BillReadyPushNotification',
        'notification': {
            'alert': 'Your latest monthly bill is ready. Tap to open.'
        },
        'image': image,
        'custom_keys': customData,
        'filter_duplicate_content': true,
        'production': false,
        'is_silent': false
    };
    fetch(pushUrl, {
        method: 'POST',
        body: JSON.stringify(pushPayload),
        headers: {'Content-Type': 'application/json'}
    })
    .then(res => res.json())
    .then(json => console.log(json))
    .then(() => res.sendStatus(200))
    .catch(err => {
        console.error('Error: ' + err);
        res.status(500).send(err);
    });
});
app.listen(PORT, () => {
    console.log('Webhook started on ' + PORT);
});
