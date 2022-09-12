import {ChannelType, Client, GatewayIntentBits, Message, Partials} from "discord.js";
import {launch} from "./lib/launch";
import conf from "./conf";

const fs = require('fs');

function fac(n: number): number {
	return n > 0 ? fac(n-1)*n : 1;
}

let client = new Client({
	intents: [
		GatewayIntentBits.Guilds,
		GatewayIntentBits.GuildMembers,
		GatewayIntentBits.GuildBans,
		GatewayIntentBits.GuildEmojisAndStickers,
		GatewayIntentBits.GuildIntegrations,
		GatewayIntentBits.GuildWebhooks,
		GatewayIntentBits.GuildInvites,
		GatewayIntentBits.GuildVoiceStates,
		GatewayIntentBits.GuildPresences,
		GatewayIntentBits.GuildMessages,
		GatewayIntentBits.GuildMessageReactions,
		GatewayIntentBits.GuildMessageTyping,
		GatewayIntentBits.DirectMessages,
		GatewayIntentBits.DirectMessageReactions,
		GatewayIntentBits.DirectMessageTyping,
		GatewayIntentBits.MessageContent,
		GatewayIntentBits.GuildScheduledEvents,
	],
	partials: [
		Partials.Channel,
		Partials.Message,
		Partials.GuildMember,
		Partials.User,
		Partials.GuildScheduledEvent,
		Partials.Reaction,
		Partials.ThreadMember,
	]
});

interface MessageStats {
	sent: number,
	chars: number
}

async function for_all_msgs(guildId: string, func: (x: Message<true>) => void) {
	let guild = await client.guilds.fetch(guildId);

	for (const [chanId, chan] of guild.channels.cache) {
		// let chan = await client.channels.fetch(chanId);
		if (chan.type !== ChannelType.GuildText)
			continue;

		let tot = 0;
		let messages = await chan.messages.fetch({limit: 100});
		while (messages.size != 0) {

			for (const [msgId, msg] of messages)
				func(msg)

			tot += messages.size;
			console.log(`--\t${chan.name}: +${messages.size} (total ${tot})`)

			messages = await chan.messages.fetch({limit: 100, before: messages.last().id});
		}
		console.log(`-- Fetched ${chan.name}`)
	}
}

async function messages(id: string) {
	let users = new Map<string, MessageStats>();
	await for_all_msgs(id, msg => {
		if (users.has(msg.author.id)) {
			users.get(msg.author.id).sent++;
			users.get(msg.author.id).chars += msg.content.length;
		} else {
			users.set(msg.author.id, {
				sent: 1,
				chars: msg.content.length
			})
		}
	});

	return users;
}

// mode 0: # of character
// mode 1: # of messages
// mode 2: average characters per message
async function msg_stats(guild: string, chan: string, mode: number) {
	let users = await messages(guild);
	let ret = "";
	let c = await client.channels.fetch(chan);

	const trySend = async () => {
		if (c.type != ChannelType.GuildText)
			return;

		try {
			await c.send(ret);
		} catch (e) {
			console.log(e);
		}
	};

	let proc: [string, MessageStats][] = [];

	for (const [user, stat] of users) {
		proc.push([user, stat]);
	}

	const sel = (a: [string, MessageStats]) => {
		switch (mode) {
			case 0: return a[1].chars;
			case 1: return a[1].sent;
			default: return a[1].chars / a[1].sent;
		}
	};

	proc.sort((a, b) => {
		let [sa, sb] = [sel(a), sel(b)];
		if (sa > sb) return -1;
		if (sa < sb) return 1;
		return 0;
	});

	for (const [user, stat] of proc) {
		let avg = (stat.chars / stat.sent);
		let add = `<@${user}> - ${stat.chars} / ${stat.sent} (${(stat.chars / 5).toFixed(2)} words, ${avg.toFixed(2)}char/${(avg/5).toFixed(2)}word avg)\n`;
		if (ret.length + add.length >= 2000 - 1) {
			await trySend();
			ret = add;
		} else {
			ret += add;
		}
	}

	await trySend();

	return ret.substring(0, 2000 - 1);
}

async function default_stats(mode: number = 0) {
	return await msg_stats(conf.statGuild, conf.statChan, mode);
}

function fmt(s: string): string {
	return `\`\`\`${s}\`\`\``;
}

// arrow function is needed to eval() in the scope of this file
// rather than in the scope of launch.ts
launch(client, (inp: string) => eval(inp))

function fmt_message<T extends boolean = boolean>(msg: Message<T>): string {
	return `[${msg.createdAt.getTime()};${msg.createdAt.toLocaleTimeString()}],[${msg.channel.id};${msg.channel.type === ChannelType.GuildText ? msg.channel.name : "#unknown#"}],[${msg.author.id};${msg.author.username}],${msg.content}`
}

async function download_msgs(g: string) {
	let messages: Message<true>[] = [];

	await for_all_msgs(g, msg => {
		messages.push(msg);
	});

	messages.sort((a, b) => {
		if (a.createdAt.getTime() > b.createdAt.getTime()) return 1;
		else if (a.createdAt.getTime() < b.createdAt.getTime()) return -1;
		return 0;
	});

	let outp = messages.map(fmt_message<true>).join("\n");
	console.log(outp);
	fs.writeFile("./down.txt", outp, () => {console.log("written")});
	return outp;
}

client.on("messageCreate", (msg) => {
	// if (message.author.bot) return false;
	console.log(fmt_message(msg))
});

