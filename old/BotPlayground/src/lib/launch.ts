import conf from "../conf";
import {Client} from "discord.js";

export function launch(client: Client, evalFunc: (string) => string) {
	client.on("ready", async () => {
		if (!client.user || !client.application) {
			return;
		}

		console.log(`-- ${client.user.username} is online`);
	});

	client.on('interactionCreate', async interaction => {
		if (!interaction.isChatInputCommand()) return;
		// Rotartsi#3754
		if (interaction.user.id !== conf.ownerId) {
			await interaction.reply({content: "No permission", ephemeral: true});
			return;
		}

		const {commandName} = interaction;
		if (commandName === 'eval') {
			try {
				let val: any = evalFunc(interaction.options.getString("input"));
				let disp: string;

				// unwrap all promises
				while (true) {
					try {
						if (!val.then)
							break;
						val = await val;
					} catch (e) {
						console.log(`-- Err ${e}`)
						disp = val;
						break;
					}
				}

				await interaction.reply({content: `${val}`, ephemeral: interaction.options.getBoolean("private")});
			} catch (e) {
				try {
					await interaction.reply({content: `ERROR\n\`\`\`${e}\`\`\``, ephemeral: true})
				} catch {}
			}
		}

	});


	client.login(conf.token).then(() => console.log("-- Logged in"));
}
