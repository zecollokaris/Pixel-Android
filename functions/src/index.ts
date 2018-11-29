import * as functions from 'firebase-functions';

import * as admin from 'firebase-admin';

admin.initializeApp();


exports.addWelcomeMessage=functions.auth.user().onCreate(async(user)=>{
    console.log('A new user signed in for the first time.');
    const fullName = user.displayName || 'Anonymous';
  
    // Saves the new welcome message into the database
    // which then displays it in the FriendlyChat clients.
    await admin.database().ref(`messages/${user.uid}`).set({
      name: 'Finder App',
      profilePicUrl: '/images/finder.png', // Firebase logo
      text: `${fullName} signed in for the first time! Welcome!`,
    });
    console.log('Welcome message written to database.');
});