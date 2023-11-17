# PlaytechInternshipProject
Code written for the 2023 Playtech Winternship application.

### Match
The Match object is used to store all the nessecary data about a match:
- id
- winner
- winner rate

### MatchResult
The MatchResult enum is a simple enum that has 3 options:
- Draw
- A Won
- B Won

### Player
The player object stores data about a player, such as:
- Matches participated in
- Bets & how many of them were won
- If the player is illegal

### OperationReadingException
A simple custom RunTimeException that is thrown when the player_data.txt file has something other than "BET", "DEPOSIT" and "WITHDRAW".

### BettingSystem
The class where the actual logic happens, such as betting and withdrawing. Also a lot of in-memory data stored here. 

### Main
A class to read files as well as trigger betting system

## Remarks
In real life the system would be a bit more complicated for better expandability, for example, we could have the Operations be classes that implement an interface. This would allow us to easily add more operations in the future. However, for the purposes of this exercise, a simple system seemed better.