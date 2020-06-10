# Game with WebSockets

## What is it?
It runs a spring server running websocket. When two players connect and send their names, the game can start.
The user types a start number and click on start game.
After that is the turn of the opponent. The games will continue until the number reach 1.
On each turn the player can choose if he will type the additional value or if it will be provided automatically.

## How to run:

### Docker
If you have docker installed just need to run the script: `start_docher_script.sh`


### linux/mac you can run (local - maven):
run the script: `start_local_mvn_script.sh` or type

### On windows(local - maven):
    > mvn install
    > java -jar "target/gameofthree1.0-SNAPSHOT.jar"


### Problems not covered on this MVP:
    1 - If a user lose the connection during the game, the list of game will become very messy.