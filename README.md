## ESchool

**ESchool** is a full‑stack web application for managing school operations and content.  
The project is split into a **Spring Boot** backend and a **Vue 3** frontend, with **PostgreSQL** as the primary database.

I am following this:
- for customer view: https://ecommerce-frontend-view.netlify.app/
- for admin view: https://ecommerce-admin-view.netlify.app/
- this is the youtube video link which I followed: https://youtu.be/KKJeBswz4e8

### Tech stack

- **Backend**: Spring Boot, Spring Security, JWT, JPA/Hibernate, Maven
- **Frontend**: Vue 3, Vue Router, Pinia (or Vuex), Vite
- **Database**: PostgreSQL
- **Build & tooling**: Node.js, npm, Maven

### Project structure

- `backend/` – Spring Boot REST API and business logic
- `frontend/` – Vue SPA client

### Prerequisites

- **Java** 17+ (or the version configured in `backend/pom.xml`)
- **Maven** 3.8+ (or use the included `mvnw` wrapper)
- **Node.js** 18+ and **npm**
- **PostgreSQL** running locally or accessible via network

### Backend setup (`backend/`)

1. Navigate to the backend folder:

   ```bash
   cd backend
   ```

2. Configure database and application properties (update values to match your environment):

   ```properties
   # src/main/resources/application.properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/eschool
   spring.datasource.username=your_db_user
   spring.datasource.password=your_db_password
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   jwt.secret=your_jwt_secret
   ```

3. Build and run the backend:

   ```bash
   ./mvnw spring-boot:run
   # or, if Maven is installed globally:
   mvn spring-boot:run
   ```

4. The API will typically be available at:

   - `http://localhost:8080`

### Frontend setup (`frontend/`)

1. Navigate to the frontend folder:

   ```bash
   cd frontend
   ```

2. Install dependencies:

   ```bash
   npm install
   ```

3. Configure environment variables (example):

   ```bash
   # .env.local
   VITE_API_BASE_URL=http://localhost:8080
   ```

4. Run the development server:

   ```bash
   npm run dev
   ```

5. The frontend will typically be available at:

   - `http://localhost:5173` (or the port shown in the terminal)

### Building for production

- **Backend**:

  ```bash
  cd backend
  ./mvnw clean package
  # produces a fat JAR in target/
  ```

- **Frontend**:

  ```bash
  cd frontend
  npm run build
  # output in dist/
  ```

You can serve the frontend build with any static file server or integrate it with the backend (e.g., by serving the `dist/` directory from Spring Boot).

### Running tests

- **Backend**:

  ```bash
  cd backend
  ./mvnw test
  ```

- **Frontend** (if configured, e.g., Vitest or Jest):

  ```bash
  cd frontend
  npm test
  # or
  npm run test
  ```

### Git & ignore configuration

- Root `.gitignore` covers shared artifacts, environment files, and both module build outputs.
- `backend/.gitignore` is tailored for Spring Boot and Java/Maven.
- `frontend/.gitignore` is tailored for Vue/Vite, Node, and frontend tooling.

This ensures that build artifacts, local environment files, and IDE metadata are not committed to version control.

### License

This project is currently not licensed for public redistribution unless a LICENSE file is added. Add a suitable license (e.g., MIT, Apache 2.0) before open‑sourcing.
