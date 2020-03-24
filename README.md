This repo contains:
* android client app
* firebase could functions

Android App:
In order to run this app, besides Android Studio, you'll also need:
- Download `google-services.json` file from your firebase project
- Enable you Google API Key for Places and Maps api's
- Paste that Google API Key on `ext.api_key` in keys.gradle file
- I've chosen Sendgrid as email server provider, so you'll need to create an account there and paste your api key on `ext.send_grid` in keys.gradle file

API:
I've created an express REST Api hosted as Firebase Cloud Functions
To deploy the api you need to run `firebase deploy --only functions:api` in the api folder
In the api folder is a file called `travelplan.postman_collection.json` with a Postman collection ready to import

Cloud functions:
There are also other two cloud functions to enable and disable users. To deploy them just run:
`firebase deploy --only functions:enableUser`
`firebase deploy --only functions:blockUser`

Android Architecture:
I've build this app following MVP-CleanArchitecture approachs. All packages are `Domain-named` (as Uncle Bob calls "Screaming Architecture"), and the presentation logic are on each `Presenter` class. Some of this `Presenters` has been unit tested in order to ensure proper work after changes.
I've added some `Domain Actions` as Mancuso's suggest in [Interaction Driven Design]() with the business logic (Unit tested)
Also, I've used the repository pattern to encapsulate the Firestore SDK and the REST calls. There two kinds of network access: Firestore SDK used to obtain trips information from the database (As suggested by Google), and a REST Api to admin users (For this I've used Retrofit, the android community standard library for REST Api calls)

User Guide: 