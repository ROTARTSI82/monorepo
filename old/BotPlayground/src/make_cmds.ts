import {REST, SlashCommandBuilder} from "discord.js";
const {
	Routes,
} = require('discord-api-types/v10');

import conf from "./conf";

const commands = [
	new SlashCommandBuilder().setName('eval').setDescription('Evaluate javascript')
		.addStringOption(option =>
			option.setName('input')
				.setDescription('The input to evaluate')
				.setRequired(true))
		.addBooleanOption(option =>
			option.setName('private')
				.setDescription("If true, the reply is emphemeral")
				.setRequired(false)),
]
	.map(command => command.toJSON());

const rest = new REST({ version: '10' }).setToken(conf.token);

rest.put(Routes.applicationGuildCommands(conf.clientId, conf.guildId), { body: commands })
	.then((data: any) => console.log(`Successfully registered ${data.length} application commands.`))
	.catch(console.error);
