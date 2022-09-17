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

	https.get(`https://archives.nd.edu/cgi-bin/wordz.pl?keyword=${encodeURIComponent(req.query.word)}`, (httpsRes) => {
		httpsRes.on("data", (data) => {
			const html = data.toString();
			const def = html.substring(html.indexOf("<pre>")+5, html.lastIndexOf("</pre>")).trim();
			res.status(200).json(def)
		});
	});
}
