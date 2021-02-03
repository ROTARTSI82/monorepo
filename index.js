const econ = require("./econ");

const log = require("./logger");

const { Client } = require('pg')
const client = new Client({
    user: 'postgres',
    host: '127.0.0.1',
    database: 'capitalistbotdb',
    password: 'password'
})

client.connect();



const Discord = require('discord.js');
const bot = new Discord.Client();

log.log(bot);

bot.on('ready', () => {
    console.log(`Logged in as ${bot.user.tag}!`);
    console.log(`Bot has started, with ${bot.users.size} users, in ${bot.channels.size} channels of ${bot.guilds.size} guilds.`);

    bot.user.setActivity("the upright organ");
    bot.generateInvite().then(link => {
            console.log(`Generated bot invite link: ${link}`);
        });
});



bot.on('message', msg => {
    if (msg.author.bot) {
        return; // ignore self & other bots
    }

    console.log(`[${new Date().toLocaleString()}] [${msg.guild} #${msg.channel}] [${msg.author.tag}]: ${msg.content}`);

    econ.runEcon(client, msg);

    // qb.runQb(qbCli, msg);

});


const fs = require('fs')

fs.readFile('./token.txt', 'utf8' , (err, data) => {
    if (err) {
        console.error("cannot locate bot token! Make sure ./token.txt is set correctly")
        console.error(err)
        return
    }

    console.info(data)
    bot.login(data);
})


