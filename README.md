Better Sprinting
================

## Network specification

If you are a server owner and want to disable the mod, or enable some of the singleplayer-only functions, see the Better Sprinting network specification which has all the information:

https://raw.githubusercontent.com/chylex/Better-Sprinting/master/src/main/java/chylex/bettersprinting/server/ServerNetwork.java

If you have a Forge server, you can just install the mod and use the `/bettersprinting` command to configure the behavior.

## Project license

The source code, asset files, and [official binaries](https://minecraft.curseforge.com/projects/better-sprinting/files) are licensed under [MPL-2.0](https://github.com/chylex/Better-Sprinting/blob/master/LICENSE). Note this only applies to commits and binaries published after 12 June 2019.

## Project setup guide

In order to setup a workspace to play around with the source code, first clone this repository using your Git client (make sure to select the correct branch, they are named by Minecraft versions; master is always the latest work in progress version).

Once you cloned the repository, follow the [Getting Started with Forge](https://mcforge.readthedocs.io/en/latest/gettingstarted) tutorial. You can skip the first 3 steps, the repository already comes with the Gradle build system.

To run Better Sprinting, make sure you add the following entry to your startup VM arguments:

`-Dfml.coreMods.load=chylex.bettersprinting.system.core.BetterSprintingCore`

## Contributing

If you want to do any large changes, please contact me first (open an [Issue](https://github.com/chylex/Better-Sprinting/issues)) with detail about what you want to do. I wouldn't want you to waste your time with a large Pull Request that will not get accepted.

When creating a Pull Request, please follow these guidelines:

- Always target the `master` branch which contains the most recent code
- Follow my code formatting style; most of it should be easy to pick up, here are a couple details:
  - Use tabs for indentation, spaces for alignment
  - No space between right parenthesis and left bracket: `void method(){`
  - No blank lines between imports
- When updating the mod to a new Minecraft version, read `UpdateTodo.txt` for an overview of what needs to be done during each update
