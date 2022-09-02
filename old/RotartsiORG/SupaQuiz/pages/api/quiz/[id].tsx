// Next.js API route support: https://nextjs.org/docs/api-routes/introduction
import type { NextApiRequest, NextApiResponse } from 'next'
import nc from 'next-connect';
import {json} from "stream/consumers";
import {Quiz, QuizItem} from "types";
import {supabase} from "supabase";


//using put request here bc https://stackoverflow.com/questions/630453/what-is-the-difference-between-post-and-put-in-http

const handler = nc()
    .get(async (req : NextApiRequest, res : NextApiResponse) => {
        //get

        //auth????

        //get from database
        const { data, error } = await supabase
            .from('quizzes')
            .select('data')
            .eq('id', req.query.id)

        //send response
        if (data) {
            res.status(200).json(data[0]);
        } else {
            res.status(418).end("I'm a teapot.")
        }
    })
    .put(async (req : NextApiRequest, res : NextApiResponse) => {
        console.log("got a request");
        console.log(req.body)

        //auth??

        //put into database
        const {data, error} = await supabase
            .from('quizzes')
            .upsert([{
                id: req.query.id,
                data: req.body
            }]);

        console.log(error);

        //put it into a database somehow xD
        res.status(200).end("BASED!!!")
    })
    .delete((req : NextApiRequest, res : NextApiResponse) => {
        //delete this lol
    });

export default handler;