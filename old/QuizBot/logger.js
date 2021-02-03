
function log(client) {
// channelCreate
    /* Emitted whenever a channel is created.
    PARAMETER    TYPE        DESCRIPTION
    channel      Channel     The channel that was created    */
    client.on("channelCreate", function (channel) {
        console.log(`channelCreate @ ${new Date().toLocaleString()}: ${channel}`);
    });

// channelDelete
    /* Emitted whenever a channel is deleted.
    PARAMETER   TYPE      DESCRIPTION
    channel     Channel   The channel that was deleted    */
    client.on("channelDelete", function (channel) {
        console.log(`channelDelete @ ${new Date().toLocaleString()}: ${channel} on ${channel.guild}`);
    });

// channelPinsUpdate
    /* Emitted whenever the pins of a channel are updated. Due to the nature of the WebSocket event, not much information can be provided easily here - you need to manually check the pins yourself.
    PARAMETER    TYPE         DESCRIPTION
    channel      Channel      The channel that the pins update occurred in
    time         Date         The time of the pins update    */
    client.on("channelPinsUpdate", function (channel, time) {
        console.log(`channelPinsUpdate @ ${time.toLocaleString()}: ${channel} on server ${channel.guild} in ${channel}`);
    });

// channelUpdate
    /* Emitted whenever a channel is updated - e.g. name change, topic change.
    PARAMETER        TYPE        DESCRIPTION
    oldChannel       Channel     The channel before the update
    newChannel       Channel     The channel after the update    */
    client.on("channelUpdate", function (oldChannel, newChannel) {
        console.log(`channelUpdate @ ${new Date().toLocaleString()} on ${oldChannel} -> ${newChannel} on server ${newChannel.guild}`);
    });

// clientUserGuildSettingsUpdate
    /* Emitted whenever the client user's settings update.
    PARAMETER                  TYPE                       DESCRIPTION
    clientUserGuildSettings    ClientUserGuildSettings    The new client user guild settings    */
    client.on("clientUserGuildSettingsUpdate", function (clientUserGuildSettings) {
        console.log(`clientUserGuildSettingsUpdate @ ${new Date().toLocaleString()} -> client user's settings update | ${clientUserGuildSettings}`);
    });

// clientUserSettingsUpdate
    /* Emitted when the client user's settings update.
    PARAMETER             TYPE                  DESCRIPTION
    clientUserSettings    ClientUserSettings    The new client user settings    */
    client.on("clientUserSettingsUpdate", function (clientUserSettings) {
        console.log(`clientUserSettingsUpdate @ ${new Date().toLocaleString()} -> client user's settings update | ${clientUserSettings}`);
    });

// debug
    /* Emitted for general debugging information.
    PARAMETER    TYPE         DESCRIPTION
    info         string       The debug information    */
    // client.on("debug", function (info) {
    //     console.log(`debug @ ${new Date().toLocaleString()} -> ${info}`);
    // });

// disconnect
    /* Emitted when the client's WebSocket disconnects and will no longer attempt to reconnect.
    PARAMETER    TYPE              DESCRIPTION
    Event        CloseEvent        The WebSocket close event    */
    client.on("disconnect", function (event) {
        console.log(`disconnect @ ${new Date().toLocaleString()} The WebSocket has closed and will no longer attempt to reconnect | ${event}`);
    });

// emojiCreate
    /* Emitted whenever a custom emoji is created in a guild.
    PARAMETER    TYPE          DESCRIPTION
    emoji        Emoji         The emoji that was created    */
    client.on("emojiCreate", function (emoji) {
        console.log(`emojiCreate @ ${new Date().toLocaleString()}: ${emoji.name} on ${emoji.guild}`);
    });

// emojiDelete
    /* Emitted whenever a custom guild emoji is deleted.
    PARAMETER    TYPE         DESCRIPTION
    emoji        Emoji        The emoji that was deleted    */
    client.on("emojiDelete", function (emoji) {
        console.log(`emojiDelete @ ${new Date().toLocaleString()}: ${emoji.name} on ${emoji.guild}`);
    });

// emojiUpdate
    /* Emitted whenever a custom guild emoji is updated.
    PARAMETER    TYPE       DESCRIPTION
    oldEmoji     Emoji      The old emoji
    newEmoji     Emoji      The new emoji    */
    client.on("emojiUpdate", function (oldEmoji, newEmoji) {
        console.log(`emojiDelete @ ${new Date().toLocaleString()}: ${oldEmoji.name} -> ${newEmoji.name} on ${newEmoji.guild}`);
    });

// error
    /* Emitted whenever the client's WebSocket encounters a connection error.
    PARAMETER    TYPE     DESCRIPTION
    error        Error    The encountered error    */
    client.on("error", function (error) {
        console.error(`error @ ${new Date().toLocaleString()}: ${error}`);
    });

// guildBanAdd
    /* Emitted whenever a member is banned from a guild.
    PARAMETER    TYPE          DESCRIPTION
    guild        Guild         The guild that the ban occurred in
    user         User          The user that was banned    */
    client.on("guildBanAdd", function (guild, user) {
        console.log(`guildBanAdd @ ${new Date().toLocaleString()}: ${user.tag} was banned from ${guild}`);
    });

// guildBanRemove
    /* Emitted whenever a member is unbanned from a guild.
    PARAMETER    TYPE         DESCRIPTION
    guild        Guild        The guild that the unban occurred in
    user         User         The user that was unbanned    */
    client.on("guildBanRemove", function (guild, user) {
        console.log(`guildBanRemove @ ${new Date().toLocaleString()}: ${user.tag} was unbanned from ${guild}`);
    });

// guildCreate
    /* Emitted whenever the client joins a guild.
    PARAMETER    TYPE         DESCRIPTION
    guild        Guild        The created guild    */
    client.on("guildCreate", function (guild) {
        console.log(`guildCreate @ ${new Date().toLocaleString()}: Joined ${guild}`);
    });

// guildDelete
    /* Emitted whenever a guild is deleted/left.
    PARAMETER    TYPE         DESCRIPTION
    guild        Guild        The guild that was deleted    */
    client.on("guildDelete", function (guild) {
        console.log(`guildDelete @ ${new Date().toLocaleString()}: Left ${guild}`);
    });

// guildMemberAdd
    /* Emitted whenever a user joins a guild.
    PARAMETER     TYPE               DESCRIPTION
    member        GuildMember        The member that has joined a guild    */
    client.on("guildMemberAdd", function (member) {
        console.log(`guildMemberAdd @ ${new Date().toLocaleString()}: ${member.tag} joins ${member.guild}`);
    });

// guildMemberAvailable
    /* Emitted whenever a member becomes available in a large guild.
    PARAMETER     TYPE               DESCRIPTION
    member        GuildMember        The member that became available    */
    client.on("guildMemberAvailable", function (member) {
        console.log(`guildMemberAvailable @ ${new Date().toLocaleString()}: ${member.tag} becomes available on ${member.guild}`);
    });

// guildMemberRemove
    /* Emitted whenever a member leaves a guild, or is kicked.
    PARAMETER     TYPE               DESCRIPTION
    member        GuildMember        The member that has left/been kicked from the guild    */
    client.on("guildMemberRemove", function (member) {
        console.log(`guildMemberRemove @ ${new Date().toLocaleString()}: ${member.tag} leaves ${member.guild}`);
    });

// guildMembersChunk
    /* Emitted whenever a chunk of guild members is received (all members come from the same guild).
    PARAMETER      TYPE                      DESCRIPTION
    members        Array<GuildMember>        The members in the chunk
    guild          Guild                     The guild related to the member chunk    */
    client.on("guildMembersChunk", function (members, guild) {
        console.error(`guildMembersChunk @ ${new Date().toLocaleString()}: a chunk of guild members is received on ${guild}`);
    });

// guildMemberSpeaking
    /* Emitted once a guild member starts/stops speaking.
    PARAMETER     TYPE                DESCRIPTION
    member        GuildMember         The member that started/stopped speaking
    speaking      boolean             Whether or not the member is speaking    */
    client.on("guildMemberSpeaking", function (member, speaking) {
        console.log(`guildMemberSpeaking @ ${new Date().toLocaleString()}: ${member.tag} speaking=${speaking}`);
    });
// guildMemberUpdate
    /* Emitted whenever a guild member changes - i.e. new role, removed role, nickname.
    PARAMETER    TYPE               DESCRIPTION
    oldMember    GuildMember        The member before the update
    newMember    GuildMember        The member after the update    */
    client.on("guildMemberUpdate", function (oldMember, newMember) {
        console.error(`guildMemberUpdate @ ${new Date().toLocaleString()}: ${oldMember} -> ${newMember}`);
    });

// guildUnavailable
    /* Emitted whenever a guild becomes unavailable, likely due to a server outage.
    PARAMETER    TYPE          DESCRIPTION
    guild        Guild         The guild that has become unavailable    */
    client.on("guildUnavailable", function (guild) {
        console.error(`guildUnavailable @ ${new Date().toLocaleString()}: ${guild}`);
    });

// guildUpdate
    /* Emitted whenever a guild is updated - e.g. name change.
    PARAMETER     TYPE      DESCRIPTION
    oldGuild      Guild     The guild before the update
    newGuild      Guild     The guild after the update    */
    client.on("guildUpdate", function (oldGuild, newGuild) {
        console.error(`guildUpdate @ ${new Date().toLocaleString()}: ${oldGuild} -> ${newGuild}`);
    });

// messageDelete
    /* Emitted whenever a message is deleted.
    PARAMETER      TYPE           DESCRIPTION
    message        Message        The deleted message    */
    client.on("messageDelete", function (msg) {
        console.log(`messageDelete @ ${new Date().toLocaleString()} [${msg.guild} #${msg.channel}] [${msg.author.tag}]: ${msg.content}`);
    });

// messageDeleteBulk
    /* Emitted whenever messages are deleted in bulk.
    PARAMETER    TYPE                              DESCRIPTION
    messages     Collection<Snowflake, Message>    The deleted messages, mapped by their ID    */
    client.on("messageDeleteBulk", function (messages) {
        console.log(`messageDeleteBulk @ ${new Date().toLocaleString()} -> ${messages}`);
    });

// messageReactionAdd
    /* Emitted whenever a reaction is added to a message.
    PARAMETER              TYPE                   DESCRIPTION
    messageReaction        MessageReaction        The reaction object
    user                   User                   The user that applied the emoji or reaction emoji     */
    client.on("messageReactionAdd", function (messageReaction, user) {
        let msg = messageReaction.message;
        console.log(`messageReactionAdd @ ${new Date().toLocaleString()}: ${messageReaction.emoji.name} by ${user.tag} on [${msg.guild} #${msg.channel}] [${msg.author.tag}]: ${msg.content}`);
    });

// messageReactionRemove
    /* Emitted whenever a reaction is removed from a message.
    PARAMETER              TYPE                   DESCRIPTION
    messageReaction        MessageReaction        The reaction object
    user                   User                   The user that removed the emoji or reaction emoji     */
    client.on("messageReactionRemove", function (messageReaction, user) {
        let msg = messageReaction.message;
        console.log(`messageReactionRemove @ ${new Date().toLocaleString()}: ${messageReaction.emoji.name} by ${user.tag} on [${msg.guild} #${msg.channel}] [${msg.author.tag}]: ${msg.content}`);
    });

// messageReactionRemoveAll
    /* Emitted whenever all reactions are removed from a message.
    PARAMETER          TYPE           DESCRIPTION
    message            Message        The message the reactions were removed from    */
    client.on("messageReactionRemoveAll", function (msg) {
        console.log(`messageReactionRemoveAll @ ${new Date().toLocaleString()} [${msg.guild} #${msg.channel}] [${msg.author.tag}]: ${msg.content}`);
    });

// messageUpdate
    /* Emitted whenever a message is updated - e.g. embed or content change.
    PARAMETER     TYPE           DESCRIPTION
    oldMessage    Message        The message before the update
    newMessage    Message        The message after the update    */
    client.on("messageUpdate", function (oldMessage, msg) {
        if (oldMessage.author.bot) {
            return; // Ignore bots.
        }

        console.log(`messageUpdate @ ${new Date().toLocaleString()} [${msg.guild} #${msg.channel}] [${msg.author.tag}]: "${oldMessage.content}" -> "${msg.content}"`);
    });

// presenceUpdate
    /* Emitted whenever a guild member's presence changes, or they change one of their details.
    PARAMETER    TYPE               DESCRIPTION
    oldMember    GuildMember        The member before the presence update
    newMember    GuildMember        The member after the presence update    */
    // client.on("presenceUpdate", function (oldMember, newMember) {
    //     console.log(`presenceUpdate @ ${new Date().toLocaleString()}: ${oldMember} -> ${newMember}`);
    // });


// reconnecting
    /* Emitted whenever the client tries to reconnect to the WebSocket.    */
    client.on("reconnecting", function () {
        console.log(`reconnecting @ ${new Date().toLocaleString()}`);
    });

// resume
    /* Emitted whenever a WebSocket resumes.
    PARAMETER    TYPE          DESCRIPTION
    replayed     number        The number of events that were replayed    */
    client.on("resume", function (replayed) {
        console.log(`resume @ ${new Date().toLocaleString()}: whenever a WebSocket resumes, ${replayed} replays`);
    });

// roleCreate
    /* Emitted whenever a role is created.
    PARAMETER    TYPE        DESCRIPTION
    role         Role        The role that was created    */
    client.on("roleCreate", function (role) {
        console.error(`roleCreate @ ${new Date().toLocaleString()}: ${role}`);
    });

// roleDelete
    /* Emitted whenever a guild role is deleted.
    PARAMETER    TYPE        DESCRIPTION
    role         Role        The role that was deleted    */
    client.on("roleDelete", function (role) {
        console.error(`roleDelete @ ${new Date().toLocaleString()}: ${role}`);
    });

// roleUpdate
    /* Emitted whenever a guild role is updated.
    PARAMETER      TYPE        DESCRIPTION
    oldRole        Role        The role before the update
    newRole        Role        The role after the update    */
    client.on("roleUpdate", function (oldRole, newRole) {
        console.error(`roleUpdate @ ${new Date().toLocaleString()}: ${oldRole} -> ${newRole}`);
    });

// typingStart
    /* Emitted whenever a user starts typing in a channel.
    PARAMETER      TYPE            DESCRIPTION
    channel        Channel         The channel the user started typing in
    user           User            The user that started typing    */
    // client.on("typingStart", function (channel, user) {
    //     console.log(`typingStart @ ${new Date().toLocaleString()}: ${user.tag} has started typing on ${channel} (guild=${channel.guild})`);
    // });

// typingStop
    /* Emitted whenever a user stops typing in a channel.
    PARAMETER       TYPE           DESCRIPTION
    channel         Channel        The channel the user stopped typing in
    user            User           The user that stopped typing    */
    // client.on("typingStop", function (channel, user) {
    //     console.log(`typingStop @ ${new Date().toLocaleString()}: ${user.tag} has stopped typing on ${channel} (guild=${channel.guild})`);
    // });

// userNoteUpdate
    /* Emitted whenever a note is updated.
    PARAMETER      TYPE          DESCRIPTION
    user           User          The user the note belongs to
    oldNote        String        The note content before the update
    newNote        String        The note content after the update    */
    client.on("userNoteUpdate", function (user, oldNote, newNote) {
        console.log(`userNoteUpdate @ ${new Date().toLocaleString()}: "${oldNote}" -> "${newNote}" for ${user.tag}`);
    });

// userUpdate
    /* Emitted whenever a user's details (e.g. username) are changed.
    PARAMETER      TYPE        DESCRIPTION
    oldUser        User        The user before the update
    newUser        User        The user after the update    */
    client.on("userUpdate", function (oldUser, newUser) {
        console.log(`userUpdate @ ${new Date().toLocaleString()}: ${oldUser.tag} -> ${newUser.tag}`);
    });

// voiceStateUpdate
    /* Emitted whenever a user changes voice state - e.g. joins/leaves a channel, mutes/unmutes.
    PARAMETER    TYPE             DESCRIPTION
    oldMember    GuildMember      The member before the voice state update
    newMember    GuildMember      The member after the voice state update    */
    client.on("voiceStateUpdate", function (oldMember, newMember) {
        console.log(`voiceStateUpdate @ ${new Date().toLocaleString()} from ${oldMember} -> ${newMember}`);
    });

// warn
    /* Emitted for general warnings.
    PARAMETER    TYPE       DESCRIPTION
    info         string     The warning   */
    client.on("warn", function (info) {
        console.log(`warn @ ${new Date().toLocaleString()}: ${info}`);
    });
}


module.exports = {
    log
}