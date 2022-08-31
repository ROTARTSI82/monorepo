// Next.js API route support: https://nextjs.org/docs/api-routes/introduction
import type { NextApiRequest, NextApiResponse } from 'next'
import nc from 'next-connect';

type Data = {
    name: string
}

const handler = nc()
    .get((req : NextApiRequest, res : NextApiResponse) => {
        //get
    })
    .put((req : NextApiRequest, res : NextApiResponse) => {
        //put
    })
    .post((req : NextApiRequest, res : NextApiResponse) => {
        //post
    })
    .delete((req : NextApiRequest, res : NextApiResponse) => {
        //delete
    });

export default handler;