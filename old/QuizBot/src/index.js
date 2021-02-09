const db = require("./database")
const qb = require("./quizbowl")
const fs = require('fs')

console.log = function() {}

let config = JSON.parse(fs.readFileSync("./res/config.json", 'utf8'));
let secret = JSON.parse(fs.readFileSync("./res/secret.json", 'utf8'));

db.init(secret);

let roomMap = new Map();

const Discord = require('discord.js')
const bot = new Discord.Client();

// const stdin = process.openStdin();
// stdin.addListener("data", d => {
//     if (d === "stop") {
//         db.stop();
//         bot.destroy();
//         process.exit(0);
//     }
// })


bot.on('message', msg => {
    if (!roomMap.has(msg.channel.id)) {
        roomMap.set(msg.channel.id, new qb.QBRoom());
    }

    if (msg.content === "$clear") {
        msg.reply(`Are you sure? This will reset EVERYONE's score and stats! (10sec to confirm)`).then(m => {
            m.react('✅').then(() => {m.react('❌')}).then(() => {

                const filter = (reaction, user) => {
                    // only allow original sender to confirm, allow anyone to abort.
                    return (reaction.emoji.name !== '✅' || msg.author.id === user.id) && !user.bot;
                };

                m.awaitReactions(filter, { max: 1, time: 10000, errors: ['time'] })
                    .then(collected => {
                        const reaction = collected.first();

                        if (reaction.emoji.name === '✅') {
                            roomMap.set(msg.channel.id, new qb.QBRoom());
                            msg.channel.send(`Cleared <#${msg.channel.id}>`);
                        } else {
                            msg.channel.send('Clear aborted!');
                        }
                    })
                    .catch(collected => {
                        msg.channel.send('Timed out! Clear aborted!');
                    });
            });
        });
    }
    roomMap.get(msg.channel.id).processMsg(msg);
})

bot.on('ready', c => {
    console.log(`Ready ${bot.user.tag}`);

    bot.user.setPresence(config.presence).then(r => {console.log(r)});
});

bot.login(secret.token);
