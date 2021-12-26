# uklient

[![wakatime](https://wakatime.com/badge/github/uku3lig/uklient.svg)](https://wakatime.com/badge/github/uku3lig/uklient)
![GitHub all releases](https://img.shields.io/github/downloads/uku3lig/uklient/total)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/uku3lig/uklient.svg)](https://lgtm.com/projects/g/uku3lig/uklient/context:java)

**uklient** is a flexible, lightweight and easy to use Minecraft client installer.
It installs everything for you, from downloading the most recent version of each mod, to installing fabric by itself, 
while copying all of the needed config files.

## Supported mods
Due to how it is made, uklient can support any mod that is either on Modrinth or Curseforge!
If you want me to add a mod, just open a pull request with the needed info (mod id or link) and it may be added!

### Current presets
 - **uku**: my personal mod list
 - **minimal**: a minimal mod list that focuses on performance
 - **foli**: the mod list from [Foli Client](https://github.com/foliclient/FoliClientInstaller) by ablazingeboy

## Building from source

### Running
Clone the project, then run it with `./gradlew run` on GNU/Linux or `gradlew run` on Windows.

### Compiling
Run `gradlew shadowJar` to build a jar file, you can find it in the `<project root>/build/libs` directory.