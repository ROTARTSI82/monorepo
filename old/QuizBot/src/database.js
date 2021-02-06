const pg = require("pg")

let client = null;

class QuestionParams {
    constructor() {
        this.qFilterTourn = []; // explicit whitelist. all questions in tournaments here are allowed
        this.qFilterScat = ["Literature European", "Fine Arts Visual", "Literature American", "Science Chemistry", "History British", "Fine Arts Auditory", "Science Other", "History American", "Science Biology", "History Classical", "Science Physics", "History World", "Literature British", "Science Computer Science", "History European", "Fine Arts Other", "Science Math", "Fine Arts Audiovisual", "History Other", "Literature Other", "Literature Classical", "Religion American", "Trash American", "Mythology American", "Social Science American", "Fine Arts American", "Science American", "Science World", "Geography American", "Philosophy American", "Current Events American", "Current Events Other", "Fine Arts World", "Geography World", "Fine Arts British", "Mythology Indian", "Mythology Chinese", "Mythology Other East Asian", "Mythology Japanese", "Fine Arts European", "Religion East Asian", "Philosophy East Asian", "Trash Video Games", "Mythology Other", "Trash Sports", "Social Science Economics", "Religion Christianity", "Mythology Greco-Roman", "Trash Other", "Social Science Other", "Philosophy Classical", "Literature World", "Religion Other", "Mythology Norse", "Social Science Political Science", "Mythology Egyptian", "Philosophy European", "Trash Music", "Religion Islam", "Religion Judaism", "Trash Television", "Social Science Psychology", "Trash Movies", "Social Science Sociology", "Philosophy Other", "Social Science Linguistics", "Social Science Anthropology", "Fine Arts Opera"];
        this.qFilterCat = ["Mythology", "Literature", "Trash", "Science", "History", "Religion", "Geography", "Fine Arts", "Social Science", "Philosophy", "Current Events"];

        this.qMinYear = -1;
        this.qMaxYear = 9999;
        this.qMinDifficulty = -9;
        this.qMaxDifficulty = 9;
        this.qMinQuality = -9;
        this.qMaxQuality = 9;
    }
}

class Bonus {
    constructor() {
        this.leadin = "";
        this.parts = [];

        this.subcategory = "";
        this.category = "";

        this.tornament = null;
        this.number = 0;
        this.round = "";
    }
}

class BonusPart {
    constructor() {
        this.number = 0;

        this.answer = "";
        this.text = "";

        this.wikipedia = "";
    }
}

class Tournament {
    constructor() {
        this.year = 0;
        this.name = "";

        this.difficulty = 0;
        this.quality = 0;
        this.address = "";
        this.type = "";
        this.link = "";
    }
}

class Tossup {
    constructor() {
        this.answer = "";
        this.text = "";

        this.subcategory = "";
        this.category = "";

        this.tornament = null;
        this.number = 0;
        this.round = "";

        this.wikipedia = "";
    }
}

function generateSelector(params) {
    let catSel = ` SELECT id FROM categories WHERE false `;
    for (let cat of params.qFilterCat) {
        catSel += ` OR lower(name) LIKE '%${cat.toLowerCase()}%' `; // assuming this is safe!
    }
    let scatSel = `SELECT id FROM subcategories WHERE false `;
    for (let scat of params.qFilterScat) {
        scatSel += ` OR lower(name) LIKE '%${scat.toLowerCase()}%' `; // assuming this is safe!
    }

    let tournSelector = ` SELECT id FROM tournaments WHERE (quality >= ${params.qMinQuality} AND quality <= ${params.qMaxQuality} AND year >= ${params.qMinYear} AND year <= ${params.qMaxYear} AND difficulty >= ${params.qMinDifficulty} AND difficulty <= ${params.qMaxDifficulty}) `;
    for (let tourn of params.qFilterTourn) {
        tournSelector += ` OR lower(name) LIKE '%${tourn.toLowerCase()}%' `;
    }

    return ` WHERE tournament_id IN (${tournSelector}) AND (subcategory_id IN (${scatSel}) OR category_id IN (${catSel})) `;
}

function randomTossup(params) {
    let sel = generateSelector(params);

    client.query(`SELECT COUNT(1) FROM tossups ${sel};`).then(res => {
        let q = `SELECT * FROM tossups ${sel} LIMIT 1 OFFSET ${Math.floor(Math.random() * res.rows[0].count)};`;
        console.log(q);

        client.query(q).then(finalRes => {

        })
    })
}

function init(secret) {
    client = new pg.Client(secret.quizDB);
}

function stop() {
    client.exit();
}

module.exports = {
    init, stop, QuestionParams
}