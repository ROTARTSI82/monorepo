import {getAuth, onAuthStateChanged, User} from "firebase/auth";
import {useEffect, useState} from "react";
import {app} from "./firebase";
import { GoogleAuthProvider } from "firebase/auth";
import initAppCheck from "./firebaseAppCheck";

if (typeof window !== 'undefined')
	initAppCheck();

export const auth = getAuth(app);

export function useAuth(callback?: (u: User | null) => void) {
	const [user, setUser] = useState<string | undefined>(undefined);

	useEffect(() => {
		onAuthStateChanged(auth, (usr) => {
			setUser(usr?.uid);
			if (callback)
				callback(usr);
		});
	}, [callback]);

	return user;
}

export function useRerenderTrigger() {
	const [state, setState] = useState(0);
	return () => setState(state + 1);
}

export const googleAuthProvider = new GoogleAuthProvider();
