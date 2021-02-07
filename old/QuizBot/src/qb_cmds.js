const allowedScats = new Set(["Literature European", "Fine Arts Visual", "Literature American", "Science Chemistry", "History British", "Fine Arts Auditory", "Science Other", "History American", "Science Biology", "History Classical", "Science Physics", "History World", "Literature British", "Science Computer Science", "History European", "Fine Arts Other", "Science Math", "Fine Arts Audiovisual", "History Other", "Literature Other", "Literature Classical", "Religion American", "Trash American", "Mythology American", "Social Science American", "Fine Arts American", "Science American", "Science World", "Geography American", "Philosophy American", "Current Events American", "Current Events Other", "Fine Arts World", "Geography World", "Fine Arts British", "Mythology Indian", "Mythology Chinese", "Mythology Other East Asian", "Mythology Japanese", "Fine Arts European", "Religion East Asian", "Philosophy East Asian", "Trash Video Games", "Mythology Other", "Trash Sports", "Social Science Economics", "Religion Christianity", "Mythology Greco-Roman", "Trash Other", "Social Science Other", "Philosophy Classical", "Literature World", "Religion Other", "Mythology Norse", "Social Science Political Science", "Mythology Egyptian", "Philosophy European", "Trash Music", "Religion Islam", "Religion Judaism", "Trash Television", "Social Science Psychology", "Trash Movies", "Social Science Sociology", "Philosophy Other", "Social Science Linguistics", "Social Science Anthropology", "Fine Arts Opera"]);
const allowedCats = new Set(["Mythology", "Literature", "Trash", "Science", "History", "Religion", "Geography", "Fine Arts", "Social Science", "Philosophy", "Current Events"]);



class Player {
    constructor() {
        this.points = 0;

        this.correct = 0; // includes powers
        this.powers = 0;
        this.buzzes = 0;
        // negs excluded as it is buzzes - correct.
        this.team = -1;
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
    if (msg.content === "$stat") {
        let p = room.players.get(msg.author.id);

        msg.reply(`\n\n**__Your Standings__**\nPoints: ${p.points}\n\n`+
            `Power/Correct/Buzzes: ${p.powers}/${p.correct}/${p.buzzes}  # NOTE: "Correct" includes powers.` +
            `\n\nNegs: ${p.buzzes - p.correct}\nNeg%: ${100*((p.buzzes - p.correct) / p.buzzes)}\n\n` +
            `Correct%: ${p.correct * 100 / p.buzzes}\nPower%: ${p.powers * 100 / p.buzzes}/${p.powers * 100 / p.correct}`);
    }

    else if (msg.content === "$reset") {
        msg.reply(`Score reset from ${room.players.get(msg.author.id).points}`);
        room.players.set(msg.author.id, new Player());
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
        msg.reply(`Year in [${room.settings.qMinYear}, ${room.set.qMaxYear}]\n` +
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

    else {

        let args = /^\$scat add ([A-z ]*)$/g.exec(msg.content);
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
            if (room.settings.qFilterTourn.has(args[1])) {
                room.settings.qFilterTourn.delete(args[1]);
                msg.reply(`Removed \`${args[1]}\``)
            } else {
                msg.reply(`\`${args[1]}\` not in enabled tournaments!`);
            }
            return;
        }

        args = /^\$tourn add (.*)$/g.exec(msg.content);
        if (args !== null) {
            room.settings.qFilterTourn.add(args[1]);
            msg.reply(`\`${args[1]}\` enabled!`);
            return;
        }

        args = /^\$filter difficulty -?[0-9]+ -?[0-9]+$/g.exec(msg.content);

    }
}

module.exports = { exec, Player }

