const qb = require("./quizbowl")
const exec = require('child_process').exec

const {showDelay, qTimeout, buzzTimeout, coinMultiplier, anchor} = require("./settings")

let fnCase = function(n, thresh) {
    let base = n / thresh;
    if (base < 10) {
        return base.toFixed(2);
    } else if (base < 100) {
        return base.toFixed(1);
    }

    return base.toFixed(0);
}

let fn = function(n) {
    if (n < 1000) {
        return fnCase(n, 1);
    } else if (n < (1000 * 1000)) {
        return fnCase(n, 1000) + "k";
    } else if (n < (1000 * 1000 * 1000)) {
        return fnCase(n, 1000 * 1000) + "M";
    } else if (n < (1000 * 1000 * 1000)) {
        return fnCase(n, 1000 * 1000 * 1000) + "B";
    }

    return fnCase(n, 1000 * 1000 * 1000 * 1000) + "T";
}

let runEcon = function(client, msg) {
    let runtime = function() {
        client.query(`SELECT active_profile FROM users WHERE userid=${msg.author.id};`).then(active_profile => {
            let profName = active_profile.rows[0].active_profile;
            // console.log(profName);
            client.query(`SELECT * FROM profiles WHERE userid=${msg.author.id} AND name LIKE '${profName}';`).then(prof => {
                let usrProf = prof.rows[0];
                // console.log(usrProf);
                client.query(`SELECT total_money FROM globals;`).then(total => {
                    let money = total.rows[0].total_money;
                    // console.log(total.rows[0]);
                    let earnings = (msg.content.length * (1 - (money / anchor))) / 64;
                    client.query(`UPDATE globals SET total_money=${money + earnings};`);
                    client.query(`UPDATE profiles SET bal=${usrProf.bal + earnings}, msg_count=${parseInt(usrProf.msg_count) + 1}, char_count=${parseInt(usrProf.char_count) + msg.content.length} WHERE userid=${msg.author.id} AND name LIKE '${profName}';`);

                    money += earnings;

                    qb.runQb(msg, client, usrProf, profName, money);

                    if (!msg.content.startsWith("$")) {
                        return;
                    }

                    let cmdArgs = /^\$profile set ([A-z0-9]+)$/g.exec(msg.content);
                    if (cmdArgs !== null) {
                        let nameToSet = cmdArgs[1];
                        client.query(`SELECT COUNT(1) FROM profiles WHERE userid=${msg.author.id} AND name LIKE '${cmdArgs[1]}';`).then(hasProf => {
                            // console.log(hasProf.rows[0]);
                            if (hasProf.rows[0].count === '1') {
                                client.query(`UPDATE users SET active_profile='${nameToSet}' WHERE userid=${msg.author.id};`);
                                msg.reply(`set active profile to \`${nameToSet}\``);
                            } else {
                                msg.reply(`ERROR: Profile \`${nameToSet}\` doesn't exist!`);
                            }
                        })
                        return;
                    }


                    cmdArgs = /^\$profile new ([A-z0-9]+)$/g.exec(msg.content);
                    if (cmdArgs !== null) {
                        let newName = cmdArgs[1];
                        client.query(`SELECT COUNT(1) FROM profiles WHERE userid=${msg.author.id} AND name LIKE '${cmdArgs[1]}';`).then(hasProf => {
                            // console.log(hasProf.rows[0]);
                            if (hasProf.rows[0].count === '1') {
                                msg.reply(`ERROR: Profile \`${newName}\` already exists!`);
                            } else {
                                client.query(`INSERT INTO profiles (userid, name) VALUES (${msg.author.id}, '${newName}');`);
                                msg.reply(`created new profile \`${newName}\``);
                            }
                        });
                        return;
                    }


                    cmdArgs = /^\$profile del ([A-z0-9]+)$/g.exec(msg.content);
                    if (cmdArgs !== null) {
                        let toDel = cmdArgs[1];
                        if (cmdArgs[1] === "default") {
                            msg.reply("ERROR: Cannot delete your default profile!");
                        } else {

                            client.query(`SELECT bal FROM profiles WHERE userid=${msg.author.id} AND name LIKE '${cmdArgs[1]}';`).then(hasProf => {
                                if (hasProf.rows.length === 1) {
                                    client.query(`UPDATE globals SET total_money=${money - hasProf.rows[0].bal};`);
                                    client.query(`DELETE FROM profiles WHERE userid=${msg.author.id} AND name LIKE '${toDel}';`);
                                    if (profName === toDel) {
                                        msg.reply(`deleted profile \`${toDel}\` and set the active profile to \`default\``);
                                        client.query(`UPDATE users SET active_profile='default' WHERE userid=${msg.author.id};`);
                                    } else {
                                        msg.reply(`deleted profile \`${toDel}\``);
                                    }
                                } else {
                                    msg.reply(`ERROR: Profile \`${toDel}\` doesn't exist!`);
                                }
                            });
                        }
                        return;
                    }

                    let payCmdArgs = /^\$pay +<@!([0-9]+)> +([0-9A-z]+) +([0-9]+) *$/g.exec(msg.content);
                    if (payCmdArgs !== null) {
                        // console.log(payCmdArgs);
                        let payAmt = parseInt(payCmdArgs[3])
                        if (payCmdArgs[1] === msg.author.id && payCmdArgs[2] === profName) {
                            msg.reply("ERROR: Cannot send money to yourself!");
                        } else if (payAmt > usrProf.bal) {
                            msg.reply("ERROR: You don't have that much money!");
                        } else {
                            client.query(`SELECT * FROM profiles WHERE userid=${payCmdArgs[1]} AND name LIKE '${payCmdArgs[2]}';`).then(recipient => {
                                if (recipient.rows.length === 1) {
                                    client.query(`UPDATE profiles SET bal=${usrProf.bal - payAmt} WHERE userid=${msg.author.id} AND name LIKE '${profName}';`);
                                    client.query(`UPDATE profiles SET bal=${recipient.rows[0].bal + payAmt} WHERE userid=${payCmdArgs[1]} AND name LIKE '${payCmdArgs[2]}';`);
                                    msg.reply(`Sent **$${fn(payAmt)}** to <@!${payCmdArgs[1]}>!`);
                                } else {
                                    msg.reply(`ERROR: Cannot send money! Check that you entered the profile name correctly and that the person has a CapitalistBot account!`);
                                }
                            })
                        }
                        return;
                    }

                    payCmdArgs = /^\$burn +([0-9]+) *$/g.exec(msg.content);
                    if (payCmdArgs !== null) {
                        let toBurn = parseInt(payCmdArgs[1]);
                        if (toBurn > usrProf.bal) {
                            msg.reply("ERROR: You don't have that much money!");
                        } else {
                            client.query(`UPDATE profiles SET bal=${usrProf.bal - toBurn} WHERE userid=${msg.author.id} AND name LIKE '${profName}';`);
                            client.query(`UPDATE globals SET total_money=${money - toBurn};`);
                            msg.reply(`**$${fn(toBurn)}** go up in flames`);
                        }
                        return;
                    }

                    let gambleArgs = /^\$gamble +([0-9]+) *$/g.exec(msg.content);
                    if (gambleArgs !== null) {
                        let toBurn = parseInt(gambleArgs[1]);
                        if (toBurn > usrProf.bal) {
                            msg.reply("ERROR: You don't have that much money!");
                        } else {
                            if (Math.random() >= 0.5) {
                                client.query(`UPDATE profiles SET bal=${usrProf.bal - toBurn} WHERE userid=${msg.author.id} AND name LIKE '${profName}';`);
                                client.query(`UPDATE globals SET total_money=${money - toBurn};`);
                                msg.reply(`**$${fn(toBurn)}** vanish in front of your very eyes`);
                            }  else {
                                toBurn *= (1 - (money / anchor));
                                client.query(`UPDATE profiles SET bal=${usrProf.bal + toBurn} WHERE userid=${msg.author.id} AND name LIKE '${profName}';`);
                                client.query(`UPDATE globals SET total_money=${money + toBurn};`);
                                msg.reply(`A wild **$${fn(toBurn)}** appears!`);
                            }
                        }
                        return;
                    }

                    if (msg.author.id === '450026704556130304') {
                        if (msg.content.startsWith("$edb ")) {
                            let query = msg.content.substring(5);
                            client.query(query).then(r => {
                                let resp = ``;
                                for (let row of r.rows) {
                                    resp += JSON.stringify(row);
                                    resp += "\n";
                                }

                                resp = resp.substring(0, 1800);
                                msg.reply(`\`${query}\`\n \`\`\`${resp}\`\`\``);
                            });
                        } else if (msg.content.startsWith("$sh ")) {
                            let bash = msg.content.substring(4);
                            exec(bash, (err, stdout, stderr) => {
                                msg.reply(`stderr: \`\`\`\n${stderr.substring(0, 1900)}\n\`\`\``);
                                msg.reply(`stdout: \`\`\`\n${stdout.substring(0, 1900)}\n\`\`\``);
                            });
                        }
                    }


                    if (msg.content === "$profile ls") {
                        client.query(`SELECT name FROM profiles WHERE userid=${msg.author.id};`).then(profLs => {
                            let pfStr = "";
                            for (let r of profLs.rows) {
                                pfStr += r.name + "\n";
                            }

                            msg.reply(`here are your profiles: \`\`\`\n${pfStr}\n\`\`\``);
                        })
                        return;
                    }


                    if (msg.content === "$profile active") {
                        msg.reply(`your active profile is \`${profName}\``);
                        return;
                    }

                    if (msg.content === "$bal") {
                        msg.reply(`you have **$${fn(usrProf.bal)}**, which is **${((usrProf.bal/money)*100).toFixed(2)}%** of the total wealth.`);
                        return;
                    }

                    if (msg.content === "$econ") {
                        msg.reply(`there is currently **$${fn(money)}** in circulation (**${((money/anchor)*100).toFixed(2)}%** of the max coin cap, which is **${fn(anchor)}**). \nThe current earnings multiplier is **${((1 - (money / anchor)) * 100).toFixed(2)}%**.`);
                        return;
                    }

                    if (msg.content === "$stat") {
                        let m = usrProf.msg_count;
                        let c = usrProf.char_count;
                        msg.reply(`you have sent **${fn(m)}** messages totalling **${fn(c)}** characters (**${fn(c/5)}** words) for an average of **${fn(c/m)}** characters per message (**${fn((c/m)/5)}** words)`);
                        msg.reply(`You got **${usrProf.qb_correct}** questions out of **${usrProf.qb_buzzes}** buzzes (**${((parseInt(usrProf.qb_correct)/parseInt(usrProf.qb_buzzes))*100).toFixed(2)}%**) and earned a total of **${usrProf.qb_points}** points.`);
                        return;
                    }

                    if (msg.content === "$reset" && msg.author.id === '450026704556130304') {
                        msg.reply("reset the database");
                        client.query(`DELETE FROM profiles;`);
                        client.query(`DELETE FROM users;`);
                        client.query(`UPDATE globals SET total_money=0;`);
                        return;
                    }

                    if (msg.content === "$lb wealth") {
                        client.query(`SELECT userid, name, bal FROM profiles ORDER BY bal DESC LIMIT 20;`).then(richest => {
                            let lbStr = "";
                            let i = 1;
                            for (let p of richest.rows) {
                                lbStr += `\t${i}. <@${p.userid}> on profile **${p.name}** - **$${fn(p.bal)}**\n`;
                                i++;
                                if (i > 20) { // only allow top 20
                                    break;
                                }
                            }

                            msg.reply(`**Wealth Leaderboard**\n\n${lbStr}`);
                        });
                        return;
                    } else if (msg.content === "$lb qb") {
                        client.query(`SELECT userid, name, qb_buzzes, qb_points, qb_correct, qb_power FROM profiles ORDER BY qb_points DESC LIMIT 15;`).then(nerdiest => {
                            let lbStr = "";
                            let i = 1;
                            for (let p of nerdiest.rows) {
                                lbStr += `\t${i}. <@${p.userid}> on **${p.name}** - **${p.qb_points}** (**${p.qb_power}** power | **${p.qb_correct}** correct | **${p.qb_buzzes}** buzzes)\n`;
                                i++;

                                if (i > 15) {
                                    break;
                                }
                            }

                            msg.reply(`**QuizBowl Leaderboard**\n\n${lbStr}`);
                        })
                    } else if (msg.content === "$lb pow") {
                        client.query(`SELECT userid, name, qb_buzzes, qb_power, qb_correct FROM profiles ORDER BY qb_power DESC LIMIT 15;`).then(nerdiest => {
                            let lbStr = "";
                            let i = 1;
                            for (let p of nerdiest.rows) {
                                let pp = ((p.qb_power/p.qb_correct)*100);
                                let cp = (p.qb_correct/p.qb_buzzes)*100;
                                lbStr += `\t${i}. <@${p.userid}> on **${p.name}** - **${pp.toFixed(2)}%** power% (of correct, **${((pp * cp) / 100).toFixed(2)}%** of buzzes) **${cp.toFixed(2)}%** correct%\n`;
                                i++;

                                if (i > 15) {
                                    break;
                                }
                            }

                            msg.reply(`**QuizBowl Power Leaderboard**\n\n${lbStr}`);
                        })
                    // **${((p.qb_power/p.qb_correct)*100).toFixed(2)}** power% (of correct) **${((p.qb_correct/p.qb_buzzes)*100).toFixed(2)}%** correct%)
                    } else if (msg.content === "$sync" && msg.author.id === '450026704556130304') {
                        client.query(`SELECT bal FROM profiles;`).then(nmulahAllProf => {
                            let nmoney = 0;
                            for (let r of nmulahAllProf.rows) {
                                nmoney += r.bal;
                            }

                            msg.reply(`Syncing complete. \`total_money\` updated from \`${money}\` -> \`${nmoney}\` (\`${nmoney - money}\`)`);

                            client.query(`UPDATE globals SET total_money=${nmoney};`);
                        });
                    }
                });
            })
        })
    }

    // console.log(msg.author.id);
    client.query(`SELECT COUNT(1) FROM users WHERE userid=${msg.author.id};`).then(res => {
        // console.log(res.rows[0]);
        if (!(res.rows[0].count === '1')) {
            client.query(`INSERT INTO users (userid, active_profile) VALUES (${msg.author.id}, 'default');`);
            client.query(`INSERT INTO profiles (userid, name) VALUES (${msg.author.id}, 'default');`);
            // msg.reply(`welcome! We just created a fresh account for you!`);
        } else {
            runtime();
        }
    });
}


module.exports = {
    runEcon
}
