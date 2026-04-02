# CS102-cards-project

## Project Structure: JavaFX UI and Console UI

This project includes two different user interfaces: a **JavaFX GUI** and a **console UI**.  
Although both versions implement the same game, they do not use exactly the same supporting classes.

### Shared Classes

Some classes are shared between both versions because they represent the core game data and game objects. These include:

- `AbstractCard`
- `Player`
- `Token`, `TokenBank`
- `DevelopmentCard`, `DevelopmentCardDeck`, `DevelopmentCardFaceUP`
- `Noble`, `NobleDeck`, `NobleFaceUP`
- most classes under the `Cards/` package
- `PurchaseService`

These shared classes store the game state and data structures needed regardless of whether the game is played through the JavaFX GUI or the console UI.

### Different Classes Used by Each UI

Some important classes differ between the two versions.

#### JavaFX version
The JavaFX version uses:
- `GameLogic` to manage turn flow, actions, noble selection, and win-condition logic
- `MoveResult` to report whether an action succeeded and what message should be shown in the GUI
- `NobleService` to handle noble eligibility and awarding
- `ComputerService` to control the computer player's decisions

#### Console version
The console UI does not use those same classes in the same way:
- game flow is handled directly inside `Game.java` instead of through `GameLogic`
- `MoveResult` is not used, since it is mainly designed for JavaFX controller feedback
- `NobleAttractService` is used instead of `NobleService`
- the `Computer` class is used directly instead of `ComputerService`

### Rough Equivalents Between the Two Versions

| Console UI | JavaFX UI |
|------------|-----------|
| `Game.java` | `GameLogic.java` |
| `NobleAttractService` | `NobleService` |
| `Computer` | `ComputerService` |

These are not exact replacements, but they serve similar roles in each version.

### JavaFX-Specific Classes

The JavaFX-specific classes are responsible for the graphical interface. These include:

- `GameApp`
- `MenuController`
- `Controller`
- `BoardView`
- `CardView`

These classes handle buttons, mouse clicks, popups, animations, and the visual display of the board. They depend on JavaFX-specific functionality and are not used by the console version.

### Console-Specific Classes

The console-specific classes are responsible for text-based input and output. They display prompts, read terminal input, and guide the player through the game using printed text instead of graphical components.

### Summary

The two interfaces share the same core game data classes, but differ in their controller and interaction logic.  
This design allows both versions to represent the same game while using different implementations suited to their respective interfaces.

## SRC Package Overview

src/
├── Cards/                   # Shared card and token data classes
│   ├── AbstractCard/        # Base card classes
│   ├── DevelopmentCard/     # Development card classes and deck logic
│   ├── Noble/               # Noble card classes and deck logic
│   └── Token/               # Token and token bank classes
├── Player/                  # Player-related classes and services (noble services, computer logic)
├── Properties/              # Config/property file reader
├── Test/                    # Console UI and game/testing logic
└── UI/                      # JavaFX UI: contains JavaFX version of the game
    ├── components/          # Reusable UI components such as BoardView and CardView
    ├── controllers/         # JavaFX controllers
    └── views/               # FXML view files

## Asset Credits

This project uses third-party visual assets downloaded from free asset pages.  
These assets were used for educational/course-project purposes and may have been cropped, resized, or adapted for the game UI.

### Backgrounds

**Menu Background**
- Craftpix — Free Nature Backgrounds Pixel Art  
  https://craftpix.net/freebies/free-nature-backgrounds-pixel-art/

**Game Background**
- Craftpix — Free Sky With Clouds Pixel Art Backgrounds Pack  
  https://craftpix.net/freebies/free-sky-with-clouds-pixel-art-backgrounds-pack-3/

### Development Card Backgrounds

- Craftpix — Free Pixel Art Fantasy 2D Battlegrounds  
  https://craftpix.net/freebies/free-pixel-art-fantasy-2d-battlegrounds/

- Craftpix — Free Mountain Backgrounds Pixel Art  
  https://craftpix.net/freebies/free-mountain-backgrounds-pixel-art/

- Craftpix — Free Mountain Peak Pixel Art Backgrounds  
  https://craftpix.net/freebies/free-mountain-peak-pixel-art-backgrounds/

- Craftpix — Forest and Trees Free Pixel Backgrounds  
  https://craftpix.net/freebies/forest-and-trees-free-pixel-backgrounds/

- Craftpix — Free Post-Apocalyptic Pixel Art Game Backgrounds  
  https://craftpix.net/freebies/free-post-apocalyptic-pixel-art-game-backgrounds/

- Craftpix — Free War Pixel Art 2D Backgrounds  
  https://craftpix.net/freebies/free-war-pixel-art-2d-backgrounds/

- Craftpix — Free Winter Backgrounds Pixel Art  
  https://craftpix.net/freebies/free-winter-backgrounds-pixel-art/

- Craftpix — Free Nature Pixel Backgrounds for Games  
  https://craftpix.net/freebies/free-nature-pixel-backgrounds-for-games/

- Craftpix — Free Fairy Tale Game Backgrounds  
  https://craftpix.net/freebies/free-fairy-tale-game-backgrounds/

- Craftpix — Free Winter Nature Pixel Game Backgrounds  
  https://craftpix.net/freebies/free-winter-nature-pixel-game-backgrounds/

- Craftpix — Free Pixel Art Abandoned Places Background Collection  
  https://craftpix.net/freebies/free-pixel-art-abandoned-places-background-collection/

- Craftpix — Free Moon Pixel Game Backgrounds  
  https://craftpix.net/freebies/free-moon-pixel-game-backgrounds/

- Craftpix — Free Crystal Cave Pixel Art Backgrounds  
  https://craftpix.net/freebies/free-crystal-cave-pixel-art-backgrounds/

- Craftpix — Free Steampunk Cityscape Pixel Backgrounds  
  https://craftpix.net/freebies/free-steampunk-cityscape-pixel-backgrounds/

- Craftpix — Free Pixel Sky With Parallax Clouds for 2D Games  
  https://craftpix.net/freebies/free-pixel-sky-with-parallax-clouds-for-2d-games/

### UI Assets

**Buttons and Title Banners**
- Retro Pixel Ribbons, Banners and Frames 2  
  https://bdragon1727.itch.io/retro-pixel-ribbons-banners-and-frames-2

### Noble Card Character Art

**Noble Card People**
- Craftpix — Free Halfling Avatar Icons  
  https://craftpix.net/freebies/free-halfing-avatar-icons/

### Note

The asset links above refer to the download/source pages used during development.  
Asset ownership remains with the original creators/publishers.