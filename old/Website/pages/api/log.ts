import type { NextApiRequest, NextApiResponse } from 'next'

export default function handler(
  req: NextApiRequest,
  res: NextApiResponse<{req: any}>
) {
    console.log(req.query);
    res.status(200).json({req: req.query})
}
