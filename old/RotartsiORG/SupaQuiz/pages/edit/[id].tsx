import Head from 'next/head'
import { useRouter } from 'next/router'
import Link from "next/link";
import {useEffect, useState} from "react";
import {Quiz, QuizItem} from "types";
// import Image from 'next/image'

const CreatePage = function () {
    return (
        <div className="">
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
    }, [router.isReady, router.query.id])

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

    }, [quizId, router.isReady])


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

    const removeQuizItem = function (i: number) {
        setQuizItems(
            quizItems.filter((data, id) => {
                return id != i;
            })
        )
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
                <div className='heading'>
                    Create a new quiz
                </div>
                <div className="Title">
                    {/*<span className="TitleHeading">Title</span>*/}
                    <input className="TitleInput" value={title} placeholder="Title"
                                       onChange={(e) => {setTitle(e.target.value)}}/>
                </div>
                <br/>
                <div className="Description">
                    {/*<span className="DescriptionHeading">Description</span>*/}
                    <textarea cols={50} rows={10} className="DescriptionInput"
                                           placeholder="Description"
                                           value={description}
                                           onChange={(e) => {setDescription(e.target.value)}} />
                </div>
            </div>

            <div className="QuizItems">
                <button className="AddQuizItemButton" onClick={addQuizItem}>+</button>
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
                                    <button className="RemoveButton" onClick={() => removeQuizItem(id)}>-</button>
                                </div>
                            )
                        })
                    }
                </div>
            </div>

            <button className="SaveChanges" onClick={pushValues}>Save Changes</button>
        </div>
    ) : null;
}

export default CreatePage
