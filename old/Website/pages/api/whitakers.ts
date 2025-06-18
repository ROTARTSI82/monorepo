// Next.js API route support: https://nextjs.org/docs/api-routes/introduction
import type { NextApiRequest, NextApiResponse } from 'next'
import * as https from "https";

type Data = {
	name: string
}

export default function handler(
	req: NextApiRequest,
	res: NextApiResponse<Data>
) {
	if (typeof req.query.word !== "string") {
		res.status(400);
		return;
	}

	https.get(`https://latin-words.com/cgi-bin/translate.cgi?query=${encodeURIComponent(req.query.word)}`, (httpsRes) => {
		let body = "";
		httpsRes.on("data", (data) => {
			body += data.toString();
		});
		httpsRes.on("end", () => {
			res.status(200).json(JSON.parse(body).message);
		})
	});
}
