# Chat Calc

A simple, powerful and lightweight calculator mod for use in modern Minecraft. Do quick maths in-game without wasting time and energy, using custom variables and functions for even more speed.

## Features
- **Easy to Use**
  To perform a calculation, simply prefix a chat message with '='.
  Pressing '=' will automatically open the chat ready to calculate.
- **Custom Variables and Functions**
  Save any commonly used numbers and write your own reusable functions.
  Perfect for planning projects, like builds or even redstone.
  You can even use the last calculation result with the 'ans' variable for extra speed.
- **Instant Portability**
  Your saved variables and functions are stored in your config folder, allowing you to use them anywhere.
  This allows your calculator setup to follow you across all singleplayer world and multiplayer servers.
- **Client-Side Only**
  Safe to use on any server, modded or otherwise with no server interaction ever.

## Why use this mod?
Whether you're:
- Designing complex redstone contraptions
- Calculating resource costs for a building project
- Or just need a nifty little calculator for occasional use

... this mod can help you save time and stay accurate.

## Extending
Nothing crazy here, the mod uses CalcManager and CalcProcessor to manage state and allow for expression evaluation, so those files may be worth a look.
No external dependencies, relatively light usage of forge-centric features should allow for easy migration, with the exception of the event handling and commands.
Good luck, hope you get that fork done!
