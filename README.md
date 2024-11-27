# Fast Typing Game
A J2ME Java application for old feature phones. The goal of the game is to type a given sentence as fast as possible using the 9 keys of the phone (without T9 prediction). In the current version, there are three levels. If the sentence was typed correctly, the player can put their name to be listed in the highscores.

## Installation on the feature phones
If you don't want to make any changes to the amount of levels and sentences of each level, simply transfer the .jar (and .jad) file to your phone (via USB, Bluetooth or Infrared). They are located in dist/. The application was built with the following J2ME settings: CLDC-1.1 and MIDP-2.1. If you encounter problems with the installation, check your phone's compatibilty.
## Usage
Once installed on the feature phone, you will find it either in the "Games" or "Applications" folder, depending on the phone. Highscores are saved on the phone's memory.
## Modifications, development
If you wish to change the sentences for each level, add levels or even contribute, you will need to setup a development environment with a suitable Java Version. This can be quite annoying on modern computers, I strongly recommend to follow these [instructions](https://microgram.app/blog/004-J2ME-development-in-2024-with-linux.html). As described there, it is best to use the NetBeans IDE. The NetBeans project can be found in nbproject/. In case your feature phones supports difference CLDC or MIDP versions, change the settings in NetBeans accordingly.
