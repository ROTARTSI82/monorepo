import Head from 'next/head'
import { useRouter } from 'next/router'
import Link from "next/link";
import {useState} from "react";
// import Image from 'next/image'
// import styles from 'styles/Index.module.css'

const CreatePage = function () {
    const router = useRouter();
    const [approved, setApproved] = useState(false);

    return (
        <div className="w-full text-center p-12">
            <Head>
                <title>SupaQuiz</title>
                <meta name="description" content="Quizlet Clone Thing" />
                <link rel="icon" href="/favicon.ico" />
            </Head>

            <main>
                <h1>quiz id = {JSON.stringify(router.query)}</h1>
                <CreateUI approved={approved} />
            </main>
        </div>
    )
}

const CreateUI = function (props: any) {

    const [quizItems, setQuizItems] = useState<any[]>([])


    const addQuizItem = function () {
        setQuizItems([...quizItems, {
            word: "",
            definition: ""
        }])
    }

    const setWordDefinition = function (i: number, obj: any) {
        setQuizItems(
            quizItems.map((data, id) => {
                return id == i ? obj : data;
            })
        )

        console.log(quizItems)
    }

    const pushValues = function () {
        fetch('/api/quiz/[id]', {
            method: "PUT",
        }).then((data) => {

        })
    }


    return (
        <div className="CreateUI">
            <div className="QuizSettings">
                <div className="Title">
                    Quiz Title: <input className="TitleInput"/>
                </div>
                <br/>
                <div className="Description">
                    Description: <textarea cols={50} rows={10} className="DescriptionInput">

                    </textarea>
                </div>
            </div>

            <div className="QuizItems">
                <button className="AddQuizItemButton rounded-md border-2 border-black bg-gray-100 p-1" onClick={addQuizItem}>+</button>
                <div className="QuizItemList">
                    {
                       quizItems.map((data, id) => {
                           return (
                               <div key={id} className="QuizItem">
                                    <input className="Word" placeholder="Word" value={data.word} onChange={e => {
                                        setWordDefinition(id, {
                                            word: e.target.value,
                                            definition: data.definition
                                        })
                                    }}/>
                                    <input className="Definition" placeholder="Definition" value={data.definition} onChange={e => {
                                        setWordDefinition(id, {
                                            word: data.word,
                                            definition: e.target.value
                                        })
                                    }}/>
                               </div>
                           )
                       })
                    }
                </div>
            </div>

            <button className="SaveChanges rounded-md border-2 border-black bg-gray-100 p-1" onClick={pushValues}>Save Changes</button>
        </div>
    )
}

export default CreatePage
