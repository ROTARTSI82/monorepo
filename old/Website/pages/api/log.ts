import type { NextApiRequest, NextApiResponse } from 'next'

export default function handler(
  req: NextApiRequest,
  res: NextApiResponse<{res: string}>
) {
    console.log(req);
    res.status(200).json({res: "Thanks for calling"})
}
