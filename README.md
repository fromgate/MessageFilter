# MessageFilter
_A Minecraft (bukkit) plugin_

This plugin brings to you ability to control message in your chat provided from any source. You can hide annoying message of plugin (forever or using cooldown to display it), change any message to another. You can even use MessageFilter as translation tool for plugins that not provides translation mechanics by itself.

## Video
[![Video tutorial](https://img.youtube.com/vi/1JG9_OxcqZI/maxresdefault.jpg)](https://www.youtube.com/watch?v=1JG9_OxcqZI)

## Why I need this plugin?
* You would like to hide some spamming message print by plugin you use at your server;
* You would like to replace one message to another;
* You going to translate message of plugin but that plugin is not providing you language-file to edit;

## Features
* Hiding messages
* Replacing messages
* Cooldown for message (for example, you don't like to lot of same message, and going to see only one of them during one minute)
* Regex (and other types of string comparison) supported

## How it works
All rules added to MessageFilter store in files in `MessageFilter/rules` folder. You can add, modify or remove rules using command or by editing rule files.

Every rule based on some parameters:
* **type** — defines the method which will used to determine text in message (before display it on screen). There are five types:
  * `EQUAL` — case insensitive compare (Example: "Aaaa Bbbb" will be equal to "aaaa bbbb")
  * `CONTAINS` — find substring in input-message
  * `START` — check if message starts with provided text
  * `END` — check if message ends with provided text
  * `REGEX` — using regular expression to find matches
* **message-mask** — defines input mask, that will used to find matches in original message
* **replace-to** — defines replacement. Replacements supports placeholders `%word1% ... %wordN%` the will be replaced with conforming word from original message
* **cooldown-time** — time defined in format similar to time format used in ReActions plugin. Cooldown used to set up time-limit for displaying annoying message. For example, if cooldown time is set to 5 seconds. And "replace-to" mask is empty. Annoying message will displayed only once per five seconds.

When plugin (or other source) is sending message, this message is controlled by MessageFilter and changed according to defined rules. Player will see replaced message instead of original. This methods allows even to translate a plugins that not provides built-in translation for your language. You just need to find messages and create rules to replace it. Here is rule file example: http://dev.bukkit.org/bukkit-plugins/message-filter/pages/main/rule-file-example/

## Commands
Main command of plugin is `msgfilter` (aliases: `mfilter`, `filter`).

`/msgfilter help` — hmm... h  
`/msgfilter add <RuleId> [GroupId]` — create new rule  
`/msgfilter set <RuleId> type|input|output|cooldown|group <Value>` — set parameter (type, input mask, output mask, cooldown time or group) to <Value>  
`/msgfilter remove <RuleId>` — remove rule  
`/msgfilter list [Mask] [PageNumber]` — show list of rules  
`/msgfilter info <RuleId>` — display rule parameters  
`/msgfilter save <GroupId> [<Number>|<Time>]` — save message into file `<GroupId>.yml`.  
`/msgfilter reload` — reload rules  
`/msgfilter test` — test MessageFilter.  

## Creating rules
You can create new rules using: command line, incoming chat message and manully editing file.

## Examples:
### Add rule using commands
`/msgfilter add newrule`  
`/msgfilter set newrule type EQUAL`  
`/msgfilter set newrule input this is the input message`  
`/msgfilter set newrule output &6this is the output message`  

### Save incoming chat message
`/msgfilter save test1` — save next (only one) incoming message to file `test1.yml`  
`/msgfilter save test2 15` — save next 15 incoming messages to file `test1.yml`  
`/msgfilter save test2 10m` — save all incoming messages during the 10 minutes to file `test1.yml`  

### Manual configuration
MessageFilter supports multiple rules definition files. It's a "YAML" fie (*.yml) located in "rules" folder. You can edit (or create new) this files manually. Here is example:
```
roadprotector: # rule Id
  type: EQUAL  # rule type
  message-mask: '&3[RP] &cThis place is protected!' # incoming message (with color code)
  replace-to: '&6Здесь нельзя ломать и строить!'  #outgoing (translated) message
  cooldown-time: '' # cooldown time
  use-formating: true # use formatting (colors)
removejoing: # rule Id
  type: REGEX #rule type
  message-mask: '\w+ joined the server\.'   # incoming message, I'm going to hide it
  replace-to: '' # Empty line
  cooldown-time: ''
  use-formating: false # ignore formatting
annoying:
  type: EQUAL  
  message-mask: 'You can\'t build here!'
  replace-to: '' # Empty line
  cooldown-time: '5s' #You will see this message only once during the five seconds
  use-formating: false # ignore formatting
MessageFilter self-test
You can use command /msgfilter test to be sure that MessageFilter works fine. Just type this command in chat. If you receive message "MessageFilter test failed!" usually you need to install or update ProtocolLib.
```

## Permissions
`messagefilter.config` — only one permission to access commands.  
`messagefilter.test` — perform test of MessageFilter when player joins the server.

## Dependencies
This plugin requires [ProtocoLib](https://www.spigotmc.org/resources/protocollib.1997/) installed on your server.

## Update checker
MessageFilter includes a update checker that use your server internet connection. Update checker will every hour check the dev.bukkit.org to find new released version of plugin and you can easy disable it: just set parameter "version-check" to "false" in `config.yml`.
