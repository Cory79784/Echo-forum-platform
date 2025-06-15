1. # Forum System (Spring Boot + Redis + Kafka)

  A basic yet fully functional forum backend system, developed using Spring Boot. This project supports secure user authentication, interactive user features (posts, comments, likes, follows), caching strategies with Redis, asynchronous messaging with Kafka and global search with Elasticsearch.

---

  ## Table of Contents

  - [Features](#features)
  - [Architecture](#architecture)
  - [Core Functionalities](#core-functionalities)
  - [Tech Stack](#tech-stack)
  - [Project Highlights](#project-highlights)
  - [Setup & Usage](#setup--usage)
  - [Learning Outcomes](#learning-outcomes)

---

  ## Features

  - Email-based account activation during registration
  - Password encryption using MD5 + salt
  - Login verification with random code (CAPTCHA)
  - Session-based login state management
  - Role-based access control for guests vs logged-in users
  - Post publishing, commenting, private messaging with sensitive word filtering
  - Like and follow systems
  - System notification via message queue

---

  ## 🔧 Core Functionalities

  1. **Login State Management**  
     - Tracks user login status using interceptors and session persistence.
     - Solves statelessness in HTTP by binding login context.
     - Protects access to sensitive resources for logged-in users only.

  2. **Hot Data Caching**  
     - Stores frequently queried data (e.g., user profiles) in Redis to reduce DB stress.
     - Improves system performance and response time.

  3. **Cache Consistency Strategy**  
     - Handles cache invalidation on updates (e.g., follow/unfollow).
     - Uses a transactional pattern: *update DB → delete cache*, ensuring consistency.

  4. **Asynchronous Messaging with Kafka**  
     - Decouples logic for system notifications (likes, follows, comments).
     - Pushes events to Kafka queues and processes them asynchronously.
     - Reduces blocking during peak interaction times.

---

  ## 🧰 Tech Stack

  - **Backend**: Java · Spring Boot · Spring MVC · MyBatis
  - **Database**: MySQL
  - **Cache**: Redis
  - **Message Queue**: Kafka
  - **Others**: Spring Security · Lombok · Maven · Elasticsearch

---

  ## 🚀 Setup & Usage

  > ⚙️ *You can expand this section with actual commands once deployment is needed.*

  ```bash
  # Clone the repository
  git clone https://github.com/your-username/forum-system.git
  
  # Enter the project directory
  cd forum-system
  
  # Build the project
  mvn clean install
  
  # Run the project
  mvn spring-boot:run
  ```
