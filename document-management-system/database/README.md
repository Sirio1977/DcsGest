# README for Database Setup

This directory contains the necessary information and scripts for setting up the database for the Document Management System project.

## Database Overview

The project utilizes PostgreSQL as the relational database management system (RDBMS). The database schema is designed to support the various functionalities of the document management system, including handling entities such as companies, clients, suppliers, articles, documents, and financial transactions.

## Migration Scripts

The `migrations` directory contains SQL migration scripts that define the initial schema of the database. The migration scripts are used to create the necessary tables and relationships between them.

### Initial Schema

- **File:** `V1__initial_schema.sql`
- **Description:** This script sets up the initial database schema, including all required tables and constraints.

## Setup Instructions

1. **Install PostgreSQL**: Ensure that PostgreSQL is installed on your machine. You can download it from the [official PostgreSQL website](https://www.postgresql.org/download/).

2. **Create Database**: Create a new database for the Document Management System.

   ```sql
   CREATE DATABASE document_management_system;
   ```

3. **Run Migration Script**: Execute the migration script to set up the initial schema.

   ```bash
   psql -U your_username -d document_management_system -f migrations/V1__initial_schema.sql
   ```

   Replace `your_username` with your PostgreSQL username.

## Additional Information

For further details on the database schema and its design, refer to the comments within the migration scripts or consult the project documentation.