# Document Management System Frontend

This project is a frontend application for a Document Management System built using React and TypeScript. It interacts with a backend API developed in Java Spring Boot and uses PostgreSQL as the database.

## Project Structure

The frontend is organized into several directories:

- **components/**: Contains reusable components for the application.
  - **common/**: Common components like layout, loading indicators, and error boundaries.
  - **forms/**: Components for handling user input through forms.
  - **tables/**: Components for displaying data in table format.
  - **charts/**: Components for visualizing data through charts.

- **pages/**: Contains page components that represent different views in the application.

- **hooks/**: Custom hooks for managing state and side effects.

- **store/**: Contains Redux-related files for state management.
  - **api/**: API service definitions for making requests to the backend.
  - **slices/**: Redux slices for managing application state.
  - **store.ts**: Sets up the Redux store.

- **types/**: TypeScript type definitions and interfaces.

- **utils/**: Utility functions for common tasks.

- **constants/**: Constant values used throughout the application.

## Getting Started

To get started with the frontend application, follow these steps:

1. **Clone the repository**:
   ```
   git clone <repository-url>
   cd document-management-system/frontend
   ```

2. **Install dependencies**:
   ```
   npm install
   ```

3. **Run the application**:
   ```
   npm start
   ```

The application will be available at `http://localhost:3000`.

## Development

- Use TypeScript for type safety and better development experience.
- Follow best practices for React development, including component reusability and state management using Redux.
- Ensure to keep the components organized and maintainable.

## Testing

- Write unit tests for components and hooks using Jest and React Testing Library.
- Ensure that all tests pass before committing changes.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any improvements or bug fixes.

## License

This project is licensed under the MIT License. See the LICENSE file for more details.