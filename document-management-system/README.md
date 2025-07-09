# Document Management System

## Overview
This project is a Document Management System designed for Italian businesses, built using Java Spring Boot for the backend, React with TypeScript for the frontend, and PostgreSQL as the database. The system aims to comply with Italian fiscal regulations and support essential commercial documents.

## Project Structure
The project is organized into three main directories: `backend`, `frontend`, and `database`.

### Backend
- **Location**: `backend/`
- **Technologies**: Java Spring Boot
- **Description**: Contains the server-side application, including REST APIs, business logic, and database interactions.
- **Key Directories**:
  - `src/main/java/com/gestionale/config`: Configuration classes for Spring.
  - `src/main/java/com/gestionale/controller`: REST controllers for handling requests.
  - `src/main/java/com/gestionale/dto`: Data Transfer Objects for data exchange.
  - `src/main/java/com/gestionale/entity`: JPA entities representing database tables.
  - `src/main/java/com/gestionale/repository`: Repository interfaces for data access.
  - `src/main/resources`: Configuration files for the application.

### Frontend
- **Location**: `frontend/`
- **Technologies**: React, TypeScript
- **Description**: Contains the client-side application, providing a user interface for interacting with the document management system.
- **Key Directories**:
  - `src/components`: Reusable components for forms, tables, and charts.
  - `src/pages`: Page components representing different views.
  - `src/store`: Redux store setup for state management.
  - `src/utils`: Utility functions for common tasks.

### Database
- **Location**: `database/`
- **Description**: Contains SQL migration scripts for setting up the database schema.
- **Key Files**:
  - `migrations/V1__initial_schema.sql`: SQL script for the initial database setup.

## Getting Started

### Prerequisites
- Java 17+
- Node.js and npm
- PostgreSQL 15+

### Installation

#### Backend
1. Navigate to the `backend` directory.
2. Run `mvn clean install` to build the project.
3. Configure the database connection in `src/main/resources/application.properties`.
4. Run the application using `mvn spring-boot:run`.

#### Frontend
1. Navigate to the `frontend` directory.
2. Run `npm install` to install dependencies.
3. Start the development server with `npm start`.

### Database Setup
1. Execute the SQL migration script located in `database/migrations/V1__initial_schema.sql` to set up the initial database schema.

## Contributing
Contributions are welcome! Please submit a pull request or open an issue for any enhancements or bug fixes.

## License
This project is licensed under the MIT License. See the LICENSE file for details.