# SJC
Simple Java Chatroom
Basic functionality has only be created.

## TODO:
- More user commands.
- General testing.
- Encryption / passwords.

## Examples:
Create a server on port 4203 with debug and terminal colors enabled:
java -jar sjc.jar --server 4203 --debug --colors

Join a server with colors enabled at IP 192.168.1.2 port 4203:
java -jar sjc.jar --client 192.168.1.2 4203 --colors

## JAR Compiled and tested with:
openjdk version "1.8.0_252"

OpenJDK Runtime Environment (build 1.8.0_252-b09)

OpenJDK 64-Bit Server VM (build 25.252-b09, mixed mode)
