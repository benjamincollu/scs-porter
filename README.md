# scs-porter

A command-line tool to port .scs mod files to another version of Euro Truck Simulator 2 or American Truck Simulator.

## Usage

```bash
java -jar scs-porter.jar <file> <version to port to>
```
example:

```bash
java -jar scs-porter.jar mod.scs 1.54
```

## Release

Can be downloaded from the [releases page](https://github.com/benjamincollu/scs-porter/releases), run using java -jar command as shown in the usage section.

## Issues

scs-porter depends on the zip-like structure of the .scs file format, however some mods may implement HashFSv2 which renders the method this tool uses ineffective.

## Disclaimer

This tool is not affiliated with SCS Software or any other company. It is a community project to help modders port their mods to newer versions of the game. Use at your own risk, and always back up your files before using this tool.