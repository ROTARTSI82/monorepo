const db = require("./database")
const edist = require("levenshtein-edit-distance")
const cmds = require("./qb_cmds")

function addPoints(room, player, n) {
    room.players.get(player.id).points += n;
}

function stopQuestion(room) {
    clearInterval(room.showInterval);
    clearTimeout(room.deadlineTimeout);
    clearTimeout(room.buzzerDeadlineTimeout);

    setTimeout(() => {
        room.isActive = false;
        room.buzzer = null;

        room.rawText += ":x:";
        showMore(room);

        room.targetMsg.channel.send(`No points. The answer was **${room.answerLine}**`);
    }, 64);
}

function stopBuzzer(room) {
    room.targetMsg.channel.send(`Buzz timed out! ${room.isShowComplete ? "No penalty" : "-5 points!"}`);

    if (!room.isShowComplete) {
        addPoints(room, room.buzzer, -5);
    }

    room.rawText += ":x:";

    room.buzzer = null;

    room.targetMsg.channel.send("<Question is resuming>").then(newMsg => {
        room.targetMsg = newMsg;

        if (!room.isShowComplete) {
            room.showInterval = setInterval(showMore, room.showDelay, room);
        } else {
            room.deadlineTimeout = setTimeout(stopQuestion, room.tuTimeout, room);
        }
    });
}

function showMore(room) {
    let limit = room.showSpeed;
    if (!room.isActive) {
        limit = 9999; // complete show
    }

    for (let i = 0; i < limit; i++) {
        if (room.textProgress < room.textList.length) {
            room.rawText += room.textList[room.textProgress++] + " ";
        } else {
            room.isShowComplete = true;
            clearInterval(room.showInterval); // clear self.
            if (room.isActive) {
                room.deadlineTimeout = setTimeout(stopQuestion, room.tuTimeout, room);
            }
            break;
        }
    }

    if (room.isPower) {
        room.isPower = !room.rawText.includes("*"); // check if we exited power
    }

    room.targetMsg.edit(room.rawText);

}

class QBRoom {
    constructor() {
        this.playersLockedOut = new Set();
        this.teamsLockedOut = new Set();

        this.teams = new Map();
        this.teamInc = 0;
        this.players = new Map();

        this.targetMsg = null;

        this.textList = [];
        this.textProgress = 0;
        this.rawText = "";
        this.answerLine = "";

        this.activeQuestion = null;
        this.isActive = false;
        this.isPaused = false;
        this.isPower = false;
        this.isShowComplete = false;

        this.showInterval = 0;
        this.deadlineTimeout = 0;
        this.buzzerDeadlineTimeout = 0;

        this.buzzer = null;

        // settings
        this.showSpeed = 5;
        this.isPausingEnabled = true;
        this.isSkippingEnabled = true;
        this.isMultipleBuzzesEnabled = false;
        this.showDelay = 1250;
        this.tuTimeout = 10000; // 10 sec
        this.bzTimeout = 10000; // 10 sec
        this.settings = new db.QuestionParams();
    }

    restartQuestion() {
        this.targetMsg.channel.send("<Question is resuming>").then(newMsg => {
            this.targetMsg = newMsg;

            if (this.isShowComplete) {
                this.deadlineTimeout = setTimeout(stopQuestion, this.tuTimeout, this);
            } else {
                this.showInterval = setInterval(showMore, this.showDelay, this);
            }
        });
    }

    isLockedOut(player) {
        if (!this.isMultipleBuzzesEnabled) {
            return this.playersLockedOut.has(player.id) || this.teamsLockedOut.has(this.players.get(player.id).team);
        }
        return false;
    }

    isCorrectAnswer(answer) {
        let needMatching = answer.split(/(?![A-z])+./g);
        let words = this.answerLine.split(/(?![A-z])+./g);

        let ignore = "the,a,an,or,and,of,lol,lmao,prompt,accept,lt,gt,do,lmfao".split(",");

        let numMatch = 0;
        for (let toMatch of needMatching) {
            if ((!toMatch) || ignore.includes(toMatch.toLowerCase())) {
                continue;
            }

            let mEditPercent = 0.0;
            let match = "";

            for (let word of words) {
                if ((!word) || ignore.includes(word.toLowerCase())) {
                    continue;
                }

                let editPercent = 1 - (edist(toMatch, word, true) / Math.max(toMatch.length, word.length));
                if (editPercent > mEditPercent) {
                    match = word;
                    mEditPercent = editPercent;
                }
            }


            console.log(`${toMatch} matches ${match} with ${mEditPercent} certainty`);

            if (!(mEditPercent >= 0.75)) {
                return false;
            }

            numMatch++;
        }

        return numMatch >= 1;
    }

    processMsg(msg) {
        if (!this.players.has(msg.author.id)) {
            this.players.set(msg.author.id, new cmds.Player());
        }

        if (msg.content === "$q" && !this.isActive) {

            this.isActive = true;

            db.randomTossup(this.settings, tu => {
                this.activeQuestion = tu;
                this.buzzer = null;
                this.rawText = "";
                this.textList = tu.text.split(" ");
                this.textProgress = 0;
                this.isShowComplete = false;
                this.answerLine = tu.answer;
                this.isPower = tu.text.includes("*"); // only give power to qs having *

                this.teamsLockedOut.clear();
                this.playersLockedOut.clear();

                msg.channel.send("<question starting>").then(tm => {
                    this.targetMsg = tm;
                    this.showInterval = setInterval(showMore, this.showDelay, this);
                })
            });
        }

        else if (msg.content === "buzz" && this.isActive && this.buzzer === null && !this.isLockedOut(msg.author) && !this.isPaused) {
            this.players.get(msg.author.id).buzzes++;

            clearInterval(this.showInterval);
            clearTimeout(this.deadlineTimeout);

            this.buzzer = msg.author;

            msg.channel.send(`Buzz from ${msg.author.toString()}! ${this.bzTimeout / 1000}s to answer.`);

            this.buzzerDeadlineTimeout = setTimeout(stopBuzzer, this.bzTimeout, this);

            if (this.isShowComplete) {
                this.rawText += ":large_blue_diamond:";
            } else if (this.isPower) {
                this.rawText += ":grey_exclamation:";
            } else {
                this.rawText += ":bell:";
            }

            if (!this.isMultipleBuzzesEnabled) {
                this.playersLockedOut.add(msg.author.id);

                let team = this.players.get(msg.author.id).team;
                if (team !== -1) {
                    this.teamsLockedOut.add(team);
                }
            }
        }

        else if (msg.author === this.buzzer && this.isActive && !this.isPaused) {
            // TODO: update stats
            this.buzzer = null;
            clearTimeout(this.buzzerDeadlineTimeout);

            if (this.isCorrectAnswer(msg.content)) {
                clearTimeout(this.deadlineTimeout);
                this.rawText += `:white_check_mark:`;

                this.players.get(msg.author.id).correct++;

                if (this.isPower) {
                    addPoints(this, msg.author, 15);
                    msg.reply(`POWER! The answer was **${this.answerLine}**. +15 points`);

                    this.players.get(msg.author.id).powers++;
                } else {
                    addPoints(this, msg.author, 10);
                    msg.reply(`Correct! The answer was **${this.answerLine}**. +10 points`);
                }

                this.isActive = false;
                showMore(this);
            } else {
                this.rawText += ":x:";

                msg.reply(`Incorrect! ${this.isShowComplete ? "No penalty." : "-5 points"}`);

                if (!this.isShowComplete) {
                    addPoints(this, msg.author, -5);
                }

                this.restartQuestion();
            }
        }

        else if (msg.content === "skip" && this.isActive && this.buzzer === null && this.isSkippingEnabled) {
            stopQuestion(this);
        }

        else if (msg.content === "pause" && this.isActive && this.buzzer === null && !this.isPaused && this.isPausingEnabled) {
            this.isPaused = true;
            clearInterval(this.showInterval);
            clearTimeout(this.deadlineTimeout);
            msg.reply("Tossup paused!");
        }

        else if (msg.content === "resume" && this.isActive && this.buzzer === null && this.isPaused) {
            this.isPaused = false;
            this.restartQuestion();
        }

        else {
            cmds.exec(msg, this);
        }


    }

}


module.exports = {
    QBRoom
}