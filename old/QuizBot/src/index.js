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

    roomMap.get(msg.channel.id).processMsg(msg);
})

bot.on('ready', c => {
    console.log(`Ready ${bot.user.tag}`);

    bot.user.setPresence(config.presence).then(r => {console.log(r)});
});

bot.login(secret.token);
