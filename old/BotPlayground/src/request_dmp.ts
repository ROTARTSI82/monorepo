const express = require('express');

const app = express();

app.get('/', (request, response) => {
	console.log(request.body);
	console.log(request.headers);
	console.log(request.auth);
	console.log(request.params);
	console.log(request.cookie);
	console.log(request.query);
	console.log(request.path);

	return response.text("Hai");
});

app.listen(31415, () => console.log(`App listening at http://localhost:${31415}`));

