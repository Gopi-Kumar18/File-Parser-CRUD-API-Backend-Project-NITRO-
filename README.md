```File Parser CRUD API with Real-Time Progress Tracking```

This is a backend application built with Spring Boot and MySQL that provides a RESTful API for uploading, parsing, and managing files. It features asynchronous processing for large files and real-time progress tracking using WebSockets. The API is secured using JWT authentication.

Features
User Authentication: Secure registration and login using JWT (JSON Web Tokens).

File Upload: Supports large file uploads via a multipart/form-data endpoint.

Asynchronous Parsing: File parsing is handled in the background, preventing API blocking. Currently supports text extraction from .CSV files.

Real-Time Progress Tracking: Uses WebSockets (with SockJS & STOMP) to push live progress updates to the client.

Full CRUD Functionality:

Create: Upload files.

Read: List all files and retrieve parsed content of a specific file.

Delete: Remove a file and its associated data.

Database Persistence: All file metadata and parsed content are stored in a MySQL database.

Tech Stack
Backend: Spring Boot 3

Database: MySQL

Security: Spring Security, JWT

Real-time Communication: Spring WebSocket, STOMP, SockJS

```File Parsing: Open CSV```

Build Tool: Maven

🚀 Setup and Installation
Follow these steps to get the application running locally.

Prerequisites
Java (JDK 17 or later)

Apache Maven

MySQL Server

An API testing tool like Postman

***1. Clone the Repository***
git clone File-Parser-CRUD-API-Backend-Project-NITRO-
cd File-Parser-CRUD-API-Backend-Project-NITRO-

2. Configure the Database
Open your MySQL client.

Create a new database for the application. The application will create the necessary tables on startup.

CREATE DATABASE **file_parser_db**;

3. Configure Application Properties
Navigate to src/main/resources/application.properties.

Update the MySQL connection details (spring.datasource.url, spring.datasource.username, spring.datasource.password) to match your local setup.

```spring.datasource.url=jdbc:mysql://localhost:3306/file_parser_db```
```spring.datasource.username=your_mysql_username```
```spring.datasource.password=your_mysql_password```

```jwt.secret=your-very-long-and-secure-secret-key-that-is-at-least-256-bitsa long, random, and secure string()```

4. Build and Run the Application
You can run the application using :

mvn spring-boot:run

```The application will start on http://localhost:8080.```

**API Documentation**
The API is secured with JWT. You must first register and authenticate to get a token, which must be included in the Authorization header for all protected endpoints.

Authorization Header Format: Bearer <your_jwt_token>

**Authentication**
***1. Register a New User***

```Endpoint: POST /register```

Description: Creates a new user account.

```Request Body:```

```{```
    ```"username": "testuser",```
    
    ```"password": "password123"```
```}```

```Success Response (200 OK):```

```{```
   ``` "id": 1,```
    ```"username": "testuser",```
    ```"password": "$2a$10$..." ```
```}```

***2. Authenticate and Get JWT***
   
```Endpoint: POST /authenticate```

Description: Logs in a user and returns a JWT.

```Request Body:```

```{```
    ```"username": "testuser",```
    
    ```"password": "password123"```
```}```


```Success Response (200 OK):```

```eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTcyNDIzMzgwMCwiZXhwIjoxNzI0MjY5ODAwfQ.exampleTokenString```

**File Management (Authentication Required)**
***1. Upload a File***

```Endpoint: POST /files```

Description: Uploads a file for processing.

```Request Body: multipart/form-data with a key file.```

```Success Response (200 OK):```

```{```
    ```"id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",```
    ```"fileName": "mydocument.pdf",```
    ```"fileType": "application/pdf",```
    ```"size": 102400,```
    ```"status": "UPLOADING",```
    ```"parsedContent": null,```
    ```"createdAt": "2025-08-21T14:00:00.000000"```
```}```

***2. Get File Progress***
   
```Endpoint: GET /files/{file_id}/progress```

Description: Retrieves the current processing status and progress of a file.

```Success Response (200 OK):```

```{```
    ```"fileId": "a1b2c3d4-e5f6-7890-1234-567890abcdef",```
    ```"status": "PROCESSING",```
    ```"progress": 50```
```}```

***3. Get Parsed File Content***
   
```Endpoint: GET /files/{file_id}```

Description: Fetches the parsed text content of a file once it's ready.

```Success Response (200 OK):```

"This is the full text content extracted from the PDF document..."

```Error Response (If processing failed):```

```{```
   ``` "message": "File processing failed."```
```}```

***4. List All Files***

```Endpoint: GET /files```

Description: Returns a list of all uploaded files with their metadata.

```Success Response (200 OK):```

```[```
    ```{```
        ```"id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",```
        ```"fileName": "mydocument.pdf",```
        ```"fileType": "application/pdf",```
       ``` "size": 102400,```
        ```"status": "READY",```
        ```"parsedContent": "This is the full text...",```
        ```"createdAt": "2025-08-21T14:00:00.000000"```
    ```}```
```]```

***5. Delete a File***

```Endpoint: DELETE /files/{file_id}```

Description: Deletes a file and its associated data from the server.

```Success Response (200 OK):```

```"File deleted successfully"```

Real-Time Progress Tracking (WebSockets)
To receive live progress updates, a client must:

Connect: Establish a WebSocket connection to the /ws endpoint.

Subscribe: After uploading a file and getting its id, the client must send a SUBSCRIBE frame to the destination /topic/progress/{file_id}.

The server will then automatically push messages to this topic as the file's status changes.

```Sample Progress Message:```

```{```
    ```"fileId": "a1b2c3d4-e5f6-7890-1234-567890abcdef",```
    ```"status": "READY",```
    ```"progress": 100```
```}```

**Testing with Postman**
A Postman collection is provided to make testing easy.

Import the Collection: Import the File Parser API.postman_collection.json file into Postman.

Set Up Environment: The collection is configured to use a jwt_token variable. After you run the ***POST /authenticate request, the collection's test script will automatically save the token to this variable.***

Run Requests: All protected endpoints under the "File Management" folder are pre-configured to use this token for authentication. Simply run the requests in order.

