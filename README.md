Better Sprinting
================

## Network specifications

If you are a server owner and want to disable the mod, or enable some of the singleplayer-only functions, see the Better Sprinting network specification which has all the information:

https://raw.githubusercontent.com/chylex/Better-Sprinting/master/src/main/java/chylex/bettersprinting/server/ServerNetwork.java

If you have a Forge server, you can just install the mod and use the /bettersprinting command to configure the behavior.

## Project license

The project is under the All Rights Reserved license, which means that redistribution is forbidden. However, I do not want to be as restrictive, so here are some general rules, and some specific cases for what you may want to do with the source.

1. Feel free to fork the project on GitHub. The repository must be publicly visible.
2. You are allowed to study the source code and learn from it, but don't just copy large portions of it into your project.
3. Official terms of use apply, thus you cannot redistribute any part of the mod or claim it as your own. Forking is the only exception to the redistribution rule.

### I want to use a piece of code in my own project

If you find some useful code in this repository, I don't mind if you use it in your own project, as long as you don't just copy entire files or don't take the time to learn how the code you want to use works. Crediting the original source is appreciated.

### I want to contribute to the code or assets

If you want to do any large changes, please contact me first (open an [Issue](https://github.com/chylex/Better-Sprinting/issues)) with detail about what you want to do. I wouldn't want you to waste your time with a large Pull Request that will not get accepted.

When creating a Pull Request, please follow these guidelines:

- Always target the `master` branch which contains the most recent code
- Follow my code formatting style; most of it should be easy to pick up, here are a couple details:
  - Use tabs for indentation
  - Import order is `io, java, net, org, api, chylex, com` for some reason
  - No space between right parenthesis and left bracket: `void method(){`
- When updating the mod to a new Minecraft version, read `UpdateTodo.txt` for an overview of what needs to be done during each update

### I want to create an addon

You are welcome to create and distribute an addon that depends on Better Sprinting, as long as it does not contain any of the original Better Sprinting code or assets.

### I want to create a modification

If you create your own version of Better Sprinting, you have to ask for my explicit permission to distribute it. (Better Sprinting is not open source!)

## Project setup guide

In order to setup your own workspace to play around with the source code, first clone this repository using your Git client (make sure to select the correct branch, they are named by Minecraft versions; master is always the latest work in progress version).

Once you cloned the repository, follow the [Getting Started with Forge](https://mcforge.readthedocs.io/en/latest/gettingstarted) tutorial. You can skip the first 3 steps, the repository already comes with the Gradle build system.

To run Better Sprinting, make sure you add the following entry to your startup VM arguments:

`-Dfml.coreMods.load=chylex.bettersprinting.system.core.BetterSprintingCore`
