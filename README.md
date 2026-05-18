🌊 TideBound - Naval Battle Game

A turn-based naval strategy game built with Java Swing featuring unique character abilities, strategic ship placement, and intense tactical combat.

## 📖 About

TideBound is a modern take on the classic Battleship game, enhanced with character-driven gameplay mechanics. Players command one of eight unique naval commanders, each with their own special abilities and playstyle. Engage in thrilling battles against AI opponents in Campaign Mode or challenge a friend in Local Multiplayer.

## ✨ Features

### Core Gameplay
- **Strategic Ship Placement** - Position your fleet across a 10x10 grid
- **Turn-Based Combat** - Attack enemy waters and sink their fleet
- **Special Abilities** - Use powerful character skills to turn the tide of battle
- **Multiple Game Modes** - Campaign and Local Multiplayer

### Characters
Fight as one of 8 unique naval commanders:
- **Jiji** - The Lazy Cat: Healing and evasion abilities
- **Kael** - Shadow Master: Teleportation and stealth mechanics
- **Valerius** - The Iron Shoreline: Defensive tank with damage reduction
- **Skye** - Cat Admiral: Multi-target attacks and agility
- **Morgana** - Ocean Enchantress: Water-based crowd control
- **Selene** - Moon Priestess: Time-based powers with day/night cycle
- **Aeris** - Wind Commander: Shield and protection abilities
- **Flue** - Digital Tactician: Virus spread and debuff mechanics

### Game Modes
- **Campaign Mode** - Face waves of AI opponents with increasing difficulty
- **Local Multiplayer** - Hot-seat 1v1 battles on a single device

### UI & Polish
- Professional naval-themed interface
- Smooth animations and particle effects
- Dynamic ocean backgrounds
- Sound effects and background music
- Responsive design for different screen sizes

## 🎮 How to Play

1. **Select Your Commander** - Choose a character that matches your playstyle
2. **Place Your Fleet** - Strategically position 5 ships:
   - Carrier (5 cells)
   - Battleship (4 cells)
   - Cruiser (3 cells)
   - Submarine (3 cells)
   - Destroyer (2 cells)
3. **Battle** - Take turns attacking enemy waters
4. **Use Skills** - Activate special abilities to gain an advantage
5. **Victory** - Sink all enemy ships to win!

## 🛠️ Technical Details

### Built With
- **Language:** Java 11+
- **GUI Framework:** Swing
- **Architecture:** MVC Pattern
- **Build Tool:** javac (standard Java compiler)

### Project Structure
```
TideBound/
├── src/
│   ├── main/           # Entry point and game coordinator
│   ├── characters/     # Character classes and abilities
│   ├── models/         # Game data structures (Board, Ship, Cell)
│   ├── gui/            # UI components and panels
│   ├── campaign/       # Single-player campaign mode
│   ├── game/           # Game logic and multiplayer
│   ├── ai/             # AI opponent
│   └── audio/          # Sound and music manager
├── assets/             # Images, sounds, and resources
└── bin/                # Compiled classes
```

### OOP Concepts Demonstrated
- **Abstraction** - GameCharacter abstract base class
- **Inheritance** - All characters extend GameCharacter
- **Polymorphism** - Uniform treatment of different character types
- **Encapsulation** - Private fields with public accessor methods
- **Interfaces** - Event listeners for UI callbacks

## 🚀 Getting Started

### Prerequisites
- Java Development Kit (JDK) 11 or higher
- Git (for cloning the repository)

### Installation

1. Clone the repository
```bash
git clone https://github.com/JiiiJiii26/Dagat2Game.git
cd Dagat2Game
```

2. Compile the project
```bash
javac -d bin -sourcepath src src/main/Main.java
```

3. Run the game
```bash
java -cp bin main.Main
```

## 🎯 Controls

- **Mouse** - Navigate menus and select targets
- **Left Click** - Fire at enemy cells / Place ships / Use skills
- **Right Click** - Rotate ships during placement
- **Buttons** - Use skill buttons to activate special abilities

## 🏗️ Architecture

### Design Patterns
- **MVC (Model-View-Controller)** - Separation of game logic, UI, and coordination
- **Observer Pattern** - Listener interfaces for event handling
- **Strategy Pattern** - Different AI behaviors and difficulty levels
- **Singleton Pattern** - MusicManager for centralized audio control

### Key Classes
- `Main.java` - Application entry point and panel coordinator
- `GameCharacter.java` - Abstract base class for all playable characters
- `Board.java` - Game board logic and state management
- `CampaignMode.java` - Single-player campaign controller
- `LocalMultiplayer.java` - Multiplayer game logic
- `BoardPanel.java` - Visual rendering of the game board

## 👥 Team

**Team Omen**
- Christian Jay S. Peña
- Nicco Victor P. Maldo
- Justin P. Maquiling
- Khylla Laine C. Menardo

## 📝 Development Notes

### Recent Updates
- ✅ Redesigned multiplayer board UI for better visibility
- ✅ Implemented transition screens between player turns
- ✅ Enhanced campaign mode with professional board styling
- ✅ Added larger board cells for improved gameplay
- ✅ Fixed ship rendering and collision detection

### Known Features
- All 8 characters fully implemented with unique abilities
- Campaign mode with wave-based progression
- Local multiplayer with turn-based system
- Professional UI matching placement screen design
- Sound effects and background music integration

## 🎓 Academic Context

This project was developed as part of an Object-Oriented Programming course, demonstrating:
- Advanced OOP principles (Abstraction, Inheritance, Polymorphism, Encapsulation)
- GUI development with Java Swing
- Event-driven programming with listener patterns
- Software architecture and design patterns
- Version control with Git

## 📄 License

This project is an educational assignment developed for academic purposes.

## 🙏 Acknowledgments

- Inspired by the classic Battleship board game
- Character designs and abilities created by Team Omen
- Sound effects and music from royalty-free sources
- Java Swing documentation and tutorials
- Thank you sir Kenn Migan Vincent C. Gumonan

## 📧 Contact

For questions or feedback about this project:
- Repository: [https://github.com/JiiiJiii26/Dagat2Game](https://github.com/JiiiJiii26/Dagat2Game)

---

**Made with ❤️ by Team Omen**

