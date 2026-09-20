const { Client } = require('pg')
const {showDelay, qTimeout, buzzTimeout, coinMultiplier, anchor, newMsg } = require("./settings")

let overrideNM = true;

function edit_distance(string1, string2) {
    let dp = [];
    for (let i = 0; i <= string1.length; i++) {
        dp.push([]);
        for (let j = 0; j <= string2.length; j++) {
            dp[i].push(0);
        }
    }
    for (let i = 1; i <= string1.length; i++) {
        dp[i][0] = i;
    }
    for (let j = 1; j <= string2.length; j++) {
        dp[0][j] = j;
    }

    for (let i = 1; i <= string1.length; i++) {
        for (let j = 1; j <= string2.length; j++) {
            if (string1[i - 1] === string2[j - 1]) {
                dp[i][j] = dp[i - 1][j - 1];
            } else {
                dp[i][j] = Math.min(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1]) + 1;
            }
        }
    }
    // console.log(dp);
    return dp[string1.length][string2.length];
}

class QBSettings {
    constructor() {

        this.teams = new Map();
        this.players = new Map();
        this.teamCounter = 0;

        this.qShowText = "";
        this.qText = [];
        this.qAnswer = "";
        this.qProgress = 0;
        this.qActive = false;
        this.qShowSpeed = 5;
        this.qShowMsg = null;
        this.qNeg = false;

        this.qFilterScat = ["Literature European", "Fine Arts Visual", "Literature American", "Science Chemistry", "History British", "Fine Arts Auditory", "Science Other", "History American", "Science Biology", "History Classical", "Science Physics", "History World", "Literature British", "Science Computer Science", "History European", "Fine Arts Other", "Science Math", "Fine Arts Audiovisual", "History Other", "Literature Other", "Literature Classical", "Religion American", "Trash American", "Mythology American", "Social Science American", "Fine Arts American", "Science American", "Science World", "Geography American", "Philosophy American", "Current Events American", "Current Events Other", "Fine Arts World", "Geography World", "Fine Arts British", "Mythology Indian", "Mythology Chinese", "Mythology Other East Asian", "Mythology Japanese", "Fine Arts European", "Religion East Asian", "Philosophy East Asian", "Trash Video Games", "Mythology Other", "Trash Sports", "Social Science Economics", "Religion Christianity", "Mythology Greco-Roman", "Trash Other", "Social Science Other", "Philosophy Classical", "Literature World", "Religion Other", "Mythology Norse", "Social Science Political Science", "Mythology Egyptian", "Philosophy European", "Trash Music", "Religion Islam", "Religion Judaism", "Trash Television", "Social Science Psychology", "Trash Movies", "Social Science Sociology", "Philosophy Other", "Social Science Linguistics", "Social Science Anthropology", "Fine Arts Opera"];
        this.qFilterCat = ["Mythology", "Literature", "Trash", "Science", "History", "Religion", "Geography", "Fine Arts", "Social Science", "Philosophy", "Current Events"];

        this.qMinYear = -1;
        this.qMaxYear = 9999;
        this.qMinDifficulty = -9;
        this.qMaxDifficulty = 9;
        this.qMinQuality = -9;
        this.qMaxQuality = 9;

        this.qBuzzer = null;
        this.qShowComplete = false;
        this.qBuzzerDeadline = 0;
        this.qDeadline = 0;
        this.qShowInterval = 0;

        this.qPower = false;
    }
}

class QBTeam {
    constructor() {
        this.points = 0;
    }
}


let channelData = new Map();

let ramUsrScore = new Map()

const qbCli = new Client({
    user: 'postgres',
    host: '127.0.0.1',
    database: 'quizdb',
    password: 'password'
})

qbCli.connect();


function stopQuestion(channel, tu) {
    let dat = channelData.get(channel.id)
    clearTimeout(dat.qBuzzerDeadline)
    clearTimeout(dat.qDeadline)
    clearInterval(dat.qShowInterval)

    setTimeout(function() {

        channelData.get(channel.id).qActive = false;
        channelData.get(channel.id).qBuzzer = null;
        // channel.fetchMessages({ limit: 1 }).then(messages => {

        channelData.get(channel.id).qShowText += ":x:";

        if (newMsg) {
            channel.send(channelData.get(channel.id).qShowText).then(m => {
                dat.qShowMsg = m
            });
        }

        showMore(dat.qShowMsg);

        channel.send(tu + "The answer was **" + channelData.get(channel.id).qAnswer + "**. Nobody got any points!");
    }, 64);
    // })
    //     .catch(console.error);
}

function addPoints(msg, cli, prof, active, money, points) {
    let earnings = points * coinMultiplier * (1 - (money / anchor));

    if (earnings < 0) {
        earnings = 0; // You can't lose money to qb!
    } else if (prof.qb_buzzes > 0) {
        earnings *= (0.75 + (prof.qb_correct / prof.qb_buzzes) * 0.25);
        // console.log(earnings);
    }

    cli.query(`UPDATE profiles SET qb_points=${parseInt(prof.qb_points) + points} WHERE userid=${msg.author.id} AND name LIKE '${active}';`);
    cli.query(`UPDATE profiles SET bal=${prof.bal + earnings} WHERE userid=${msg.author.id} AND name LIKE '${active}';`);
    cli.query(`UPDATE globals SET total_money=${money + earnings};`);

    if (!ramUsrScore.has(msg.author.id)) {
        ramUsrScore.set(msg.author.id, 0);
    }

    ramUsrScore.set(msg.author.id, ramUsrScore.get(msg.author.id) + points);
}

function stopBuzzer(msg, cli, prof, active, money) {
    let channel = msg.channel;
    channel.send("Buzz timed out! -5 points");
    channelData.get(channel.id).qShowText += ":x:"


    addPoints(msg, cli, prof, active, money, -5);

    channelData.get(channel.id).qBuzzer = null;
    if (!channelData.get(channel.id).qShowComplete) {
        channelData.get(channel.id).qShowInterval = setInterval(showMore, showDelay, channelData.get(channel.id).qShowMsg);
    } else {
        channelData.get(channel.id).qDeadline = setTimeout(stopQuestion, qTimeout, channel, "Time's up! ");
    }

    if (newMsg || overrideNM) {
        msg.channel.send("<Question is resuming>").then(nmsg => {
            channelData.get(channel.id).qShowMsg = nmsg
        })
    }
}

let showMore = function(msg) {
    let limit = channelData.get(msg.channel.id).qShowSpeed;

    let dat = channelData.get(msg.channel.id);
    if (!dat.qActive) {
        limit = 9999;
    }

    for (let i = 0; i < limit; i++) {
        dat.qShowText += dat.qText[dat.qProgress++] + " ";

        if (dat.qProgress >= dat.qText.length) {
            dat.qNeg = false;
            dat.qShowComplete = true;
            clearInterval(dat.qShowInterval);
            dat.qShowMsg.edit(dat.qShowText);
            if (dat.qActive) {
                dat.qDeadline = setTimeout(stopQuestion, qTimeout, msg.channel, "Time's up! ");
            }
            return;
        }
    }

    if (dat.qPower) {
        dat.qPower = !dat.qShowText.includes("*");
    }

    dat.qShowMsg.edit(dat.qShowText);
}

function check(content, answer) {
    let conWords = content.split(/(?![A-z])+./g);
    let answerds = answer.split(/(?![A-z])+./g);

    let ignorelist = "the,a,an,or,and,of,lol,lmao".split(",");
    let ignorelistAnsw = "the,a,an,or,and,of,prompt,accept,lt,gt".split(",");

    let nMatch = 0
    for (let cword of conWords) {
        if ((!cword) || ignorelist.includes(cword.toLowerCase())) {
            continue;
        }

        let med = 99999;
        let match = "";
        for (let aword of answerds) {
            if ((!aword) || ignorelistAnsw.includes(aword.toLowerCase())) {
                continue;
            }

            let ed = edit_distance(cword.toLowerCase(), aword.toLowerCase());
            if (ed < med) {
                med = ed;
                match = aword;
            }
        }

        if (med > 2) {
            // console.log(`"${cword}" cannot be matched in answer string!`)
            return false;
        }

        console.log(`${cword} matches ${match} with edit dist ${med}`);

        nMatch++;
    }

    console.log(`matches = ${nMatch}`);

    return nMatch >= 1;
}

check("babe ruth", "polymerization [or word forms, such as making a polymer] &lt;David Dennis&gt;/&lt;ed. AR&gt;");
check("babe ruth", "water [or H2O]");
check("babe ruth", "uniform distribution &lt;MaxS&gt;");


function runQb(msg, cli, prof, active, money) {
    if (!channelData.has(msg.channel.id)) {
        channelData.set(msg.channel.id, new QBSettings());
    }

    let dat = channelData.get(msg.channel.id);

    if (msg.author === dat.qBuzzer && dat.qActive) {
        if (check(msg.content, dat.qAnswer)) {

            dat.qShowText += `:white_check_mark:`

            cli.query(`UPDATE profiles SET qb_correct=${parseInt(prof.qb_correct) + 1} WHERE userid=${msg.author.id} AND name LIKE '${active}';`);

            if (dat.qPower) {
                cli.query(`UPDATE profiles SET qb_power=${parseInt(prof.qb_power + 1)} WHERE userid=${msg.author.id} AND name LIKE '${active}';`);

                addPoints(msg, cli, prof, active, money, 15);
                msg.reply(`POWER! The answer was **${dat.qAnswer}**! +15 points`);
            } else {
                addPoints(msg, cli, prof, active, money, 10);
                msg.reply(`Correct! The answer was **${dat.qAnswer}**! +10 points`);
            }

            clearTimeout(dat.qBuzzerDeadline)
            clearTimeout(dat.qDeadline)

            if (newMsg) {
                msg.channel.send("<Question is resuming>").then(nmsg => {
                    dat.qShowMsg = nmsg
                })
            }

            dat.qBuzzer = null;
            dat.qActive = false;
        } else {
            dat.qShowText += `:x:`

            msg.reply(`Incorrect! ${dat.qNeg ? "-5 points!" : "No penalty!"}`);

            if (dat.qNeg) {
                addPoints(msg, cli, prof, active, money, -5);
            }

            if (newMsg || overrideNM) {
                msg.channel.send("<Question is resuming>").then(nmsg => {
                    dat.qShowMsg = nmsg
                })
            }

            if (dat.qShowComplete) {
                dat.qDeadline = setTimeout(stopQuestion, qTimeout, msg.channel, "Time's up! ");
            }
            clearTimeout(dat.qBuzzerDeadline)
            dat.qBuzzer = null;
        }

        if (!dat.qShowComplete) {
            dat.qShowInterval = setInterval(showMore, showDelay, dat.qShowMsg);
        }
    }

    if (check(msg.content, 'buzz') && dat.qActive && dat.qBuzzer === null) { // `\$qcust\ +(([a-z ]+)\,{0,1}\ *)*\ *\|\ *(([a-z ]+)\,{0,1}\ *)*`
        cli.query(`UPDATE profiles SET qb_buzzes=${parseInt(prof.qb_buzzes) + 1} WHERE userid=${msg.author.id} AND name LIKE '${active}';`);

        dat.qBuzzer = msg.author;
        msg.channel.send("Buzz from " + msg.author.toString() + `! You have ${buzzTimeout / 1000}sec.`);
        clearInterval(dat.qShowInterval);
        dat.qBuzzerDeadline = setTimeout(stopBuzzer, buzzTimeout, msg, cli, prof, active, money);
        clearTimeout(dat.qDeadline);

        if (dat.qShowComplete) {
            dat.qShowText += `:large_blue_diamond:`
        } else if (dat.qPower) {
            dat.qShowText += `:grey_exclamation:`
        } else {
            dat.qShowText += `:bell:`
        }


    }

    if (msg.content === "$qb score") {
        msg.reply(`your score is **${ramUsrScore.get(msg.author.id)}**. Use \`$stat\` to see your all time total.`);
    } else if (msg.content === "$qb score reset") {
        ramUsrScore.set(msg.author.id, 0);
        msg.reply(`your score was reset to **0**`);
    } else if (msg.content === "$q" && !dat.qActive) {
        dat.qActive = true;

        let catNames = "";
        for (let c of dat.qFilterCat) {
            catNames += `OR lower(name) LIKE '%${c.toLowerCase()}%' `
        }

        let scatNames = "";
        for (let sc of dat.qFilterScat) {
            scatNames += `OR lower(name) LIKE '%${sc.toLowerCase()}%' `
        }

        let catSel = `SELECT id FROM categories WHERE false ${catNames}`;
        let scatSel = `SELECT id FROM subcategories WHERE false ${scatNames}`;
        let tournSelector = `SELECT id FROM tournaments WHERE (quality >= ${dat.qMinQuality} AND quality <= ${dat.qMaxQuality} AND year >= ${dat.qMinYear} AND year <= ${dat.qMaxYear} AND difficulty >= ${dat.qMinDifficulty} AND difficulty <= ${dat.qMaxDifficulty})`;
        let selector = `WHERE tournament_id IN (${tournSelector}) AND (subcategory_id IN (${scatSel}) OR category_id IN (${catSel}))`;

        qbCli.query(`SELECT COUNT(1) FROM tossups ${selector};`).then(nqres => {
            // console.log(nqres);

            let fullQuery = `SELECT * FROM tossups ${selector} LIMIT 1 OFFSET ${Math.floor(Math.random() * nqres.rows[0].count)};`;
            console.log(fullQuery);

            qbCli.query(fullQuery).then(toAsk => {
                // console.log(res);

                let info = "";
                qbCli.query(`SELECT * FROM tournaments WHERE id=${toAsk.rows[0].tournament_id};`).then(tinfo => {
                    info += (`\nFrom tournament "${tinfo.rows[0].name}" (difficulty ${tinfo.rows[0].difficulty}, quality ${tinfo.rows[0].quality})\naddr=${tinfo.rows[0].address}, type=${tinfo.rows[0].type}, link=${tinfo.rows[0].link}\n`)
                });

                info += (`\nFrom round "${toAsk.rows[0].round}", number ${toAsk.rows[0].number}. Wikipedia URL (SPOILER): ||${toAsk.rows[0].wikipedia_url}||`)

                qbCli.query("SELECT * FROM categories WHERE id=" + toAsk.rows[0].category_id + ";").then(
                    (catRes) => {
                        info += (`\nFrom category ${catRes.rows[0].name}`);
                    })

                if (toAsk.rows[0].subcategory_id !== null) {
                    qbCli.query("SELECT * FROM subcategories WHERE id=" + toAsk.rows[0].subcategory_id + ";").then(
                        (catRes) => {
                            info += ("\nFrom subcategory " + catRes.rows[0].name);
                        })
                }

                setTimeout(function() {
                    msg.reply(info);
                }, 64);


                setTimeout(function() {
                    msg.channel.send("<The question is starting>").then(qMsg => {
                        dat.qShowMsg = qMsg;
                        dat.qText = toAsk.rows[0].text.split(" ");
                        dat.qShowText = "";
                        dat.qProgress = 0;
                        dat.qAnswer = toAsk.rows[0].answer;
                        dat.qActive = true;
                        dat.qNeg = true;
                        dat.qShowComplete = false;
                        dat.qShowInterval = setInterval(showMore, showDelay, qMsg);

                        dat.qPower = toAsk.rows[0].text.includes("*");

                        console.log(dat.qAnswer);
                    });
                }, 128);
            });
        });
    } else if (msg.content.startsWith("$qcat ") && !dat.qActive) {
        dat.qActive = true;
        let cat = msg.content.substr(6);

        // First, query the id of the category.
        // uhhh there's a sql injection vuln here but i care
        qbCli.query({text: "SELECT * FROM categories WHERE lower(name) LIKE $1;", values: [cat.toLowerCase()]}).then((res) => {
            qbCli.query("SELECT COUNT(*) FROM tossups WHERE category_id=" + res.rows[0].id + ";").then((countRes) => {
                let n = Math.floor(Math.random() * countRes.rows[0].count); // sql injection uh-ohs
                qbCli.query("SELECT * FROM tossups WHERE category_id=" + res.rows[0].id + " LIMIT 1 OFFSET " + n + ";").then((toAsk) => {
                    msg.channel.send("<Question is starting>").then(qMsg => {
                        dat.qShowMsg = qMsg;
                        dat.qText = toAsk.rows[0].text.split(" ");
                        dat.qShowText = "";
                        dat.qProgress = 0;
                        dat.qAnswer = toAsk.rows[0].answer;
                        dat.qActive = true;
                        dat.qNeg = true;
                        dat.qShowComplete = false;
                        dat.qShowInterval = setInterval(showMore, showDelay, qMsg);
                        dat.qPower = toAsk.rows[0].text.includes("*");
                    });

                }).catch(e => console.error(e.stack))
            })
        })
    } else if (msg.content.startsWith("$qscat ") && !dat.qActive) {
        dat.qActive = true;
        let cat = msg.content.substr(7);

        qbCli.query({text: "SELECT * FROM subcategories WHERE lower(name) LIKE $1;", values: [cat.toLowerCase()]}).then((res) => {
            qbCli.query("SELECT COUNT(*) FROM tossups WHERE subcategory_id=" + res.rows[0].id + ";").then((countRes) => {
                let n = Math.floor(Math.random() * countRes.rows[0].count); // sql injection uh-ohs
                qbCli.query("SELECT * FROM tossups WHERE subcategory_id=" + res.rows[0].id + " LIMIT 1 OFFSET " + n + ";").then((toAsk) => {
                    msg.channel.send("<Question is starting>").then(qMsg => {
                        dat.qShowMsg = qMsg;
                        dat.qText = toAsk.rows[0].text.split(" ");
                        dat.qShowText = "";
                        dat.qProgress = 0;
                        dat.qAnswer = toAsk.rows[0].answer;
                        dat.qActive = true;
                        dat.qNeg = true;
                        dat.qShowComplete = false;
                        dat.qShowInterval = setInterval(showMore, showDelay, qMsg);
                        dat.qPower = toAsk.rows[0].text.includes("*");
                    });

                }).catch(e => console.error(e.stack))
            })
        })
    }  else if (msg.content === "skip" && dat.qActive && dat.qBuzzer === null && msg.author !== dat.qBuzzer) {
        stopQuestion(msg.channel, "");
    } else if (msg.content.startsWith("$qdb ") && msg.author.id === '450026704556130304') {
        let query = msg.content.substring(5);
        qbCli.query(query).then(r => {
            let resp = ``;
            for (let row of r.rows) {
                resp += JSON.stringify(row);
                resp += "\n";
            }

            resp = resp.substring(0, 1800);
            msg.reply(`\`${query}\`\n \`\`\`${resp}\`\`\``);
        });
    }
}


module.exports = {
    runQb
}
