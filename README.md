<img src="https://geysermc.org/img/geyser-1760-860.png" alt="Geyser" width="600"/>

[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Discord](https://img.shields.io/discord/613163671870242838.svg?color=%237289da&label=discord)](https://discord.gg/geysermc)
[![Crowdin](https://badges.crowdin.net/e/51361b7f8a01644a238d0fe8f3bddc62/localized.svg)](https://translate.geysermc.org/)

Geyser is a bridge between Minecraft: Bedrock Edition and Minecraft: Java Edition, closing the gap from those wanting to play true cross-platform.

Geyser is an [Open Collaboration](https://opencollaboration.dev/) project.

## Geyser Fast by ArkFlame Studios
This repository is **Geyser Fast by ArkFlame Studios**, a fork of [GeyserMC/Geyser](https://github.com/GeyserMC/Geyser) that keeps Geyser's names, packages, APIs, plugin IDs, artifacts, configuration, licenses and credits unchanged, so it drops in where Geyser is used today. It is built against a companion fork of MCProtocolLib ([ArkFlame/MCProtocolLib](https://github.com/ArkFlame/MCProtocolLib)) (tag `af-26.3-codecfix-2`).

What the fork adds on top of upstream:
- Bug fixes taken from the upstream issue and pull-request backlog, each backed by an offline test that fails on the code before the fix and passes after it.
- Memory and tick-cost work, described in [`docs/OPTIMIZATION_PLAN.md`](docs/OPTIMIZATION_PLAN.md) and [`docs/MEMORY_OPTIMIZATION.md`](docs/MEMORY_OPTIMIZATION.md).
- Offline-mode UUID handling preserved from the fork's earlier work.

Every upstream issue and pull request of Geyser and MCProtocolLib is tracked by repository and number, with its status and the evidence behind it, in [`docs/ARKFLAME_GEYSER_TRACKER.md`](docs/ARKFLAME_GEYSER_TRACKER.md) (machine-readable: [`docs/arkflame-geyser-tracker.json`](docs/arkflame-geyser-tracker.json)). Not every record is fixed: the tracker lists what is still open and which cases cannot be fixed from this side. Fixes are proven with offline tests only; behaviour that depends on a real Bedrock client is marked as such on its record.

## What is Geyser?
Geyser is a proxy, bridging the gap between Minecraft: Bedrock Edition and Minecraft: Java Edition servers.
The ultimate goal of this project is to allow Minecraft: Bedrock Edition users to join Minecraft: Java Edition servers as seamlessly as possible. However, due to the nature of Geyser translating packets over the network of two different games, *do not expect everything to work perfectly!*

Special thanks to the DragonProxy project for being a trailblazer in protocol translation and for all the team members who have joined us here!

## Supported Versions

| Edition | Supported Versions                                                                                             |
|---------|----------------------------------------------------------------------------------------------------------------|
| Bedrock | 26.40, 26.41, 26.42, 26.43, 26.44, 26.45, 26.50, 26.51, 26.52, 26.60, 26.61, 26.62, 26.63, 26.64, 26.65, 26.66 |
| Java    | 26.3 (For older versions, [see this guide](https://geysermc.org/wiki/geyser/supported-versions/))              |

## Setting Up
Take a look [here](https://geysermc.org/wiki/geyser/setup/) for how to set up Geyser.

## Links:
- Website: https://geysermc.org
- Docs: https://geysermc.org/wiki/geyser/
- Download: https://geysermc.org/download
- Discord: https://discord.gg/geysermc
- Donate: https://opencollective.com/geysermc
- Test Server: `test.geysermc.org` port `25565` for Java and `19132` for Bedrock

## What's Left to be Added/Fixed
- Near-perfect movement (to the point where anticheat on large servers is unlikely to ban you)
- Some Entity Flags

## What can't be fixed
There are a few things Geyser is unable to support due to various differences between Minecraft Bedrock and Java. For a list of these limitations, see the [Current Limitations](https://geysermc.org/wiki/geyser/current-limitations/) page.

## Compiling
1. Clone the repo to your computer
2. Navigate to the Geyser root directory and run `git submodule update --init --recursive`. This command downloads all the needed submodules for Geyser and is a crucial step in this process.
3. Run `gradlew build` and locate to `bootstrap/build` folder.

## Contributing
Any contributions are appreciated. Please feel free to reach out to us on [Discord](https://discord.gg/geysermc) if
you're interested in helping out with Geyser.

## Libraries Used:
- [Adventure Text Library](https://github.com/KyoriPowered/adventure)
- [CloudburstMC Bedrock Protocol Library](https://github.com/CloudburstMC/Protocol)
- [GeyserMC's Java Protocol Library](https://github.com/GeyserMC/MCProtocolLib)
- [TerminalConsoleAppender](https://github.com/Minecrell/TerminalConsoleAppender)
- [Simple Logging Facade for Java (slf4j)](https://github.com/qos-ch/slf4j)
