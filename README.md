## This repo contains

* Android client app
* Firebase could functions

### Android App
#### In order to run this app, besides Android Studio, you'll also need to:
- Download `google-services.json` file from your firebase project
- Enable you Google API Key for Google Places and Google Maps api's
- Paste that Google API Key on `ext.api_key` in *keys.gradle* file
- I've chosen *Sendgrid* as email server provider, so you'll need to create an account there and paste your api key on `ext.send_grid` in keys.gradle file

### API
I've created an express REST Api hosted as Firebase Cloud Functions
To deploy the api you need to run `firebase deploy --only functions:api` in the api folder
In the api folder is a file called `travelplan.postman_collection.json` with a Postman collection ready to import

Cloud functions:
There are also other two cloud functions to enable and disable users. To deploy them just run:
`firebase deploy --only functions:enableUser`
`firebase deploy --only functions:blockUser`

Android Architecture:
I've build this app following MVP-CleanArchitecture approachs. All packages are **Domain-named** (as *Uncle Bob* calls "Screaming Architecture"), and the presentation logic are on each `Presenter` class. Some of this `Presenters` has been unit tested in order to ensure proper work after changes.
I've added **some** **Domain Actions** as *Sandro Mancuso* suggest in [Interaction Driven Design](https://codurance.com/2017/12/08/introducing-idd/) with the business logic (Unit tested)
Also, I've used the repository pattern to encapsulate the *Firestore SDK* and the REST calls. There two kinds of network access: *Firestore SDK* used to obtain trips information from the database (As suggested by Google), and a REST Api to admin users (For this I've used *Retrofit*, the android community standard library for REST Api calls)

## User Guide



![](/images/main_screen.png)

In your main screen you will see your trips. First your future trips with the counter of days to go! And then your past trips. Also there is a bottom bar with some actions:
* Log Out
* Profile
* Filter Trips
* Map Trips

![](/images/trip_detail.png)

If you click on any trip, you will see a detail where you can add some -Points of interest-, and edit or delete your trip

![](/images/map_trips.png)

If you click the _Map Trips_ button in the bottom bar, you will a Google Maps with all your trips markers.

![](/images/filter_trips.png)

The default filter options are the *Next Month* trips, but you can reduce your results filtering by place or description
