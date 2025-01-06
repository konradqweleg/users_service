# MyChatServer - User Account Management

User Service is a backend application for managing user accounts, including registration, login, and account activation. It uses Spring WebFlux for reactive programming and integrates with Keycloak for user authentication and authorization.

## Technologies Used

- Java
- Spring WebFlux
- Keycloak
- Gradle
- Reactor

## Getting Started

### Prerequisites

- Java 17 or higher
- Gradle 7.0 or higher
- Keycloak server

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/konradqweleg/mychatserver.git
    cd mychatserver
    ```

2. Configure Keycloak:
    - Set up a Keycloak server.
    - Create a realm named `my-chat-realm`.
    - Create a client named `mychat-client`.
    - Configure the client with the necessary credentials and roles.

3. Update the `application.properties` file with your Keycloak server details:
    ```properties
    keycloak.server.url=http://localhost:8080/auth
    keycloak.admin.username=admin
    keycloak.admin.password=admin
    ```

4. Build the project:
    ```sh
    ./gradlew build
    ```

5. Run the application:
    ```sh
    ./gradlew bootRun
    ```

## API Endpoints

### User Registration

- **POST** `/api/v1/users/register`
    - Request Body: `UserRegisterDataDTO`
    - Response: `201 Created`

### User Login

- **POST** `/api/v1/users/login`
    - Request Body: `LoginData`
    - Response: `200 OK` with `UserAccessData`

### Activate User Account

- **POST** `/api/v1/users/activate`
    - Request Body: `ActiveAccountCodeData`
    - Response: `200 OK`

### Resend Activation Code

- **POST** `/api/v1/users/activate/resend-activation-code`
    - Request Body: `UserEmailData`
    - Response: `200 OK` with activation code

## Exception Handling

Custom exception handlers are defined to handle various exceptions and return appropriate HTTP status codes.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.