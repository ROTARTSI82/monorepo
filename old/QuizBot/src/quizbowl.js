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
        room.isTossupActive = false;
        room.buzzer = null;

        showMore(room);

        room.targetMsg.channel.send(`No points. The answer was **${room.answerLine}**`);
    }, 64);
}

function stopBuzzer(room) {
    clearTimeout(room.buzzerDeadlineTimeout);

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
    if (!room.isTossupActive) {
        limit = 9999; // complete show
    }

    for (let i = 0; i < limit; i++) {
        if (room.textProgress < room.textList.length) {
            room.rawText += room.textList[room.textProgress++] + " ";
        } else {
            room.isShowComplete = true;
            clearInterval(room.showInterval); // clear self.
            if (room.isTossupActive) {
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

function nextBonusPart(room) {
    clearInterval(room.showInterval);
    clearTimeout(room.deadlineTimeout);

    if (room.answerLine) {
        room.targetMsg.channel.send(`The answer was **${room.answerLine}**. No points.`);
    }

    room.isShowComplete = true;
    showMoreBonus(room);

    room.partIndex++;
    if (room.partIndex < room.activeBonus.parts.length) {
        console.log("nextpart");

        room.rawText = "";
        room.textList = room.activeBonus.parts[room.partIndex].text.split(" ");
        room.answerLine = room.activeBonus.parts[room.partIndex].answer;
        room.textProgress = 0;
        room.isShowComplete = false;

        room.targetMsg.channel.send("<bonus starting>").then(tm => {
            room.targetMsg = tm;
            room.showInterval = setInterval(showMoreBonus, room.bonusDelay, room);
        });
    } else {
        console.log("end");
        room.isBonusActive = false;
    }
}

function showMoreBonus(room) {
    let limit = room.showSpeed;

    if (room.isShowComplete) {
        limit = 99999;
    }

    console.log("showmore");
    for (let i = 0; i < limit; i++) {
        if (room.textProgress < room.textList.length) {
            room.rawText += room.textList[room.textProgress++] + " ";
        } else {
            clearInterval(room.showInterval);
            if (!room.isShowComplete) {
                // room.partIndex++;
                room.deadlineTimeout = setTimeout(nextBonusPart, room.partIndex === -1 ? 0 : room.bonusTimeout, room);
            }

            room.isShowComplete = true;
            break;
        }
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
        this.activeBonus = null;
        this.isTossupActive = false;
        this.isBonusActive = false;
        this.isPaused = false;
        this.isPower = false;
        this.isShowComplete = false;

        this.whoseBonus = null; // null = everyone, `${number}` for a team, `p${number}` for a player
        this.partIndex = -1;

        this.showInterval = 0;
        this.deadlineTimeout = 0;
        this.buzzerDeadlineTimeout = 0;

        this.buzzer = null;

        // settings
        this.showSpeed = 5;
        this.isPausingEnabled = true;
        this.doShowBonusAfterTu = true;
        this.isSkippingEnabled = true;
        this.isMultipleBuzzesEnabled = false;
        this.showDelay = 1250;
        this.bonusDelay = 1250;
        this.bonusTimeout = 10000;
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

    isTheirBonus(player) {
        if (this.whoseBonus === null) {
            return true;
        } else if (this.whoseBonus.startsWith("p")) {
            return player.id === this.whoseBonus.substr(1);
        } else {
            return this.players.get(player.id).team === parseInt(this.whoseBonus);
        }
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

    startBonus(msg) {
        this.isBonusActive = true;

        db.randomBonus(this.settings, bonus => {
            if (bonus === null) {
                this.isBonusActive = false;
                msg.reply(`No bonuses in QuizDB matched the provided criterion! Adjust your filters and settings.`);
                return;
            }

            this.activeBonus = bonus;
            this.rawText = "";
            this.textList = bonus.leadin.split(" ");
            this.answerLine = null;
            this.textProgress = 0;
            this.partIndex = -1;
            this.isShowComplete = false;

            msg.channel.send("<bonus starting>").then(tm => {
                this.targetMsg = tm;
                this.showInterval = setInterval(showMoreBonus, this.bonusDelay, this);
            });

        });
    }

    processMsg(msg) {
        if (!this.players.has(msg.author.id)) {
            this.players.set(msg.author.id, new cmds.Player());
        }

        if (msg.content === "$q" && !this.isTossupActive && !this.isBonusActive) {

            this.isTossupActive = true;

            db.randomTossup(this.settings, tu => {
                if (tu === null) {
                    this.isTossupActive = false;
                    msg.reply(`No tossups in QuizDB matched the provided criterion! Adjust your filters and settings.`);
                    return;
                }

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

        else if (msg.content === "buzz" && this.isTossupActive && this.buzzer === null && !this.isLockedOut(msg.author) && !this.isPaused) {
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

        else if (msg.author === this.buzzer && this.isTossupActive && !this.isPaused) {
            // TODO: update stats
            this.buzzer = null;
            clearTimeout(this.buzzerDeadlineTimeout);

            if (this.isCorrectAnswer(msg.content)) {
                let player = this.players.get(msg.author.id);

                clearTimeout(this.deadlineTimeout);
                this.rawText += `:white_check_mark:`;

                player.correct++;

                if (this.isPower) {
                    addPoints(this, msg.author, 15);
                    msg.reply(`POWER! The answer was **${this.answerLine}**. +15 points`);

                    player.powers++;
                } else {
                    addPoints(this, msg.author, 10);
                    msg.reply(`Correct! The answer was **${this.answerLine}**. +10 points`);
                }

                this.isTossupActive = false;
                showMore(this);

                if (this.doShowBonusAfterTu) {
                    if (player.team !== -1) {
                        this.whoseBonus = `${player.team}`;
                    } else {
                        this.whoseBonus = `p${msg.author.id}`;
                    }

                    this.startBonus(msg);
                }
            } else {
                this.rawText += ":x:";

                msg.reply(`Incorrect! ${this.isShowComplete ? "No penalty." : "-5 points"}`);

                if (!this.isShowComplete) {
                    addPoints(this, msg.author, -5);
                }

                this.restartQuestion();
            }
        }

        else if (msg.content === "$b" && !this.isBonusActive && !this.isTossupActive) {
            this.whoseBonus = null;
            this.startBonus(msg);
        }

        else if (this.isBonusActive && this.answerLine && msg.content === msg.content.toUpperCase()) {
            if (!this.isTheirBonus(msg.author)) {
                msg.react("❌");
                return;
            }

            clearInterval(this.showInterval);
            clearTimeout(this.deadlineTimeout);

            this.players.get(msg.author.id).boniInterrupted++; // THESE STATS ARE TRACKED BUT NOT REPORTED

            if (this.isCorrectAnswer(msg.content)) {
                msg.reply(`Correct! The answer was **${this.answerLine}**. +10 points`);
                this.rawText += `:white_check_mark:`;

                this.players.get(msg.author.id).bonusPoints += 10;
                this.players.get(msg.author.id).boniCorrect++;  // THESE STATS ARE TRACKED BUT NOT REPORTED
            } else {
                msg.reply(`Incorrect! The answer was **${this.answerLine}**. No penalty.`);
                this.rawText += ":x:";
            }

            // null answerLine to prevent nextBonusPart from printing it again with "no points" message
            this.answerLine = null;
            nextBonusPart(this);
        }

        else if (msg.content === "skip" && this.isTossupActive && this.buzzer === null && this.isSkippingEnabled) {
            this.rawText += ":fast_forward:";
            stopQuestion(this);
        }

        else if (msg.content === "skip" && this.isBonusActive && this.isSkippingEnabled) {
            clearInterval(this.showInterval);
            clearTimeout(this.deadlineTimeout);

            this.rawText += ":fast_forward:";
            nextBonusPart(this);
        } else if (msg.content === "skip all" && this.isBonusActive && this.isSkippingEnabled) {
            clearInterval(this.showInterval);
            clearTimeout(this.deadlineTimeout);

            this.rawText += ":fast_forward:";

            if (this.answerLine) {
                this.targetMsg.channel.send(`Times up! The answer was **${this.answerLine}**. No points.`);
            }

            this.isShowComplete = true;
            showMoreBonus(this);

            this.partIndex++;
            for (; this.partIndex < this.activeBonus.parts.length; this.partIndex++) {
                this.targetMsg.channel.send(this.activeBonus.parts[this.partIndex].text);
                this.targetMsg.channel.send(`Answer: **${this.activeBonus.parts[this.partIndex].answer}**`);
            }

            this.isBonusActive = false;
        }

        else if (msg.content === "pause" && this.isBonusActive && !this.isPaused && this.isPausingEnabled) {
            this.rawText += ":pause_button:";
            this.isPaused = true;
            clearInterval(this.showInterval);
            clearTimeout(this.deadlineTimeout);
            msg.reply("bonus paused!");
        }

        else if (msg.content === "resume" && this.isBonusActive && this.isPaused) {
            this.isPaused = false;
            this.targetMsg.channel.send("<bonus is resuming>").then(newMsg => {
                this.targetMsg = newMsg;

                if (this.isShowComplete) {
                    this.deadlineTimeout = setTimeout(nextBonusPart, this.partIndex === -1 ? 0 : this.bonusTimeout, this);
                } else {
                    this.showInterval = setInterval(showMoreBonus, this.bonusDelay, this);
                }
            });
        }

        else if (msg.content === "pause" && this.isTossupActive && this.buzzer === null && !this.isPaused && this.isPausingEnabled) {
            this.rawText += ":pause_button:";
            this.isPaused = true;
            clearInterval(this.showInterval);
            clearTimeout(this.deadlineTimeout);
            msg.reply("Tossup paused!");
        }

        else if (msg.content === "resume" && this.isTossupActive && this.buzzer === null && this.isPaused) {
            this.isPaused = false;
            this.restartQuestion();
        }

        else if (msg.content === "$info tossup" && !this.isTossupActive && this.activeQuestion) {
            msg.reply(`${this.activeQuestion.toString()}`);
        }

        else if (msg.content === "$info bonus" && !this.isBonusActive && this.activeBonus) {
            msg.reply(`${this.activeBonus.toString()}`);
        }

        else {
            cmds.exec(msg, this);
        }


    }

}


module.exports = {
    QBRoom
}