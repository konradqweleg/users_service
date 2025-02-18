# MyChatServer - Message Service

Message Service is a backend application responsible for managing user messages, including sending, retrieving, and filtering messages between users. It is built using Spring WebFlux for reactive programming and integrates with the User Service for user validation.

## Technologies Used

- Java
- Spring WebFlux
- Reactor
- Gradle
- PostgreSQL

## Getting Started

### Prerequisites

- Java 17 or higher
- Gradle 7.0 or higher
- PostgreSQL database
- Running instance of **User Service**

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/konradqweleg/mychatserver.git
    cd mychatserver
    ```

2. Configure the application:  
   Update the `application.properties` file with the required database and service connection details:
    ```properties
    user.service.url=http://user-service:8082/api/v1/users
    spring.datasource.url=jdbc:postgresql://localhost:5432/message_db
    spring.datasource.username=postgres
    spring.datasource.password=root
    ```

3. Build the project:
    ```sh
    ./gradlew build
    ```

4. Run the application:
    ```sh
    ./gradlew bootRun
    ```

---

## API Endpoints

### Create a Message
- **POST** `/api/v1/messages`
- Creates a new message between users.
- Requires a request body containing sender ID, receiver ID, and message content.
- Returns `201 Created` on success.

---

### Get Last Messages with Friends
- **GET** `/api/v1/messages/{userId}/friends/last-messages`
- Retrieves the latest messages between the specified user and their friends.
- Requires the user ID as a path variable.
- Returns `200 OK` with a list of recent messages or `404 Not Found` if no messages exist.

---

### Get Messages Between Two Users
- **GET** `/api/v1/messages/{firstUserId}/friends/{friendId}/messages`
- Fetches the message history between two users.
- Requires both user IDs as path variables.
- Returns `200 OK` with a list of messages or `404 Not Found` if no messages exist.

---

### Get All Messages with Friends
- **POST** `/api/v1/messages/{userId}/friends/messages`
- Retrieves all conversations between the specified user and their friends.
- Requires the user ID as a path variable and a request body specifying additional filters.
- Returns `200 OK` with a list of message conversations.

---

## Exception Handling

Custom exception handlers are implemented to handle various scenarios, including:
- Invalid request data (`400 Bad Request`)
- User not found (`404 Not Found`)
- Database errors (`500 Internal Server Error`)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
