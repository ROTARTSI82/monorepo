import Head from 'next/head'
import { useRouter } from 'next/router'
import Link from "next/link";
import {useEffect, useState} from "react";
import {Quiz, QuizItem} from "types";
// import Image from 'next/image'
// import styles from 'styles/Index.module.css'

const CreatePage = function () {
    return (
        <div className="w-full text-center p-12">
            <Head>
                <title>SupaQuiz</title>
                <meta name="description" content="Quizlet Clone Thing" />
                <link rel="icon" href="/favicon.ico" />
            </Head>

            <main>
                <QuizView/>
            </main>
        </div>
    )
}

const QuizView = function (props: any) {
    const router = useRouter();

    const [quizId, setQuizId] = useState("");
    const [title, setTitle] = useState("")
    const [description, setDescription] = useState("")
    const [quizItems, setQuizItems] = useState<QuizItem[]>([])

    const [render, setRender] = useState(false)

    useEffect(() => {
        if (!router.isReady)
            return;

        setRender(false)

        setQuizId(router.query.id as string);
    }, [router.isReady])

    useEffect(() => {
        if (!router.isReady)
            return;

        console.log(quizId);

        fetch(`/api/quiz/${quizId}`).then((data) => {
            if (data.status == 404) {
                setTitle("");
                setDescription("");
                setQuizItems([]);
                setRender(true);
                return;
            }
            data.json().then((body) => {
                let quizData : Quiz = body.data;
                console.log(quizData)
                if (quizData.id != quizId) {
                    console.log("what the heck?")
                    return;
                }
                setTitle(quizData.title);
                setDescription(quizData.description);
                setQuizItems(quizData.quizItems);
                setRender(true);
            })
        })

    }, [quizId])


   return render ? (
       <div>
           <div className="QuizInformation">
               <h1>{title}</h1>
               <p>{description}</p>
           </div>
           <div className="QuizItems">
               {
                   quizItems.map((data, id) => {
                       return (
                           <div key={id} className="QuizItem">
                               <span className="Word">{data.word}:</span> <span className="Word">{data.definition}</span>
                           </div>
                       )
                   })
               }
           </div>
       </div>
   ) : null;
}

export default CreatePage
