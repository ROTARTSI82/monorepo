import React, {PropsWithChildren, useState} from "react";
import {deleteUser, reauthenticateWithPopup, signInWithPopup, signOut, User} from "firebase/auth";
import {auth, googleAuthProvider, useAuth} from "../firebaseAuth";

export function LoginPrompt(props: PropsWithChildren) {
	return <>
		{props.children}
		<div className={"rFullWidthCenter"}>
			<button onClick={()=>signInWithPopup(auth, googleAuthProvider).catch()}
							className={"rCenteredBtn"}>Log In</button>
		</div>
	</>
}

interface AccountInfoProps {
	user: string
}

export function AccountInfo(props: AccountInfoProps) {
	const [del, setDel] = useState(false);

	function delAcc() {
		if (!auth.currentUser) {
			// this code path should be impossible!
			alert("‽⸘ You aren't logged in to any user?");
			return;
		}

		setDel(true);
		reauthenticateWithPopup(auth.currentUser, googleAuthProvider).then((cred) => {
			deleteUser(cred.user).then(() => setDel(false)).catch(() => setDel(false));
		}).catch(() => {
			setDel(false);
		});
	}

	return <>
		<p className={"text-center"}>
			You are signed in as <b>{auth.currentUser?.displayName}</b> (<b>{auth.currentUser?.email}</b>).
		</p>

		{
			del ? <>
				<p className={"bg-amber-900 m-1 p-1"}>
					<h1>Delete Account</h1>
					Please sign in again and re-authenticate to delete your account.
					<br/>
					To cancel, simply close the pop-up.
				</p>
			</> : <>
				<button onClick={()=>signOut(auth)}>Log Out</button>
				<button onClick={delAcc} className={"bg-rose-900 hover:bg-rose-700"}>Delete Account</button>
			</>
		}
	</>
}

interface AuthManagerProps extends PropsWithChildren {
	callback?: (u: User | null) => void
}

export function AuthManager(props: AuthManagerProps) {
	const user = useAuth(props.callback);

	return user ? <AccountInfo user={user}/> : <LoginPrompt>
		{props.children}
	</LoginPrompt>
}
