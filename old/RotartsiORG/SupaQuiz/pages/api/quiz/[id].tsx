// Next.js API route support: https://nextjs.org/docs/api-routes/introduction
import type { NextApiRequest, NextApiResponse } from 'next'
import nc from 'next-connect';
import {json} from "stream/consumers";
import {Quiz, QuizItem} from "types";


//using put request here bc https://stackoverflow.com/questions/630453/what-is-the-difference-between-post-and-put-in-http

const handler = nc()
    .get((req : NextApiRequest, res : NextApiResponse) => {
        //get

        //auth????

        //get from database

        //send response
    })
    .put((req : NextApiRequest, res : NextApiResponse) => {
        const body : Quiz = JSON.parse(req.body);

        //put it into a database somehow xD

    })
    .delete((req : NextApiRequest, res : NextApiResponse) => {
        //delete this lol
    });

export default handler;