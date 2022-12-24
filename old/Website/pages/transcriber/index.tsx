import Head from "next/head";

import style from "../../styles/transcriber/Transcriber.module.css"

export default function PageTranscriber(props: unknown) {
	return <div>
		<Head>
			<title>Transcription Viewer</title>
			<meta property="og:title" content="Transcription Viewer"/>
			<meta property="og:description" content="Share & view transcriptions"/>
			<meta property="og:image" content="favicon.ico"/>
		</Head>

		<div className={"container"}>
			<h1>Transcription Viewer</h1>

			<br/>

			<input type={"file"}/>
			<div className={"grid grid-cols-2 gap-4 m-auto max-w-[75%]"}>
				<button>Import Transcription</button>
				<button onClick={()=>alert("hi")}>Log In</button>
			</div>
		</div>
	</div>
}
