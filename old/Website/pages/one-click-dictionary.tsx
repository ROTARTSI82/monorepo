import {Button, Form, OverlayTrigger, Popover} from "react-bootstrap";
import 'bootstrap/dist/css/bootstrap.min.css';
import Head from "next/head";
import {FormEvent, ForwardedRef, forwardRef, useEffect, useRef, useState} from "react";


let keyInc = 0;

const LatinInput = forwardRef((props: { onSubmit: (e: FormEvent) => void }, ref: ForwardedRef<HTMLTextAreaElement | null>) => {
	return <Form>
		<Form.Group className="mb-3" controlId="exampleForm.ControlTextarea1">
			<Form.Label>Enter Latin Text</Form.Label>
			<Form.Control as="textarea" rows={16} ref={ref} className={"m-auto max-w-[90%] fill-slate-700"}/>
		</Form.Group>

		<Button variant="primary" type="submit" onClick={props.onSubmit}>
			Submit
		</Button>
	</Form>
});
LatinInput.displayName = "LatinInput";

function WhitakersWordsFetcher(props: {word: string}) {
	let [result, setResult] = useState("");

	// thanks nelson
	// https://github.com/ng8165/Whitakers/blob/f981d507491326ebbc5cea33bc4b7844164dad3c/app.js
	useEffect(() => {
		fetch(`/api/whitakers/?word=${encodeURIComponent(props.word)}`)
			.then((response) => response.json())
			.then(setResult).catch((error) => {
				setResult(`ERROR: ${JSON.stringify(error)}`)
			})
	}, [setResult, props.word]);

	return <>
		{props.word} <hr/>
		{result.split('\n').map((line: string) => <span key={++keyInc}>{line}<br/></span>)}
	</>
}


const alphabet = "abcdefghijklmnopqrstuvwxyzāēīōū";
function parseWords(line: string) {
	let ret: JSX.Element[] = [];

	let nonWord = "";
	let wordPart = "";

	const pushWord = () => {
		ret.push(<OverlayTrigger
			trigger="click"
			key={++keyInc}
			placement="top"
			overlay={
				<Popover id={`popover-${++keyInc}`}>
					<Popover.Header as="h3"></Popover.Header>
					<Popover.Body>
						<WhitakersWordsFetcher word={wordPart}/>
					</Popover.Body>
				</Popover>
			}
			defaultShow={false} delay={100} flip={false} onHide={undefined} onToggle={undefined}
			popperConfig={undefined} show={undefined} target={undefined} rootClose>

			<span className={"hover:cursor-pointer underline"}>{wordPart}</span>
		</OverlayTrigger>);
	};

	for (let c of line) {
		if (alphabet.includes(c.toLowerCase())) {
			if (nonWord.length > 0) {
				ret.push(<>{nonWord}</>)
				nonWord = "";
			}

			wordPart += c;
		} else {
			if (wordPart.length > 0) {
				pushWord();
				wordPart = ""
			}

			nonWord += c;
		}
	}

	if (nonWord.length > 0)
		ret.push(<span key={++keyInc}>{nonWord}</span>)

	if (wordPart.length > 0)
		pushWord();

	return ret;
}

function LatinReader(props: {latin: string | null}) {
	const spacingStyle = {
		marginTop: "50rem"
	}

	return <div className={"font-serif p-4 text-left pt-6"}>
		<div style={spacingStyle}>&nbsp;</div>
		<p>
			{props.latin?.split("\n").map((e: string) => <span key={++keyInc}>{parseWords(e)}<br/></span>)}
		</p>
	</div>
}

export default function PageOneClickDictionary(props: unknown) {
	let latinRef = useRef<HTMLTextAreaElement | null>(null);
	let [latinState, setLatin] = useState<string | null>(null);

	const submitLatin = (e: FormEvent) => {
		e.preventDefault();
		if (latinRef.current?.value)
			setLatin(latinRef.current?.value);
	}

	function clearLatin() {
		if (latinRef.current)
			latinRef.current.value = "";
		setLatin(null);
	}

	return <div className={"solarisBg"}>
		<Head>
			<title>One-Click Latin Dictionary</title>
			<meta property="og:title" content="One-Click Latin Dictionary"/>
			<meta property="og:description" content="Look up words in a text instantly with Whitaker's Words."/>
			<meta property="og:image" content="favicon.ico"/>
		</Head>


		<div className={"rContainer max-w-6xl px-16"}>
			<h1>One-Click Latin Dictionary</h1>
			<Button variant={"primary"} onClick={clearLatin}>Clear</Button>
			<br/><br/>
			{latinState ? <LatinReader latin={latinState}/> : <LatinInput ref={latinRef} onSubmit={submitLatin}/> }
		</div>
	</div>
}

