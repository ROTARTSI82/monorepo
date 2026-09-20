import { initializeAppCheck, ReCaptchaV3Provider } from "firebase/app-check";
import {app} from './firebase'


// Pass your reCAPTCHA v3 site key (public key) to activate(). Make sure this
// key is the counterpart to the secret key you set in the Firebase console.
let appCheckInit = false;
export default function initAppCheck() {
	if (appCheckInit) {
		console.log("AppCheck double init blocked");
		return;
	}

	appCheckInit = true;
	console.log("AppCheck init");
	initializeAppCheck(app, {
		provider: new ReCaptchaV3Provider('6LdX0qgjAAAAAExX6DQsk_Ja4lLRawMjhG4b9WQ4'),

		// Optional argument. If true, the SDK automatically refreshes App Check
		// tokens as needed.
		isTokenAutoRefreshEnabled: true
	});
}
