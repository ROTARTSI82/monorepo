const {showDelay, qTimeout, buzzTimeout, coinMultiplier, anchor} = require("./settings")

function runAdmin(client, msg) {
    if (msg.author.id === '450026704556130304') {
        if (msg.content.startsWith('$npurge')) {
            msg.channel.bulkDelete(parseInt(msg.content.substr(8)));
        }

        let args = /\$purge +<@!([0-9]+)> +([0-9]+)/g.exec(msg.content);
        if (args !== null) {
            msg.channel.send(args);
        }
    }
}

module.exports = {
    runAdmin
}