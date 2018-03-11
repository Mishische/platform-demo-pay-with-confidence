## Platform Rich Push Notification Demo

### Introduction

Rich push notification is a push notification with a rich media attachment such as images, videos, audio, and interactive buttons.
Compared to a normal notification, a rich push notification drives significantly more engagement with mobile users. [REFERENCE?]


This project demonstrates the integration between [Gentrack Platform](https://help.gentrack.com/platform) and [Taplytics](https://taplytics.com/) in providing a rich push notification experience to mobile users.  The user case it demonstrates is as follow:
1. A bill is generated for a customer in a Gentrack core system, Velocity or Junifer.
2. The core system trigers and publishes an event to the Gentrack Platform.
3. The Gentrack Platform then sends the event to a registered *webhook*.
4. The webhook sends a rich push notification a utility customer's mobile device.

This project includes sample implementations of a *webhook* and an Android App.

* *webhook* - a NodeJs/Express application that receives bill events from the Platform and then pushes notifications to registered devices using Taplytics APIs.
* *Android App* -  A mobile App that receives push notifications from Taplytics and presents bill information on a user's phone.

### Run the Demo

#### Prerequisites
To build and run this demo, you will need:
1. A Taplytics account
2. A Heroku account for deploying the webhook
3. A Google account for setting up [Google Push Certificates](https://taplytics.com/docs/guides/push-notifications/google-push-certificates) and for receiving push notifications from an emulator.
3. Android Studio 3.x
    * Android SDK and Platform Tools version 27 are required for building the Android App. Refer [Android Studio user guide](https://developer.android.com/studio/intro/update.html) on installing and updating Android SDK and Platform tools.
    * An Android Emulator to run the Android App. The Emulator must use an image with `API level = 24 and Target = Android 7.0(Google APIs)`. To do so, click `AVD Manager` icon in the top right bar. Click the button `+ Create Virtual Device`. Choose Category `Phone` and Name `Pixel 2`. Click `Next`. Select `Nougat` -> `Next` -> `Finish`. In addition, you must add and log in to a Google account in the Emulator in order to receive push notifications.

#### Create a Taplytics APP
1. Log in to Taplytics. Select *Add a New Project*. Choose a project name and click on *Create App*.
2. Select *Android/TV/FIre TV* on the *SDK integration* page.
3. Find Android SDK key and URL scheme for your application on the page. You will need to provide them to the Android App in the next step.
```
....
# Android SDK key
Taplytics.startTaplytics(this, "6e9baefa4c41c829f327887f2401a43a48e372ce");
...
# Android URL scheme
<data android:scheme="tl-06ba13aa"/>
...
```
### Build Android Application
1. Clone or download the demo project to a local directory.
2. Open Android Studio. Choose *Open an Existing Android Studio Project*. Navigate and Open the directory *mobileAppAndroid* in the project.
3. Open *mobileAppAndroid\app\src\main\res\values\strings.xml* and update *taplytics_android_sdk_key* and *taplytics_android_sdk_key* with the keys of your Taplytics application, obtained in the previous step.
4. Build and run the application in the emulator. The Android App will connect to Taplytics, allowing it to finsh *SDK integration* for your application.

### Finish Configuration
1. Log in to [Firebase](https://firebase.google.com/) using your Google account.
2. Create a new Firebase project where you can get *Server key* to be used by Taplytics for Google Cloud Messaging.
3. Go to the dashboard of your application on Taplytics. Set up [Google Push Certificates](https://taplytics.com/docs/guides/push-notifications/google-push-certificates).
4. Restart the Android application and it is ready to receive push notifications.

### Deploy Webhook Application
[![Deploy to Heroku](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy?template=https://github.com/jamesa-gentrack/platform-demo-notification)

1. Create a new application in the Platform Developer Portal (https://portal.gentrack.io/).
2. Click the `Deploy to Heroku` button to host the webhook.
3. On the next page, enter the Platform application's public key (App Settings -> Basic Information).
4. And Taplytics API key (Project Settings -> Taplytics REST API Private Key).
5. Test the URL in a browser, e.g., `https://(app-name).herokuapp.com`.
6. Subscribe webhook URL `https://(app-name).herokuapp.com/webhook` to the `bill-ready` event in the developer portal (Event Subscriptions -> Edit).
7. Send a test `bill-ready` event (Event Subscriptions -> Send Test Event). A push notification will be received by the mobile application.

### Disclaimer
This is a proof of concept for demonstrations only.
