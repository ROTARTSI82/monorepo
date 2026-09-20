# QuizBot

Half-completed discord bot for playing quizbowl among other things. 

Documentation and help is non-existant, so you'll have to ask me (Rotartsi#3754) or look through my spaghett code.

Invite link for the bot (i probably won't be running it unless i decide to put it on my raspi)

https://discord.com/api/oauth2/authorize?client_id=752356606850629722&permissions=8&scope=bot

## Commands
1. `$q` - Start a new tossup
1. `$b` - Start a new bonus
2. `skip` - Skip tossup (or bonus) if skipping enabled. Only the part of the bonus currently being read will be skipped.
2. `skip all` - Skip all parts of a bonus. 
3. `buzz` - Buzz in if not locked out
4. `pause` - Pause tossup (or bonus) if pausing enabled
5. `resume` - Resume tossup (or bonus)
6. `$score` - View quizbowl score and stats
7. `$reset` - Reset quizbowl stats
8. `$lb` - View the leaderboard for the channel #TODO: NOT IMPLEMENTED
9. `$cat [subcommand[args?]]` - Manage which categories of questions you want 
    - `$cat ls` - list the enabled categories.
    - `$cat la` - List all possible categories
    - `$cat reset` - Reset categories to default (all categories enabled)
    - `$cat rm [category]` - Remove `category` from the rotation of questions
    - `$cat add [category` - Add `category` to the rotation of questions.
10. `$scat [subcommand[args?]]` - Manage which subcategories of questions you want
    - `$scat ls` - list the enabled subcategories.
    - `$scat la` - List all possible subcategories
    - `$scat reset` - Reset subcategories to default (all subcategories enabled)
    - `$scat rm [subcategory]` - Remove `subcategory` from the rotation of questions
    - `$scat add [subcategory` - Add `subcategory` to the rotation of questions.
11. `$tourn [subcommand[args?]]` - Manage which tournaments are whitelisted (e.g. all questions from these tournaments are added to the 
    rotation of questions)
    - `$tourn ls` - List the tournaments whitelisted
    - `$tourn clear` - Clear the tournament whitelist
    - `$tourn rm [tournamnet]` - Remove `tournament` from the whitelist
    - `$tourn add [tournament]` - add `tournament` to the whitelist
12. `$filter [subcommand[args?]]` - Manage filters of difficulty, year, or quality of the question.
    - `$filter ls` - List the current filter settings
    - `$filter clear` - Clear the filter. All questions allowed.
    - `$filter difficulty [min] [max]` - Filter for difficulties between `min` and `max` inclusive.
    - `$filter year [min] [max]` - Filter for years between `min` and `max` inclusive.
    - `$filter quality [min] [max]` - Filter for qualities between `min` and `max` inclusive.
13. `$team [subcommand[args?]]` - Manage quizbowl teams
    - `$team leave` - Leave the current team you're on
    - `$team disband` - Dissolve the team. (Can only be used by team captain.)
    - `$team transfer [ping]` - Transfer ownership (team captain) of the team to `ping`.
    - `$team score` - List the stats and scores for your team, including members
    - `$team new [team]` - Create new `team`
    - `$team join [team]` - Join `team`
16. `$info (tossup|bonus)` - Get metadata about a tossup (`$info tossup`) or bonus (`$info bonus`)
17. `$clear` - Reset the quizbowl room. (anyone can execute)

# TODO LIST
- Implement leaderboards
- Reset all settings
- Commands to change other settings like the following:
```js
this.showSpeed = 5;
this.isPausingEnabled = true;
this.isSkippingEnabled = true;
this.isMultipleBuzzesEnabled = false;
this.showDelay = 1250;
this.bonusDelay = 1250;
this.bonusTimeout = 10000;
this.tuTimeout = 10000; // 10 sec
this.bzTimeout = 10000; // 10 sec
```