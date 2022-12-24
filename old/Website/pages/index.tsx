import type { NextPage } from 'next';
import Head from 'next/head';
import Link from "next/link";

const Home: NextPage = () => {
  return <div className={"container max-w-prose"}>
    <Head>
      <title>Grant&apos;s Random Things</title>
      <meta property="og:title" content="Grant's Site"/>
      <meta property="og:description" content="Grant Yang's personal site"/>
      <meta property="og:image" content="favicon.ico"/>
    </Head>

    <h1>Grant&apos;s Random Things</h1>
    <hr/>
    <main>
      <p className={"p-1 pb-2 text-s"}>
        Hi, I'm Grant Yang, a sophomore in highschool. Here is a list of interesting things I've done.
      </p>

      <br/>
      <h2>Latin</h2>
      <p className={"p-1 pb-2"}>
        <Link href={"one-click-dictionary"}>
          <a>One Click Dictionary</a>
        </Link>
      </p>
      <h2>Journalism</h2>
      <p className={"p-1 pb-2"}>
        <Link href={"transcriber"}>
          <a>Interview Transcriber</a>
        </Link>
      </p>
    </main>
  </div>
}

export default Home
