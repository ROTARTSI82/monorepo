import type { NextPage } from 'next'
import Head from 'next/head'
import Link from "next/link";
// import Image from 'next/image'
// import styles from 'styles/Index.module.css'

const Home: NextPage = () => {
  return (
    <div className="w-full text-center p-12">
      <Head>
        <title>SupaQuiz</title>
        <meta name="description" content="Quizlet Clone Thing" />
        <link rel="icon" href="/favicon.ico" />
      </Head>

      <main>
        <div className="rounded-md bg-slate-100 p-4 max-w-xl m-auto">
          <h1 className="text-violet-700 font-bold text-3xl pb-3">SupaQuiz</h1>
          <hr/>
          <p className="p-3">
            Hi, I have no idea how to do CSS!
            <br/>
            This page will have the quizlets and let you search them
          </p>

          <p>
            <Link href="login">Log In</Link>, I guess (this link leads nowhere)
          </p>


        </div>
      </main>
    </div>
  )
}

export default Home
