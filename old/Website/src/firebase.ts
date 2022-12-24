// Import the functions you need from the SDKs you need

import { initializeApp } from "firebase/app";

import { getAnalytics } from "firebase/analytics";

// TODO: Add SDKs for Firebase products that you want to use

// https://firebase.google.com/docs/web/setup#available-libraries


// Your web app's Firebase configuration

// For Firebase JS SDK v7.20.0 and later, measurementId is optional

const firebaseConfig = {

	apiKey: "AIzaSyCoAisA9251kinFU04bp2g7J5TVOXI9zM8",

	authDomain: "transcribe-10ebb.firebaseapp.com",

	projectId: "transcribe-10ebb",

	storageBucket: "transcribe-10ebb.appspot.com",

	messagingSenderId: "456813096854",

	appId: "1:456813096854:web:54dbdd9d38f7a50402b7b7",

	measurementId: "G-F3MXR6CKD1"

};


// Initialize Firebase

export const app = initializeApp(firebaseConfig);

const analytics = getAnalytics(app);