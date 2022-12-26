import Head from "next/head";

import style from "../../styles/transcriber/Transcriber.module.css"
import {AuthManager} from "../../src/components/Accounts";
import {useEffect, useRef, useState} from "react";
import {User} from "firebase/auth";
import {useRouter} from "next/router";

export default function PageTranscriber(props: unknown) {
	const [uid, setUid] = useState<string | undefined>(undefined);

	return <div>
		<Head>
			<title>Transcription Viewer</title>
			<meta property="og:title" content="Transcription Viewer"/>
			<meta property="og:description" content="Share & view transcriptions"/>
			<meta property="og:image" content="favicon.ico"/>
		</Head>

		<div className={"rContainer"}>
			<h1>Transcription Viewer</h1>
			<AuthManager callback={(u: User | null) => setUid(u?.uid)}>Log in with Google to upload transcriptions</AuthManager>
		</div>

		{uid ? <UploaderForm/> : <></>}
	</div>
}

function UploaderForm(props: unknown) {
	const router = useRouter();

	let transName = useRef<HTMLInputElement | null>(null);
	let visibility = useRef<HTMLSelectElement | null>(null);
	let vtt = useRef<HTMLTextAreaElement | null>(null);
	let audio = useRef<HTMLInputElement | null>(null);
	let [isSet, setSet] = useState(false);

	useEffect(() => {
		const forceString = (s: string | string[] | undefined) => typeof s === 'string' ? s : "";
		if (!isSet && transName.current && visibility.current && vtt.current && audio.current) {
			transName.current.value = forceString(router.query.name);
			visibility.current.value = forceString(router.query.visibility);
			vtt.current.value = forceString(router.query.vtt);
			// audio.current.value = forceString(router.query.audioURL);

			if (!["private", "shared"].includes(visibility.current.value))
				visibility.current.value = "private";

			setSet(true);
		}
	}, [isSet, router, /* audio.current */]);

	return <div className={"rContainer"}>
		<h1>Upload Transcription</h1>

		<div className={style.rTranscriptionForm}>
			<div>
				<b>Transcription Name</b>
				<input type={"text"} ref={transName}/>
			</div>
			<div>
				<b>Visibility</b>
				<select ref={visibility}>
					<option value={'private'}>Private</option>
					<option value={'shared'}>Anyone with Link</option>
				</select>
			</div>
			<div>
				<b>Input WebVTT</b>
				<textarea ref={vtt}/>
			</div>
			<div>
				<b>Upload Audio File</b>
				<input type={"file"} ref={audio}/>
			</div>
		</div>

		<div className={"rFullWidthCenter"}>
			<button className={"rCenteredBtn"}>Submit</button>
		</div>
	</div>
}
