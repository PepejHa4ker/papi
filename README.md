# papi
A collection of utilities and extended APIs to support the rapid and easy development of Bukkit plugins. 
### Modules
##### [`papi-core`](https://github.com/PepejHa4ker/papi-core/tree/master/papi): The main papi module
##### [`papi-js`](https://github.com/PepejHa4ker/papi-js/tree/master/papi-js): The javascript papi module
##### [`papi-kotlin`](https://github.com/PepejHa4ker/papi-core/tree/master/papi): The kotlin papi module 
##### [`papi-groovy`](https://github.com/PepejHa4ker/papi-core/tree/master/papi): The groovy papi module (Not implemented yet)

## Events:
```
    Events.subscribe("player:join")
        .handler(e -> {
            Players.msg(e.player, "&6Welcome to my server!")
        })
```
#### or standard spigot events
```
    Events.subscribe(PlayerJoinEvent.class)
        .handler(e -> {
            Players.msg(e.player, "&6Welcome to my server!")
        })
```
#### event merging
```
    Events.merge(PlayerEvent.class, PlayerQuitEvent.class, PlayerKickEvent.class)
            .filter(e -> !e.getPlayer().isOp())
            .handler(e -> {
                Bukkit.broadcastMessage(Text.colorize("&aPlayer &d" + e.getPlayer().getName() + "&a has left the server!"));
            });
```
## JavaScript

```javascript 1.6
Commands.create()
    .assertConsole()
    .assertUsage("<delay>")
    .assertCooldown(10, SECONDS)
    .handler(context => {
        let delay = context.arg(0).parseOrFail(int)
        Promise.start()
       	 	.thenRunSync(() => server.broadcastMessage(colorize(`&aServer will shutdown in &d${delay}&a seconds`)))
       		.thenRunDelayedSync(() => server.shutdown(), delay * 20)
    })
    .registerAndBind(registry, "shutdown", "stop", "off");	
```
##### It's just a javascript file than loaded from init.js


