# Learning Management System (LMS) – Backend API

This is the backend ecosystem for a high-performance **Learning Management System (LMS)** platform built with scalability, security, and technical robustness in mind. The project strictly follows the principles of **Clean Architecture** and **DDD (Domain-Driven Design)** to ensure low coupling between core business rules and infrastructure providers.

## 🚀 Architecture & Tech Stack

The application is structured into well-defined layers (`domain`, `application`, `adapter`, and `infrastructure`), completely isolating the business core from persistence frameworks and external libraries.

* **Core:** Java 21 & Spring Boot 4.x


* **Security:** Spring Security with OAuth2 Resource Server and stateless authentication via JWT tokens (Signed using RSA asymmetric cryptographic keys).


* **Relational Database:** PostgreSQL as the primary database managed with Flyway for evolutionary database migrations.


* **Performance & Cache:** Redis is used for fast caching of video progress, short-lived idempotency locks, and query optimization.


* **Storage (Content Delivery):** AWS S3 SDK integration for secure, isolated video uploads and protected content streaming.


* **Document Generation:** Apache PDFBox 3.x for dynamic compilation, rendering, and issuance of PDF certificates.



---

## 📦 System Modules

The system is divided into the following specialized business modules:

### 1. User & Identity Service

* Full management of user credentials (`tb_identity`) and social/biographical profiles (`tb_profile`).


* Role-Based Access Control (RBAC) supporting the following roles: `ADMIN`, `INSTRUCTOR`, `USER`, and `SUBSCRIBER`.



### 2. Catalog Service

* Core content inventory system handling courses, modules, and lessons.


* Dynamic creation of course flows mapping modules and lessons sorted by ordering position, bound to certified instructors.


* Native, programmatic extraction of video duration (in seconds) directly from MP4 file headers upon upload.



### 3. Payment & Enrollment Service

* Subscription purchase flows integrated with a simulated payment gateway (*Mock*).


* Strict **Idempotency** control powered by Redis to block duplicate transactions and double-charging on concurrent user requests.


* Asynchronous payment execution leveraging Java 21 **Virtual Threads** to maximize concurrency without blocking the primary HTTP response threads.



### 4. Progression Service

* Asynchronous *heartbeat* engine receiving periodic watch-time markers from the video player to track progress.


* Automated logic: whenever a student consumes **95% or more** of a lesson's total duration, it is automatically flagged as completed.


* Drastic reduction of relatioal database heavy writes by storing live states in Redis and flushing selectively to PostgreSQL.



### 5. Certification Service

* Course completion validator: once a student achieves $100\%$ progress across all mandatory lessons in a course, a certificate is automatically triggered.


* The system uses Apache PDFBox to dynamically overlay the student's name and course data on a base template and uploads the resulting PDF to a secure S3 bucket.



---

## 🛠️ How to Run the Project Locally

### Prerequisites

* Java 21 JDK installed
* Docker and Docker Compose installed

### Step-by-Step

1. **Spin Up Infrastructure Containers (PostgreSQL):**
   Run the following command in the root folder containing the `compose.yml` file:


```bash
docker compose up -d

```


2. **Environment Variables:**
   Create a `.env` file based on the `.env.example` file provided in the project root. Configure your PostgreSQL credentials and S3 bucket connection variables (Supabase Storage / MinIO).


3. **Compile and Start the Application:**
   The project features an **Embedded Redis** server (`embedded-redis`) enabled for the `default` Spring profile. This means you do not need to install Redis on your local machine to test or run the app in dev mode!


```bash
./gradlew bootRun

```


4. **Run Automated Tests:**
   The project includes unit tests and database integration tests (`@DataJpaTest`) with a strict JaCoCo coverage validation rule requiring at least $80\%$ coverage.


```bash
./gradlew test

```



---

## 📬 Testing the API (Postman)

The project includes an optimized Postman collection fully automated with variables and script hooks.

Once you trigger the **Login endpoint (`POST /api/v1/auth`)**, a test script automatically captures the `accessToken` from the response body and saves it to a `{{token}}` variable. All subsequent protected endpoints inherit this variable, completely removing the hassle of copying and pasting JWT tokens manually.

* The collection import file is located at the root under the name: `learning management system.postman_collection.json`.