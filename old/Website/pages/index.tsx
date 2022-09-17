import type { NextPage } from 'next'
import Head from 'next/head'
import Link from "next/link";

const Home: NextPage = () => {
  return <div className={"container max-w-prose"}>
    <Head>
      <title>Grant&apos;s Random Things</title>
    </Head>

    <h1>Grant&apos;s Random Things</h1>
    <hr/>
    <h2>Latin Resources</h2>
    <main>
      <p className={"p-1 pb-2"}>
        <Link href={"one-click-dictionary"}>
          <a>One Click Dictionary</a>
        </Link>
      </p>
    </main>
  </div>
}

export default Home
