import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';
import * as express from 'express';
import * as cors from 'cors';
import { routesConfig } from './users/routes-config';

admin.initializeApp();

const app = express();
app.use(express.json());
app.use(cors({ origin: true }));
routesConfig(app)

export const api = functions.https.onRequest(app);

exports.blockUser = functions.https.onCall((data, context) => {
  admin.auth().getUserByEmail(data.email).then ((userRecord) => {
    console.log('Successfully fetched user data:', userRecord.toJSON());
    admin.auth().updateUser(userRecord.uid, { disabled: true })
      .then(function(userUpdated) {
        // See the UserRecord reference doc for the contents of userRecord.
        console.log('Successfully blocked user', userUpdated.toJSON());
        return userUpdated.uid
      })
      .catch(function(error) {
        console.log('Error blocking user:', error);
        throw new functions.https.HttpsError('invalid-argument', 'Error blocking user');
      });
  })
  .catch(function(error) {
    console.log('Error fetching user:' + data.email, error);
    throw new functions.https.HttpsError('invalid-argument', 'Email invalid');
  });
});

exports.enableUser = functions.https.onCall((data, context) => {
  admin.auth().getUser(data.uid).then ((userRecord) => {
    console.log('Successfully fetched user data:', userRecord.toJSON());
    admin.auth().updateUser(userRecord.uid, { disabled: false })
      .then(function(userUpdated) {
        // See the UserRecord reference doc for the contents of userRecord.
        console.log('Successfully blocked user', userUpdated.toJSON());
        return userUpdated.uid
      })
      .catch(function(error) {
        console.log('Error blocking user:', error);
        throw new functions.https.HttpsError('invalid-argument', 'Error blocking user');
      });
  })
  .catch(function(error) {
    console.log('Error fetching user:' + data.email, error);
    throw new functions.https.HttpsError('invalid-argument', 'Email invalid');
  });
});