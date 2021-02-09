const pg = require("pg")

let client = null;

class QuestionParams {
    constructor() {
        this.qFilterTourn = new Set(); // explicit whitelist. all questions in tournaments here are allowed
        this.qFilterScat = new Set(["Literature European", "Fine Arts Visual", "Literature American", "Science Chemistry", "History British", "Fine Arts Auditory", "Science Other", "History American", "Science Biology", "History Classical", "Science Physics", "History World", "Literature British", "Science Computer Science", "History European", "Fine Arts Other", "Science Math", "Fine Arts Audiovisual", "History Other", "Literature Other", "Literature Classical", "Religion American", "Trash American", "Mythology American", "Social Science American", "Fine Arts American", "Science American", "Science World", "Geography American", "Philosophy American", "Current Events American", "Current Events Other", "Fine Arts World", "Geography World", "Fine Arts British", "Mythology Indian", "Mythology Chinese", "Mythology Other East Asian", "Mythology Japanese", "Fine Arts European", "Religion East Asian", "Philosophy East Asian", "Trash Video Games", "Mythology Other", "Trash Sports", "Social Science Economics", "Religion Christianity", "Mythology Greco-Roman", "Trash Other", "Social Science Other", "Philosophy Classical", "Literature World", "Religion Other", "Mythology Norse", "Social Science Political Science", "Mythology Egyptian", "Philosophy European", "Trash Music", "Religion Islam", "Religion Judaism", "Trash Television", "Social Science Psychology", "Trash Movies", "Social Science Sociology", "Philosophy Other", "Social Science Linguistics", "Social Science Anthropology", "Fine Arts Opera"]);
        this.qFilterCat = new Set(["Mythology", "Literature", "Trash", "Science", "History", "Religion", "Geography", "Fine Arts", "Social Science", "Philosophy", "Current Events"]);

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

        this.tournament = null;
        this.number = 0;
        this.round = "";
    }

    toString() {
        let ret = `Bonus **${this.number}**, round \`${this.round}\`.\n${this.category} (${this.subcategory})\n\n` +
            `${this.tournament.toString()}\n`;

        for (let part of this.parts) {
            ret += part.toString();
        }

        return ret;
    }
}

class BonusPart {
    constructor() {
        this.number = 0;

        this.answer = "";
        this.text = "";

        this.wikipedia = "";
    }

    toString() {
        return `\nPart **${this.number}**, Wikipedia: ${this.wikipedia}\n`
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

    toString() {
        return `Tournament \`${this.name}\` (${this.year}).\nDifficulty **${this.difficulty}**, ` +
            `Quality **${this.quality}**.\n${this.link} Type: \`${this.type}\`, Address: ${this.address}`
    }
}

class Tossup {
    constructor() {
        this.answer = "";
        this.text = "";

        this.subcategory = "";
        this.category = "";

        this.tournament = null;
        this.number = 0;
        this.round = "";

        this.wikipedia = "";
    }

    toString() {
        return `Tossup **${this.number}**, round \`${this.round}\`.\n${this.category} (${this.subcategory}).\n` +
            `Wikipedia: ${this.wikipedia}\n\n${this.tournament.toString()}`;
    }
}

function generateSelector(params) {
    let catSel = ` SELECT id FROM categories WHERE false `;
    for (let cat of params.qFilterCat) {
        catSel += ` OR name LIKE '${cat}' `; // assuming this is safe!
    }
    let scatSel = `SELECT id FROM subcategories WHERE false `;
    for (let scat of params.qFilterScat) {
        scatSel += ` OR name LIKE '${scat}' `; // assuming this is safe!
    }

    let tournSelector = ` SELECT id FROM tournaments WHERE (quality >= ${params.qMinQuality} AND quality <= ${params.qMaxQuality} AND year >= ${params.qMinYear} AND year <= ${params.qMaxYear} AND difficulty >= ${params.qMinDifficulty} AND difficulty <= ${params.qMaxDifficulty}) `;
    for (let tourn of params.qFilterTourn) {
        tournSelector += ` OR lower(name) LIKE '%${tourn.toLowerCase()}%' `;
    }

    return ` WHERE tournament_id IN (${tournSelector}) AND (subcategory_id IN (${scatSel}) OR category_id IN (${catSel})) `;
}

function sqlLookup(id, table, cb) {
    client.query(`SELECT * FROM ${table} WHERE id=${id};`).then(res => {
        cb(res.rows[0]);
    }).catch(err => {
        console.error(`sqlLookup(${id}, ${table}, ${cb}) failed: ${err}`);
        cb(null);
    })
}

function constructTournament(id, cb) {
    sqlLookup(id, "tournaments", t => {
        let ret = new Tournament();
        if (t) {
            ret.year = t.year;
            ret.name = t.name;
            ret.difficulty = t.difficulty;
            ret.quality = t.quality;
            ret.address = t.address;
            ret.type = t.type;
            ret.link = t.link;
        }

        cb(ret);
    })
}

function randomTossup(params, cb) {
    let sel = generateSelector(params);
    let onErr = err => {cb(null)};

    console.log("ranTu")

    client.query(`SELECT COUNT(1) FROM tossups ${sel};`).then(res => {
        if (res.rows[0].count < 1) {
            cb(null);
            return;
        }

        let q = `SELECT * FROM tossups ${sel} LIMIT 1 OFFSET ${Math.floor(Math.random() * res.rows[0].count)};`;
        console.log(q);

        client.query(q).then(finalRes => {
            let ret = new Tossup();
            ret.answer = finalRes.rows[0].answer;
            ret.text = finalRes.rows[0].text;

            ret.number = finalRes.rows[0].number;
            ret.round = finalRes.rows[0].round;

            ret.wikipedia = finalRes.rows[0].wikipedia_url;

            sqlLookup(finalRes.rows[0].subcategory_id, "subcategories", scat => {
                if (scat) { ret.subcategory = scat.name; }

                sqlLookup(finalRes.rows[0].category_id, "categories", cat => {
                    if (cat) { ret.category = cat.name; }

                    constructTournament(finalRes.rows[0].tournament_id, tourn => {
                        ret.tournament = tourn;
                        cb(ret);
                    });
                });
            });

        }).catch(onErr);
    }).catch(onErr);
}

function randomBonus(params, cb) {
    let sel = generateSelector(params);
    let onErr = err => {cb(null)};

    client.query(`SELECT COUNT(1) FROM bonuses ${sel};`).then(res => {
        if (res.rows[0].count < 1) {
            cb(null);
            return;
        }

        let q = `SELECT * FROM bonuses ${sel} LIMIT 1 OFFSET ${Math.floor(Math.random() * res.rows[0].count)};`;
        console.log(q);

        client.query(q).then(finalRes => {
            let ret = new Bonus();
            ret.leadin = finalRes.rows[0].leadin;
            ret.number = finalRes.rows[0].number;
            ret.round = finalRes.rows[0].round;
            ret.parts = [];

            sqlLookup(finalRes.rows[0].category_id, "categories", v => {
                if (v) { ret.category = v.name; }
                sqlLookup(finalRes.rows[0].subcategory_id, "subcategories", vs => {
                    if (vs) { ret.subcategory = vs.name; }

                    constructTournament(finalRes.rows[0].tournament_id, t => {
                        ret.tournament = t;

                        let pq = `SELECT * FROM bonus_parts WHERE bonus_id=${finalRes.rows[0].id} ORDER BY number ASC;`
                        client.query(pq).then(pres => {
                            for (let part of pres.rows) {
                                let newPush = new BonusPart();
                                newPush.answer = part.answer;
                                newPush.number = part.number;
                                newPush.text = part.text;
                                newPush.wikipedia = part.wikipedia_url;

                                ret.parts.push(newPush);
                            }

                            cb(ret);
                        }).catch(onErr);
                    });
                });
            });
        }).catch(onErr);
    }).catch(onErr);
}

function init(secret) {
    client = new pg.Client(secret.quizDB);
    client.connect();
}

function stop() {
    client.exit();
}

module.exports = {
    init, stop, QuestionParams, Bonus, BonusPart, Tossup, Tournament, randomBonus, randomTossup
}