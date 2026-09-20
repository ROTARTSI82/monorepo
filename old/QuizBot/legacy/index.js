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

function getPosition(string, subString, index) {
    return string.split(subString, index).join(subString).length;
}

bot.on('ready', c => {
    console.log(`Logged in as ${bot.user.tag}!`);
    console.log(`Bot has started, with ${bot.users.size} users, in ${bot.channels.size} channels of ${bot.guilds.size} guilds.`);

    bot.user.setActivity("the upright organ");
    bot.generateInvite().then(link => {
            console.log(`Generated bot invite link: ${link}`);
        });


    var stdin = process.openStdin();

    stdin.addListener("data", function(d) {
        // note:  d is an object, and when converted to a string it will
        // end with a linefeed.  so we (rather crudely) account for that
        // with toString() and then trim()


        let cmd = d.toString().trim();
        if (cmd.startsWith('a')) { // all channels
            bot.channels.cache.each((v,k,m) => {
                if (v.type === 'text') {
                    bot.channels.cache.get(v.id).send(cmd.substring(1));
                }
            });
        }

        if (cmd.startsWith('g')) {
            bot.guilds.cache.get(cmd.split(' ')[1]).channels.cache.each((v,k,m) => {
                if (v.type === 'text') {
                    bot.channels.cache.get(v.id).send(cmd.substring(getPosition(cmd, ' ', 2)));
                }
            });
        }

        if (cmd.startsWith('c')) {
            bot.channels.cache.get(cmd.split(' ')[1]).send(cmd.substring(getPosition(cmd, ' ', 2)))
        }

    });

});



bot.on('message', msg => {
    if (msg.author.bot) {
        return; // ignore self & other bots
    }

    console.log(`[${new Date().toLocaleString()}] [${msg.guild} ${msg.channel}] [${msg.author.tag}]: ${msg.content}`);

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



