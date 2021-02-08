const allowedScats = new Set(["Literature European", "Fine Arts Visual", "Literature American", "Science Chemistry", "History British", "Fine Arts Auditory", "Science Other", "History American", "Science Biology", "History Classical", "Science Physics", "History World", "Literature British", "Science Computer Science", "History European", "Fine Arts Other", "Science Math", "Fine Arts Audiovisual", "History Other", "Literature Other", "Literature Classical", "Religion American", "Trash American", "Mythology American", "Social Science American", "Fine Arts American", "Science American", "Science World", "Geography American", "Philosophy American", "Current Events American", "Current Events Other", "Fine Arts World", "Geography World", "Fine Arts British", "Mythology Indian", "Mythology Chinese", "Mythology Other East Asian", "Mythology Japanese", "Fine Arts European", "Religion East Asian", "Philosophy East Asian", "Trash Video Games", "Mythology Other", "Trash Sports", "Social Science Economics", "Religion Christianity", "Mythology Greco-Roman", "Trash Other", "Social Science Other", "Philosophy Classical", "Literature World", "Religion Other", "Mythology Norse", "Social Science Political Science", "Mythology Egyptian", "Philosophy European", "Trash Music", "Religion Islam", "Religion Judaism", "Trash Television", "Social Science Psychology", "Trash Movies", "Social Science Sociology", "Philosophy Other", "Social Science Linguistics", "Social Science Anthropology", "Fine Arts Opera"]);
const allowedCats = new Set(["Mythology", "Literature", "Trash", "Science", "History", "Religion", "Geography", "Fine Arts", "Social Science", "Philosophy", "Current Events"]);

const { escape } = require('sqlutils/pg');

const recognizedStaticCmds = new Set(["$q", "$b", "skip", "skip all", "buzz", "pause", "resume"]);


class Player {
    constructor() {
        this.points = 0;
        this.bonusPoints = 0;

        this.boniCorrect = 0;
        this.boniInterrupted = 0;

        this.correct = 0; // includes powers
        this.powers = 0;
        this.buzzes = 0;
        // negs excluded as it is buzzes - correct.
        this.team = -1;
    }
}

class Team {
    constructor() {
        this.name = "";
        this.captain = 0; // id of captain, also in players[]
        this.players = new Set(); // list of discord ids
    }

}

function arrToStr(arr) {
    let r = "";
    for (let scat of arr) {
        r += scat + "\n";
    }
    return r;
}

function processCat(args) {
    let arg = "";
    for (let word of args[1].toLowerCase().split(" ")) {
        arg += word.charAt(0).toUpperCase() + word.substr(1) + " ";
    }
    return arg.trim();
}

function exec(msg, room) {
    if (msg.content === "$score") {
        let p = room.players.get(msg.author.id);

        msg.reply(`\n\n**__Your Standings__**\nPoints: ${p.points + p.bonusPoints} (${p.points} without bonuses)\n\n`+
            `Power/Correct/Buzzes: ${p.powers}/${p.correct}/${p.buzzes}  # NOTE: "Correct" includes powers.` +
            `\n\nNegs: ${p.buzzes - p.correct}\nNeg%: ${100*((p.buzzes - p.correct) / p.buzzes)}\n\n` +
            `Correct%: ${p.correct * 100 / p.buzzes}\nPower%: ${p.powers * 100 / p.buzzes}/${p.powers * 100 / p.correct}`);
    }

    else if (msg.content === "$reset") {
        if (room.players.get(msg.author.id).team !== -1) {
            msg.reply(`ERROR: You're on a team! Leave that team before resetting your score.`);
        } else {
            msg.reply(`Score reset from ${room.players.get(msg.author.id).points}.`);
            room.players.set(msg.author.id, new Player());
        }
    }

    else if (msg.content === "$lb") {
        msg.reply(`Not implemented yet`)
    }

    else if (msg.content === "$scat ls") {
        let r = arrToStr(room.settings.qFilterScat);
        msg.reply(`\`\`\`\n${r}\n\`\`\``);
    }
    else if (msg.content === "$scat reset") {
        room.settings.qFilterScat = new Set(allowedScats);
        msg.reply("All subcategories enabled!");
    }
    else if (msg.content === "$scat clear") {
        room.settings.qFilterScat.clear();
        msg.reply("Cleared subcategories.");
    }
    else if (msg.content === "$cat ls") {
        let r = arrToStr(room.settings.qFilterCat);
        msg.reply(`\`\`\`\n${r}\n\`\`\``);
    }
    else if (msg.content === "$cat reset") {
        room.settings.qFilterCat = new Set(allowedCats);
        msg.reply("All categories enabled!");
    }
    else if (msg.content === "$cat clear") {
        room.settings.qFilterCat.clear();
        msg.reply("Cleared categories.");
    }
    else if (msg.content === "$tourn ls") {
        let r = arrToStr(room.settings.qFilterTourn);
        msg.reply(`\`\`\`\n${r}\n\`\`\``);
    }
    else if (msg.content === "$tourn clear") {
        room.settings.qFilterTourn.clear();
        msg.reply("Cleared tournaments");
    }
    else if (msg.content === "$filter ls") {
        msg.reply(`Year in [${room.settings.qMinYear}, ${room.settings.qMaxYear}]\n` +
                  `Quality in [${room.settings.qMinQuality}, ${room.settings.qMaxQuality}]\n` +
                  `Difficulty in [${room.settings.qMinDifficulty}, ${room.settings.qMaxDifficulty}]`)
    }
    else if (msg.content === "$filter clear") {
        room.settings.qMinYear = -1;
        room.settings.qMaxYear = 9999;
        room.settings.qMinDifficulty = -9;
        room.settings.qMaxDifficulty = 9;
        room.settings.qMinQuality = -9;
        room.settings.qMaxQuality = 9;
        msg.reply("Filters reset!");
    }
    else if (msg.content === "$team leave") {
        let executor = room.players.get(msg.author.id);
        let team = room.teams.get(executor.team);

        if (executor.team === -1) {
            msg.reply(`ERROR: You aren't on a team`);
        } else if (team.captain === msg.author.id) {
            msg.reply(`ERROR: You're captain! Transfer ownership with \`$team transfer\` first!`);
        } else {
            team.players.delete(msg.author.id);
            executor.team = -1;
            msg.reply(`You left \`${team.name}\`!`);
        }
    }
    else if (msg.content === "$team disband") {
        let team = room.teams.get(room.players.get(msg.author.id).team);

        if (room.players.get(msg.author.id).team === -1) {
            msg.reply(`ERROR: You aren't on a team`);
        } else if (team.captain !== msg.author.id) {
            msg.reply(`ERROR: You aren't team captain!`);
        } else {
            room.teams.delete(room.players.get(msg.author.id).team);

            for (let player of team.players) {
                room.players.get(player).team = -1;
            }

            msg.reply(`\`${team.name}\` has been disbanded.`);
        }
    }
    else if (msg.content === "$team score") {
        let executor = room.players.get(msg.author.id);

        if (executor.team === -1) {
            msg.reply(`ERROR: You aren't on a team!`);
        } else {
            let team = room.teams.get(executor.team);
            let r = `Team **${team.name}**\n\n`;
            let points = 0, powers = 0, correct = 0, buzzes = 0;

            for (let m of team.players) {
                let p = room.players.get(m);
                let notes = team.captain === m ? "- CAPTAIN" : "";
                r += `<@${m}> - ${p.points}p ${p.powers}/${p.correct}/${p.buzzes} ${notes}\n`;

                points += p.points + p.bonusPoints; powers += p.powers; correct += p.correct; buzzes += p.buzzes;
            }

            r += `\nPoints: ${points}\tNegs: ${buzzes - correct}\tNeg%: ${100*((buzzes - correct) / buzzes)}\n`+
                `Correct%: ${correct * 100 / buzzes}\tPower%: ${powers * 100 / buzzes}/${powers * 100 / correct}` +
                `\nPower/Correct/Buzzes: ${powers}/${correct}/${buzzes}`;


            msg.reply(r.substr(0, 1995));
        }
    }

    else {

        let executor = room.players.get(msg.author.id);

        let args = /^\$team new (.*)$/g.exec(msg.content);
        if (args !== null) {
            if (executor.team !== -1) {
                msg.reply(`ERROR: Leave your team first to create a new team.`);
            } else {
                for (let [key, value] of room.teams) {
                    if (value.name === args[1]) {
                        msg.reply(`ERROR: A team with that name already exists!`);
                        return;
                    }
                }

                let nt = new Team();
                nt.captain = msg.author.id;
                nt.players.add(nt.captain);
                nt.name = args[1];

                room.teams.set(room.teamInc, nt);
                executor.team = room.teamInc++;
                msg.reply(`Created new team \`${args[1]}\``)
            }

            return;
        }

        args = /^\$team join (.*)$/g.exec(msg.content);
        if (args !== null) {
            if (executor.team !== -1) {
                msg.reply(`ERROR: You're already on a team! Leave that team first!`);
            } else {
                let teamNames = new Map();
                for (let [key, value] of room.teams) {
                    teamNames.set(value.name, key);
                }

                if (!teamNames.has(args[1])) {
                    msg.reply(`ERROR: No team named \`${args[1]}\``)
                } else {
                    executor.team = teamNames.get(args[1]);
                    room.teams.get(executor.team).players.add(msg.author.id);
                    msg.reply(`Joined team \`${args[1]}\``);
                }
            }

            return;
        }

        args = /^\$team transfer <@!([0-9]+)>$/g.exec(msg.content);
        if (args !== null) {
            let team = room.teams.get(executor.team);
            if (executor.team === -1) {
                msg.reply(`ERROR: You're not on a team!`);
            } else if (team.captain !== msg.author.id) {
                msg.reply(`ERROR: You're not team captain!`);
            } else if (!team.players.has(args[1])) {
                msg.reply(`ERROR: <@${args[1]}> is not on your team!`);
            } else {
                team.captain = args[1];
                msg.reply(`You made <@${args[1]}> captain.`);
            }
            return;
        }

        args = /^\$scat add ([A-z ]*)$/g.exec(msg.content);
        if (args !== null) {
            let arg = processCat(args);

            if (allowedScats.has(arg)) {
                room.settings.qFilterScat.add(arg);
                msg.reply(`Enabled \`${arg}\``);
            } else {
                msg.reply(`\`${arg}\` is not a valid subcategory!`);
            }
            return;
        }

        args = /^\$scat rm ([A-z ]*)$/g.exec(msg.content);
        if (args !== null) {
            let arg = processCat(args);
            if (room.settings.qFilterScat.has(arg)) {
                room.settings.qFilterScat.delete(arg);
                msg.reply(`Removed \`${arg}\``)
            } else {
                msg.reply(`\`${arg}\` not in enabled subcategories!`);
            }
            return;
        }

        args = /^\$cat add ([A-z ]*)$/g.exec(msg.content);
        if (args !== null) {
            let arg = processCat(args);
            if (allowedCats.has(arg)) {
                room.settings.qFilterCat.add(arg);
                msg.reply(`Enabled \`${arg}\``);
            } else {
                msg.reply(`\`${arg}\` is not a valid category!`);
            }
            return;
        }

        args = /^\$cat rm ([A-z ]*)$/g.exec(msg.content);
        if (args !== null) {
            let arg = processCat(args);
            if (room.settings.qFilterCat.has(arg)) {
                room.settings.qFilterCat.delete(arg);
                msg.reply(`Removed \`${arg}\``)
            } else {
                msg.reply(`\`${arg}\` not in enabled categories!`);
            }
            return;
        }

        args = /^\$tourn rm (.*)$/g.exec(msg.content);
        if (args !== null) {
            let arg = escape(args[1]);
            if (room.settings.qFilterTourn.has(arg)) {
                room.settings.qFilterTourn.delete(arg);
                msg.reply(`Removed \`${arg}\``)
            } else {
                msg.reply(`\`${arg}\` not in enabled tournaments!`);
            }
            return;
        }

        args = /^\$tourn add (.*)$/g.exec(msg.content);
        if (args !== null) {
            let arg = escape(args[1]);
            room.settings.qFilterTourn.add(arg);
            msg.reply(`\`${arg}\` enabled!`);
            return;
        }

        args = /^\$filter difficulty (-?[0-9]+) (-?[0-9]+)$/g.exec(msg.content);
        if (args !== null) {
            room.settings.qMinDifficulty = parseInt(args[1]);
            room.settings.qMaxDifficulty = parseInt(args[2]);

            msg.reply(`Difficulty now in [${room.settings.qMinDifficulty}, ${room.settings.qMaxDifficulty}]`);
            return;
        }

        args = /^\$filter year (-?[0-9]+) (-?[0-9]+)$/g.exec(msg.content);
        if (args !== null) {
            room.settings.qMinYear = parseInt(args[1]);
            room.settings.qMaxYear = parseInt(args[2]);

            msg.reply(`Year now in [${room.settings.qMinYear}, ${room.settings.qMaxYear}]`);
            return;
        }

        args = /^\$filter quality (-?[0-9]+) (-?[0-9]+)$/g.exec(msg.content);
        if (args !== null) {
            room.settings.qMinQuality = parseInt(args[1]);
            room.settings.qMaxQuality = parseInt(args[2]);

            msg.reply(`Quality now in [${room.settings.qMinQuality}, ${room.settings.qMaxQuality}]`);
            return;
        }

        if (recognizedStaticCmds.has(msg.content)) {
            msg.react("❌");
        }
    }
}

module.exports = { exec, Player, Team }

