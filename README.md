# SJC
Simple Java Chatroom.  
Provides command line based chat rooms with private messaging, and encyption, written in Java.  

![Preview of SJC](https://i.imgur.com/T5dtlYA.gif "SJC Demo")

## TODO:
- More user commands.

## Examples:
Create a password protected server on port 4203 with debug and terminal colors enabled:

java -jar sjc.jar --server 4203 MyPassword --debug --colors

java -jar sjc.jar --server [server port] [server password] [--debug] [--colors]

Join a password protected server with colors enabled at IP 192.168.1.2 port 4203:

java -jar sjc.jar --client 192.168.1.2 4203 MyPassword --colors

java -jar sjc.jar --client [server IP] [server port] [server password] [--colors]

## JAR Compiled and tested with:
openjdk version "1.8.0_252"

OpenJDK Runtime Environment (build 1.8.0_252-b09)

OpenJDK 64-Bit Server VM (build 25.252-b09, mixed mode)
