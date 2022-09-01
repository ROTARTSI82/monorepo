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
                <CreateUI/>
            </main>
        </div>
    )
}

const CreateUI = function (props: any) {

    const router = useRouter();
    const quizId = router.query.id as string;

    const [title, setTitle] = useState("")
    const [description, setDescription] = useState("")
    const [quizItems, setQuizItems] = useState<QuizItem[]>([])
    //CHANGE TO FALSE AFTER YOU IMPLEMENT THE DATABASE STUFF
    const [render, setRender] = useState(true)

    useEffect(() => {
        fetch(`quiz/${quizId}`).then((data) => {
            data.json().then((body : Quiz) => {
                if (body.id != quizId) {
                    console.log("what the heck?")
                    return;
                }
                setTitle(body.title);
                setDescription(body.description);
                setQuizItems(body.quizItems);
                setRender(true);
            })
        })
    }, [])


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
        );
    }

    const pushValues = function () {

        const quizData : Quiz = {
            id: quizId,
            title: title,
            description: description,
            quizItems: quizItems,
            //auth token? idk how auth works
        }

        fetch(`/api/quiz/${quizId}`, {
            method: "PUT",
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(quizData)
        }).then((data) => {
            if (data.status == 200) {
                console.log(":)")
            }
        })
    }


    return render ? (
        <div className="CreateUI">
            <div className="QuizSettings">
                <div className="Title">
                    Quiz Title: <input className="TitleInput" onChange={(e) => {setTitle(e.target.value)}}/>
                </div>
                <br/>
                <div className="Description">
                    Description: <textarea cols={50} rows={10} className="DescriptionInput" onChange={(e) => {setDescription(e.target.value)}}>

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
    ) : null;
}

export default CreatePage
