const db = require("./database")
const edist = require("levenshtein-edit-distance")

const showDelay = 1250;
const tuTimeout = 10000; // 10 sec
const bzTimeout = 10000; // 10 sec

function addPoints(room, player, n) {
    room.targetMsg.channel.send(`<@!${player.id}> ${n}`);
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
            room.showInterval = setInterval(showMore, showDelay, room);
        } else {
            room.deadlineTimeout = setTimeout(stopQuestion, tuTimeout, room);
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
                room.deadlineTimeout = setTimeout(stopQuestion, tuTimeout, room);
            }
            break;
        }
    }

    if (room.isPower) {
        room.isPower = !room.rawText.includes("*"); // check if we exited power
    }

    room.targetMsg.edit(room.rawText);

}

class Player {
    constructor() {
        this.points = 0;

        this.correct = 0; // includes powers
        this.powers = 0;
        this.buzzes = 0;
        // negs excluded as it is buzzes - correct.

        this.team = 0;
    }
}

class Team {
    constructor() {
        this.name = "";
        this.players = []; // list of discord ids
    }

}

class QBRoom {
    constructor() {
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
        this.showSpeed = 5;
        this.settings = new db.QuestionParams();
    }

    isLockedOut(player) {
        return false;
    }

    isCorrectAnswer(answer) {
        return true;
    }

    processMsg(msg) {
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

                msg.channel.send("<question starting>").then(tm => {
                    this.targetMsg = tm;
                    this.showInterval = setInterval(showMore, showDelay, this);
                })
            });
        }

        else if (msg.content === "buzz" && this.isActive && this.buzzer === null && !this.isLockedOut(msg.author) && !this.isPaused) {
            // TODO: getStats(msg.author).buzzes++;

            clearInterval(this.showInterval);
            clearTimeout(this.deadlineTimeout);

            this.buzzer = msg.author;

            msg.channel.send(`Buzz from ${msg.author.toString()}! ${bzTimeout / 1000}s to answer.`);

            this.buzzerDeadlineTimeout = setTimeout(stopBuzzer, bzTimeout, this);

            if (this.isShowComplete) {
                this.rawText += ":large_blue_diamond:";
            } else if (this.isPower) {
                this.rawText += ":grey_exclamation:";
            } else {
                this.rawText += ":bell:";
            }
        }

        else if (msg.author === this.buzzer && this.isActive && !this.isPaused) {
            // TODO: update stats
            this.buzzer = null;
            clearTimeout(this.buzzerDeadlineTimeout);

            if (this.isCorrectAnswer(msg.content)) {
                clearTimeout(this.deadlineTimeout);
                this.rawText += `:white_check_mark:`;

                if (this.isPower) {
                    addPoints(this, msg.author, 15);
                    msg.reply(`POWER! The answer was **${this.answerLine}**. +15 points`);
                } else {
                    addPoints(this, msg.author, 10);
                    msg.reply(`Correct! The answer was **${this.answerLine}**. +10 points`);
                }

                this.isActive = false;
            } else {
                this.rawText += ":x:";

                msg.reply(`Incorrect! ${this.isShowComplete ? "No penalty." : "-5 points"}`);

                if (!this.isShowComplete) {
                    addPoints(this, msg.author, -5);
                }

                msg.channel.send("<Question is resuming>").then(newMsg => {
                    this.targetMsg = newMsg;

                    if (this.isShowComplete) {
                        this.deadlineTimeout = setTimeout(stopQuestion, tuTimeout, this);
                    } else {
                        this.showInterval = setInterval(showMore, showDelay, this);
                    }
                })
            }
        }

        else if (msg.content === "skip" && this.isActive && this.buzzer === null) {
            stopQuestion(this);
        }

        else if (msg.content === "pause" && this.isActive && this.buzzer === null && !this.isPaused) {
            this.isPaused = true;
            clearInterval(this.showInterval);
            clearTimeout(this.deadlineTimeout);
        }

        else if (msg.content === "resume" && this.isActive && this.buzzer === null && this.isPaused) {
            this.isPaused = false;
            if (this.isShowComplete) {
                this.deadlineTimeout = setTimeout(stopQuestion, tuTimeout, this);
            } else {
                this.showInterval = setInterval(showMore, showDelay, this);
            }
        }
    }



}


module.exports = {
    QBRoom
}