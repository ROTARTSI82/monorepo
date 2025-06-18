import type { NextPage } from 'next';
import Head from 'next/head';
import Link from "next/link";
import React, {Suspense} from "react";
import {AuthManager} from "../src/components/Accounts";
import styles from "../styles/Home.module.css";

const Home: NextPage = () => {
  return <>
    <Head>
      <title>Grant&apos;s Random Things</title>
      <meta property="og:title" content="Grant's Site"/>
      <meta property="og:description" content="Grant Yang's personal site"/>
      <meta property="og:image" content="favicon.ico"/>
    </Head>

    <div className={"rContainer max-w-prose"}>
      <h1>Grant&apos;s Random Things</h1>
      <hr/>
      <main>
        <p className={"p-1 pb-2 text-s"}>
          Hi, I&apos;m Grant Yang, a sophomore in highschool.
        </p>
        <div className={styles.rTextGrid}>
          <div><Link href={"https://github.com/ROTARTSI82"}>ROTARTSI82 on GitHub</Link></div>
          <div>Rotartsi#3754 on Discord</div>
          <div><Link href={"https://open.spotify.com/user/lg0amvf4xozuf1uec7x6a8bq4?si=6bd3570a3e9944ff"}>Rotartsi on Spotify</Link></div>
        </div>
        <br/>
        <p>
          This website was made with NextJS and TailwindCSS, because why not?
          Anyways, here is a list of interesting things I&apos;ve done:
        </p>

        <ul>
          <li>
            <Link href={"https://github.com/ROTARTSI82/Chess"}>
              Scacus
            </Link>
            &nbsp;A C++ UCI chess engine that does alpha-beta game tree search, with transposition tables and
            quiescence search. The board representation is with &quot;magic bitboards,&quot; and its speed is on par with
            Stockfish.
          </li>
          <li>
            <Link href={"one-click-dictionary"}>
              One Click Dictionary
            </Link>
            &nbsp;Simple web app for looking up unfamiliar Latin words.
            It allows you to paste in some text and click individual words to look them up with Whitaker&apos;s Words.
          </li>
          <li>
            <Link href={"transcriber"}>
              Transcription Viewer
            </Link>
            &nbsp;(WIP, doesn&apos;t work yet): A nice UI for viewing and editing transcriptions and sharing them with links.
            It&apos;s meant to be used with <Link href={"https://gist.github.com/ROTARTSI82/d0606b3e2acd697d64808c9a8fe8c582"}>
              this hacky python script
            </Link>,
            which transcribes an audio file with OpenAI Whisper and adds information about speakers with
            PyAnnote&apos;s speaker diarization. Probably worse than just using Otter.ai, but hey.
          </li>
          <li>
            <Link href={"https://rotartsi82.github.io/VirtualBell"}>
              MS Virtual Bell
            </Link>
            &nbsp;Virtual bell schedule for the middle school, which I made for convenience during the
            pandemic.
          </li>
        </ul>
      </main>
    </div>

    <div className={"rContainer"}>
      <h1>Account</h1>
      <Suspense fallback={<h1>Loading...</h1>}>
        <AuthManager>
          <p className={"text-left mb-0 pb-0"}>
            Some of the projects I built with Firebase require you to log in with Google to use them.
            You are not currently logged in to any account.
          </p>
        </AuthManager>
      </Suspense>
    </div>
  </>
}

export default Home
