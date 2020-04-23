# ScreenSaverSnow
Screen saver "Snow" in Java. Simple implementation of the movement of snowflakes.

The program is compiled as an "almost" full screen saver for the Windows platform.
![Project illustration](preview.gif)

## Principle of operation
- A home screen is created in Swing.JFrame.
- An array of snowflakes is created.
- The events to exit the program are described.
- The cycle starts (rendering, movement of snowflakes, rendering).

## Features
- The program is configured through the command line. Reads commands at startup as Windows screensaver.
- The application can be built as EXE for Windows through the maven builder (launch4j-maven-plugin). Then rename the EXE file to SCR and place it in the Windows directory. Then the savers are displayed in Windows screensavers. (Important! The "/ p" switch is disabled; it does not work in Java.)

## How to run and compile
For ease of assembly and compilation, Maven is used (in `pom.xml` the settings for this project).
- `mvn compile assembly:single` - Compile an executable JAR file.
- `mvn install` - Rebuild the project completely (an EXE file will be created).
- `java -jar Snow.jar` - Run `Snow.jar` in the Java machine.

## License
- The author of the original idea of Deepak Monster (Snow Particles Animation).
- The original code can be found in the first entries of Git.
- For my part, refactoring and some improvements have been made.
