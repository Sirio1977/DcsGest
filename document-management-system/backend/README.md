# Document Management System Backend

This document provides an overview of the backend for the Document Management System project, which is built using Java Spring Boot and PostgreSQL.

## Project Structure

The backend project is organized as follows:

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── gestionale/
│   │   │           ├── config/              # Configuration classes for Spring
│   │   │           ├── controller/          # REST controllers for handling requests
│   │   │           ├── dto/                 # Data Transfer Objects (DTOs)
│   │   │           ├── entity/               # JPA entity classes
│   │   │           ├── exception/            # Custom exception classes
│   │   │           ├── mapper/               # Classes for mapping entities and DTOs
│   │   │           ├── repository/           # Repository interfaces for data access
│   │   │           ├── report/               # Classes related to report generation
│   │   │           ├── security/             # Security configuration classes
│   │   │           ├── service/              # Service classes containing business logic
│   │   │           ├── util/                 # Utility classes with helper methods
│   │   │           └── validation/           # Custom validators for input data
│   │   └── resources/
│   │       ├── application.properties         # Configuration properties for the application
│   │       └── application.yml                # Alternative YAML configuration file
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── gestionale/
│       │           └── test/                 # Test classes for unit and integration testing
│       └── resources/
│           └── application-test.properties    # Test-specific configuration properties
├── pom.xml                                    # Maven configuration file
└── README.md                                  # Documentation for the backend project
```

## Technologies Used

- **Java**: The primary programming language for the backend.
- **Spring Boot**: Framework for building the backend application.
- **PostgreSQL**: Relational database management system used for data storage.
- **Maven**: Build automation tool used for managing project dependencies.

## Getting Started

To get started with the backend project, follow these steps:

1. **Clone the Repository**: Clone the project repository to your local machine.
2. **Install Dependencies**: Navigate to the `backend` directory and run `mvn install` to install the necessary dependencies.
3. **Configure Database**: Update the `application.properties` or `application.yml` file with your PostgreSQL database connection details.
4. **Run the Application**: Use the command `mvn spring-boot:run` to start the backend application.

## API Documentation

The backend exposes a RESTful API for interacting with the document management system. API endpoints are documented using OpenAPI/Swagger.

## Testing

Unit and integration tests are located in the `src/test` directory. Use Maven to run the tests with the command `mvn test`.

## Contribution

Contributions to the project are welcome. Please submit a pull request or open an issue for any enhancements or bug fixes.

## License

This project is licensed under the MIT License. See the LICENSE file for more details.