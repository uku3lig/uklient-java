# uklient

[![wakatime](https://wakatime.com/badge/github/uku3lig/uklient.svg)](https://wakatime.com/badge/github/uku3lig/uklient)
[![GitHub all releases](https://img.shields.io/github/downloads/uku3lig/uklient/total)](https://github.com/uku3lig/uklient/releases)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/uku3lig/uklient.svg)](https://lgtm.com/projects/g/uku3lig/uklient/context:java)
[![Resources](https://img.shields.io/badge/resources-uklient--resources-informational)](https://github.com/uku3lig/uklient-resources)

## End of life

I have decided to no longer develop the Java version of uklient, due to too many issues and a big lack of motivation.
I will maybe restart the project in another, better language, hoping that I'll be able to get it done this time.

Thank you for your support.

<hr />

**uklient** is a flexible, lightweight and easy to use Minecraft client installer.
It installs everything for you, from downloading the most recent version of each mod, to installing fabric by itself, 
while copying all of the needed config files.

## Features
 - supports multiple minecraft versions
 - installs the latest version of each mod
 - updates the mods
 - multiple mod presets
 - customizable and shareable user mod presets
 - super cool config files
 - installs the latest version of fabric
 - keeps your keybinds and resource packs
 - i probably forgot features here but you get my point it's super cool

What uklient is **NOT**:
 - Foli Client but femboy themed
 - hacked client
 - fr\*nch virus that will install l\*nux on your computer
 - vbuck generator
 - virus that mines crypto on your behalf

## Supported mods
Due to how it is made, uklient can support any mod that is either on Modrinth or Curseforge!
If you want me to add a mod, just open a pull request with the needed info (mod id or link) and it may be added!

### Current presets
 - **uku**: my personal mod list
 - **minimal**: a minimal mod list that focuses on performance
 - **foli**: inspired from ablazingeboy's [Foli Client](https://github.com/foliclient/FoliClientInstaller)

## Building from source

### Running
Clone the project, then run it with `./uklient.sh` on GNU/Linux or `uklient.bat` on Windows.

### Compiling
Run `gradlew shadowJar` to build a jar file, you can find it in the `<project root>/build/libs` directory.
