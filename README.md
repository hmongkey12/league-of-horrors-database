# League of Horrors Database Server
This is the database server for the League of Horrors game, a 2D MOBA. The game server communicates with this database server via HTTP REST to get access to the game's data. The database server is built using JDBC and is a Gradle project.

## Requirements
- Java 8 or higher
- Oracle database
## Getting started
- Clone this repository to your local machine.
- Build the project using Gradle: gradle build
- Create a new Oracle database for the League of Horrors game.
- Edit the src/main/resources/application.properties file to configure the database connection details.
- Start the server: java -jar build/libs/league-of-horrors-db-server.jar
- The server will start and listen for incoming connections on port <TBD>

## API documentation
The server exposes a REST API for interacting with the League of Horrors database. You can find the API documentation in the docs folder.

## Contributing
Contributions to this recreation project are welcome! If you would like to contribute, please fork this repository and submit a pull request with your changes.

## License
This project is licensed under the MIT License - see the LICENSE file for details.
