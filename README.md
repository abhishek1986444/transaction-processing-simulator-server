# Transaction Processing Simulator Server

Transaction Processing Simulator Server | Java, JDBC, MySQL
Developed a Java-based server that simulates transaction-processing workflows using mock transaction data, implementing request handling, transaction validation, database interaction, and error handling to demonstrate concepts relevant to real-world backend transaction systems.


The server runs locally on port `9090` and uses SSL certificates for secure communication.

---



# Disclaimer

This project is developed for educational, learning, and demonstration purposes only. It is a simulation of a transaction processing system and is not intended for use as a real banking, payment, or financial transaction system.

No real financial transactions or real monetary funds should be processed through this application. The security mechanisms, authentication, database design, and transaction-processing logic are implemented as part of the learning and demonstration scope of the project and should not be considered production-ready financial infrastructure.

AI tools were used during the development of this project for purposes such as coding assistance, debugging, documentation, and learning. .

---

# 1. Features

* User authentication
* Transaction processing
* Transaction status tracking
* Failed request tracking
* JWT-based authentication
* JDBC database connectivity
* SSL certificate support
* Gmail-based email configuration support

---

# 2. Technology Stack

| Technology                  | Purpose                             |
| --------------------------- | ----------------------------------- |
| Java 25                     | Application development and runtime |
| Maven                       | Build and dependency management     |
| MySQL                       | Persistent database                 |
| JDBC                        | Database connectivity               |
| OpenSSL                     | SSL certificate generation          |
| JWT                         | Authentication                      |
| Gmail / Google App Password | Email authentication                |

---

# 3. Prerequisites

Install the following software before running the project:

* Java 25
* Maven
* MySQL Server
* OpenSSL
* Git

If email functionality is enabled, a Google account with **2-Step Verification** is also required for generating a Google App Password.

---

## 3.1 Verify Java

Run:

```bash
java -version
```

The project requires:

```text
Java 25
```

---

## 3.2 Verify Maven

Run:

```bash
mvn -version
```

Make sure Maven is using Java 25.

---

## 3.3 Verify MySQL

Run:

```bash
mysql --version
```

Make sure the MySQL Server is installed and running.

---

## 3.4 Verify OpenSSL

Run:

```bash
openssl version
```

OpenSSL is required for generating the SSL certificate and PKCS#12 keystore.

---

## 3.5 Verify Git

Run:

```bash
git --version
```

---

# 4. Clone the Repository

Clone the repository:

```bash
git clone https://github.com/abhishek1986444/transaction-system-simulation-server.git
```

Enter the project directory:

```bash
cd transaction-system-simulation-server
```

---

# 5. MySQL Database Setup

The application uses MySQL for persistent data storage.

The default local database configuration is:

| Setting       | Value           |
| ------------- | --------------- |
| Database      | `nettyproject2` |
| Database User | `nettyadmin`    |
| Database Host | `localhost`     |
| Database Port | `3306`          |

The JDBC connection URL is:

```text
jdbc:mysql://localhost:3306/nettyproject2
```

---

## 5.1 Start MySQL

Make sure MySQL Server is running.

Open the MySQL client as an administrator:

```bash
mysql -u root -p
```

Enter the MySQL administrator password when prompted.

> **Caution:** The MySQL `root` account is only used here to create the application database and application user. The Java application itself should use `nettyadmin`, not `root`.

---

## 5.2 Create the Application Database

Create the database:

```sql
CREATE DATABASE nettyproject2;
```

Select the database:

```sql
USE nettyproject2;
```

Verify the selected database:

```sql
SELECT DATABASE();
```

Expected result:

```text
nettyproject2
```

---

## 5.3 Create the Application Database User

Create the MySQL user used by the Java application:

```sql
CREATE USER 'nettyadmin'@'localhost' IDENTIFIED BY 'your_mysql_password';
```

Replace:

```text
your_mysql_password
```

with the password you want to use for the MySQL `nettyadmin` account.

Grant access to the application database:

```sql
GRANT ALL PRIVILEGES ON nettyproject2.* TO 'nettyadmin'@'localhost';
```

Apply the privileges:

```sql
FLUSH PRIVILEGES;
```

Verify the user:

```sql
SELECT User, Host
FROM mysql.user
WHERE User = 'nettyadmin';
```

Expected result should contain:

```text
nettyadmin | localhost
```

> **Caution:** This password is the **MySQL database password**. It is completely separate from the Google App Password used for email.

---

## 5.4 Test the Application Database User

Exit MySQL:

```sql
EXIT;
```

Log in using the application account:

```bash
mysql -u nettyadmin -p
```

Enter the MySQL password created in the previous step.

Select the database:

```sql
USE nettyproject2;
```

Verify:

```sql
SELECT DATABASE();
```

Expected result:

```text
nettyproject2
```

---

# 6. Create the Database Tables

The application uses five tables:

| Table             | Purpose                                            |
| ----------------- | -------------------------------------------------- |
| `users`           | Stores user authentication and profile information |
| `accounts`        | Stores user account information                    |
| `balances`        | Stores account balances                            |
| `transactions`    | Stores transaction records                         |
| `failed_requests` | Stores information about failed requests           |

Make sure the application database is selected:

```sql
USE nettyproject2;
```

---

## 6.1 Create `users`

The `users` table stores authentication and user profile information.

```sql
CREATE TABLE users (
    user_id VARCHAR(50) NOT NULL,
    username VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    payment_password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(100),
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_users_username (username),
    UNIQUE KEY uk_users_email (email)
);
```

---

## 6.2 Create `accounts`

The `accounts` table stores accounts associated with users.

```sql
CREATE TABLE accounts (
    account_id VARCHAR(50) NOT NULL,
    user_id VARCHAR(50) NOT NULL,
    account_status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    username VARCHAR(100),
    PRIMARY KEY (account_id),
    KEY idx_accounts_user_id (user_id)
);
```

---

## 6.3 Create `balances`

The `balances` table stores the current balance of each account.

```sql
CREATE TABLE balances (
    account_id VARCHAR(50) NOT NULL,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    version INT DEFAULT 0,
    PRIMARY KEY (account_id)
);
```

---

## 6.4 Create `transactions`

The `transactions` table stores transaction information.

```sql
CREATE TABLE transactions (
    txn_id VARCHAR(64) NOT NULL,
    from_account VARCHAR(50) NOT NULL,
    to_account VARCHAR(50) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    from_username VARCHAR(100),
    to_username VARCHAR(100),
    daily_seq BIGINT NOT NULL,
    txn_date DATE NOT NULL,
    request_id VARCHAR(200),
    PRIMARY KEY (txn_id),
    KEY idx_transactions_from_account (from_account),
    KEY idx_transactions_to_account (to_account)
);
```

---

## 6.5 Create `failed_requests`

The `failed_requests` table stores information about failed requests.

```sql
CREATE TABLE failed_requests (
    serial_no BIGINT NOT NULL AUTO_INCREMENT,
    request_id VARCHAR(100) NOT NULL,
    username VARCHAR(100) NOT NULL,
    reason VARCHAR(100),
    PRIMARY KEY (serial_no),
    UNIQUE KEY uk_failed_requests_request_id (request_id)
);
```

---

## 6.6 Verify the Tables

After creating all five tables:

```sql
SHOW TABLES;
```

Expected tables:

```text
accounts
balances
failed_requests
transactions
users
```

Verify individual tables if required:

```sql
DESCRIBE users;
```

```sql
DESCRIBE accounts;
```

```sql
DESCRIBE balances;
```

```sql
DESCRIBE transactions;
```

```sql
DESCRIBE failed_requests;
```

> **Caution:** Run the `CREATE TABLE` commands only once for a fresh database. Running them again after the tables already exist will produce `Table already exists` errors.

---

# 7. Database Schema

## `users`

Stores authentication and user profile information.

| Column                  | Type           | Key         |
| ----------------------- | -------------- | ----------- |
| `user_id`               | `varchar(50)`  | Primary Key |
| `username`              | `varchar(100)` | Unique      |
| `password_hash`         | `varchar(255)` |             |
| `payment_password_hash` | `varchar(255)` |             |
| `role`                  | `varchar(20)`  |             |
| `status`                | `varchar(20)`  |             |
| `created_at`            | `timestamp`    |             |
| `email`                 | `varchar(255)` | Unique      |
| `name`                  | `varchar(100)` |             |

## `accounts`

Stores accounts associated with users.

| Column           | Type           | Key         |
| ---------------- | -------------- | ----------- |
| `account_id`     | `varchar(50)`  | Primary Key |
| `user_id`        | `varchar(50)`  | Indexed     |
| `account_status` | `varchar(20)`  |             |
| `created_at`     | `timestamp`    |             |
| `username`       | `varchar(100)` |             |

## `balances`

Stores the current balance of an account.

| Column       | Type            | Key         |
| ------------ | --------------- | ----------- |
| `account_id` | `varchar(50)`   | Primary Key |
| `balance`    | `decimal(15,2)` |             |
| `version`    | `int`           |             |

## `transactions`

Stores transaction information.

| Column          | Type            | Key         |
| --------------- | --------------- | ----------- |
| `txn_id`        | `varchar(64)`   | Primary Key |
| `from_account`  | `varchar(50)`   | Indexed     |
| `to_account`    | `varchar(50)`   | Indexed     |
| `amount`        | `decimal(15,2)` |             |
| `status`        | `varchar(20)`   |             |
| `created_at`    | `timestamp`     |             |
| `from_username` | `varchar(100)`  |             |
| `to_username`   | `varchar(100)`  |             |
| `daily_seq`     | `bigint`        |             |
| `txn_date`      | `date`          |             |
| `request_id`    | `varchar(200)`  |             |

## `failed_requests`

Stores failed request information.

| Column       | Type           | Key                         |
| ------------ | -------------- | --------------------------- |
| `serial_no`  | `bigint`       | Primary Key, Auto Increment |
| `request_id` | `varchar(100)` | Unique                      |
| `username`   | `varchar(100)` |                             |
| `reason`     | `varchar(100)` |                             |

---

# 8. SSL Configuration

The server uses SSL-related files for secure communication.

The required files are:

```text
private.key
cert.pem
keystore.p12
```

OpenSSL is used to generate these files.

---

## 8.1 Generate the Private Key and Certificate

Run:

```bash
openssl req -x509 -newkey rsa:2048 -nodes -keyout private.key -out cert.pem -days 365 -subj "/CN=localhost"
```

This generates:

```text
private.key
cert.pem
```

| File          | Purpose                     |
| ------------- | --------------------------- |
| `private.key` | SSL private key             |
| `cert.pem`    | Self-signed SSL certificate |

The generated certificate uses:

* RSA 2048-bit key
* 365-day validity
* `localhost` as the certificate common name

> **Caution:** This is a self-signed certificate intended for local/development use. It is not equivalent to a certificate issued by a trusted public Certificate Authority.

---

## 8.2 Generate the PKCS#12 Keystore

Run:

```bash
openssl pkcs12 -export -in cert.pem -inkey private.key -out keystore.p12
```

OpenSSL will ask for an export password.

This generates:

```text
keystore.p12
```

The application should have access to:

```text
private.key
cert.pem
keystore.p12
```

at the locations expected by the project.

> **Caution:** Keep `private.key` and `keystore.p12` private. Do not upload private keys to a public Git repository.

---

## 8.3 Verify the Certificate

Inspect the certificate:

```bash
openssl x509 -in cert.pem -text -noout
```

Inspect the PKCS#12 keystore:

```bash
openssl pkcs12 -info -in keystore.p12 -noout
```

Enter the keystore password when prompted.

---

# 9. Application Configuration

The project uses two separate properties files:

```text
src/main/resources/application.properties
```

and:

```text
src/main/resources/configurationfiles/security.properties
```

General application settings are maintained in `application.properties`.

Database and security-related credentials are maintained in `security.properties`.

---

# 10. `application.properties`

File:

```text
src/main/resources/application.properties
```

The application configuration contains settings such as:

```properties
server.port=9090
server.host=localhost

db.transcationtable=transactions
db.accounttable=accounts
db.balancestable=balances
db.userstable=users

jwt.expirationtime=30
```

## Configuration

| Property              | Purpose                       |
| --------------------- | ----------------------------- |
| `server.port`         | Port on which the server runs |
| `server.host`         | Server host                   |
| `db.transcationtable` | Transactions table            |
| `db.accounttable`     | Accounts table                |
| `db.balancestable`    | Balances table                |
| `db.userstable`       | Users table                   |
| `jwt.expirationtime`  | JWT expiration setting        |

> **Important:** The property name `db.transcationtable` is intentionally preserved because it is the property name currently used by the application. Do not rename it to `db.transactiontable` unless the application source code is also changed.

---

# 11. `security.properties`

File:

```text
src/main/resources/configurationfiles/security.properties
```

The local configuration should follow this structure:

```properties
db.url=jdbc:mysql://localhost:3306/nettyproject2
db.user=nettyadmin
db.password=your_mysql_password
db.table=users

jwt.secretkey=your-secret-key

mail.id=your_gmail_address
mail.secretkey=your_google_app_password
```

## Configuration

| Property         | Purpose                       |
| ---------------- | ----------------------------- |
| `db.url`         | MySQL database connection URL |
| `db.user`        | MySQL username                |
| `db.password`    | MySQL password                |
| `db.table`       | User table                    |
| `jwt.secretkey`  | JWT secret                    |
| `mail.id`        | Gmail/email account           |
| `mail.secretkey` | Google App Password           |

---

# 12. Important: MySQL Password vs Google App Password

The project may use two completely different passwords.

## MySQL Password

This is the password created for:

```text
nettyadmin
```

It is configured as:

```properties
db.password=your_mysql_password
```

It is used to connect the Java application to MySQL.

---

## Google App Password

The Google App Password is used for email authentication.

It is **not** the same as the MySQL password.

If Gmail functionality is used, generate a Google App Password from your Google Account.

You can access the Google App Passwords page here:

https://myaccount.google.com/apppasswords

Google App Passwords are **16-character app-specific passwords**.

Configure the generated value as:

```properties
mail.id=your_gmail_address
mail.secretkey=your_google_app_password
```

For example:

```properties
mail.id=yourname@gmail.com
mail.secretkey=your_16_character_app_password
```

> **Caution:** Never put your normal Google Account password into `mail.secretkey`. Use the Google-generated App Password.

> **Caution:** Do not publish the Google App Password in GitHub, screenshots, README files, assignments submitted publicly, or source code.

---

# 13. Important Local Database Configuration

The normal local setup uses:

```properties
db.url=jdbc:mysql://localhost:3306/nettyproject2
```

This assumes:

* MySQL is running on the same computer.
* MySQL is using port `3306`.
* The database is named `nettyproject2`.
* The application user is `nettyadmin`.

The credentials must match the MySQL account created earlier:

```properties
db.user=nettyadmin
db.password=your_mysql_password
```

---

# 14. Security and Credential Precautions

The following values are sensitive:

```properties
db.password=...
jwt.secretkey=...
mail.secretkey=...
```

The following files may also contain sensitive cryptographic material:

```text
private.key
keystore.p12
```

## Never commit real secrets

Do not commit:

* MySQL passwords
* Google App Passwords
* JWT secrets
* Private SSL keys
* Keystore passwords
* Real email credentials

Use placeholders in a public repository:

```properties
db.password=your_mysql_password
jwt.secretkey=your-secret-key
mail.id=your_gmail_address
mail.secretkey=your_google_app_password
```

Then configure the real values locally.

### If a secret is accidentally published

If a real password, Google App Password, JWT secret, or private key is accidentally pushed to a public repository:

1. Treat the credential as compromised.
2. Change/revoke the credential immediately.
3. Generate a replacement credential.
4. Remove the secret from the repository.
5. Check Git history if the secret was committed previously.

Simply deleting the secret from the latest version of a file does not necessarily remove it from Git history.

---

# 15. Build the Project

After completing the database, SSL, and configuration setup, return to the project root.

Run:

```bash
mvn clean package
```

This will:

1. Remove previous build output.
2. Resolve Maven dependencies.
3. Compile the project.
4. Execute the configured build/test steps.
5. Package the application.

---

# 16. Useful Maven Commands

Clean the project:

```bash
mvn clean
```

Compile:

```bash
mvn compile
```

Run tests:

```bash
mvn test
```

Package:

```bash
mvn package
```


------ 

# Note :

**Before starting the server, run the User Seeder in the server model to populate the required users.**

-----


# 17. Start the Server

The server is configured to use:

```properties
server.port=9090
server.host=localhost
```

Therefore, the server runs locally on:

```text
localhost:9090
```

Start the application using the project's configured Maven/application startup command.

If Maven generates an executable JAR in the `target` directory, it can be started using:

```bash
java -jar target/<generated-jar-name>.jar
```

Replace:

```text
<generated-jar-name>
```

with the actual JAR generated by Maven.

> **Note:** The exact startup command depends on the project's Maven configuration and main application class. Do not assume the JAR is executable unless the project's Maven configuration creates an executable JAR.

---

# 18. Client Configuration

The client must connect to the server using:

```text
Host: localhost
Port: 9090
```

Therefore, the server address is:

```text
localhost:9090
```

Start the Java server before starting the client.

---

# 19. Verify the Running System

After starting the server, verify the following:

1. MySQL Server is running.
2. The `nettyproject2` database exists.
3. The `nettyadmin` user can access the database.
4. All five required tables exist.
5. SSL files are available at the locations expected by the application.
6. `application.properties` is configured.
7. `security.properties` is configured.
8. Maven build succeeds.
9. The Java server starts successfully.
10. The server is listening on port `9090`.
11. The client can connect to `localhost:9090`.

---

# 20. Verify Data Using MySQL

Connect using the application database user:

```bash
mysql -u nettyadmin -p
```

Select the database:

```sql
USE nettyproject2;
```

Check users:

```sql
SELECT * FROM users;
```

Check accounts:

```sql
SELECT * FROM accounts;
```

Check balances:

```sql
SELECT * FROM balances;
```

Check transactions:

```sql
SELECT * FROM transactions;
```

Check failed requests:

```sql
SELECT * FROM failed_requests;
```

> **Caution:** Avoid posting database query results publicly if they contain real usernames, email addresses, account information, transaction information, or other private data.

---

# 21. Useful MySQL Commands

Show all databases:

```sql
SHOW DATABASES;
```

Select the application database:

```sql
USE nettyproject2;
```

Show tables:

```sql
SHOW TABLES;
```

Describe a table:

```sql
DESCRIBE users;
```

or:

```sql
DESC users;
```

Count users:

```sql
SELECT COUNT(*) FROM users;
```

Count accounts:

```sql
SELECT COUNT(*) FROM accounts;
```

Count transactions:

```sql
SELECT COUNT(*) FROM transactions;
```

Check the current database:

```sql
SELECT DATABASE();
```

Exit MySQL:

```sql
EXIT;
```

---

# 22. Troubleshooting

## 22.1 MySQL Connection Error

Verify that MySQL is installed:

```bash
mysql --version
```

Try logging in:

```bash
mysql -u nettyadmin -p
```

Then:

```sql
USE nettyproject2;
```

Check the application configuration:

```properties
db.url=jdbc:mysql://localhost:3306/nettyproject2
db.user=nettyadmin
db.password=your_mysql_password
```

Make sure the password is the **MySQL password**, not the Google App Password.

---

## 22.2 Database Does Not Exist

Create the database:

```sql
CREATE DATABASE nettyproject2;
```

Then:

```sql
USE nettyproject2;
```

---

## 22.3 Access Denied for `nettyadmin`

Log in as a MySQL administrator and run:

```sql
GRANT ALL PRIVILEGES ON nettyproject2.* TO 'nettyadmin'@'localhost';
FLUSH PRIVILEGES;
```

Then test:

```bash
mysql -u nettyadmin -p
```

---

## 22.4 Tables Are Missing

Check:

```sql
USE nettyproject2;
SHOW TABLES;
```

The expected tables are:

```text
users
accounts
balances
transactions
failed_requests
```

If the tables are missing, execute the `CREATE TABLE` commands in Section 6.

---

## 22.5 Table Already Exists

If MySQL reports:

```text
ERROR 1050 (42S01): Table 'users' already exists
```

the table has already been created.

Verify it:

```sql
DESCRIBE users;
```

Do not recreate the table unless you intentionally want to rebuild the database.

---

## 22.6 Port 9090 Is Already in Use

On Windows:

```cmd
netstat -ano | findstr :9090
```

This displays the process using port `9090`.

If necessary:

```cmd
taskkill /PID <PID> /F
```

> **Caution:** Only terminate a process if you are certain it is safe to do so.

---

## 22.7 OpenSSL Is Not Recognized

Run:

```bash
openssl version
```

If the command is not recognized, install OpenSSL and make sure its executable directory is included in the system `PATH`.

---

## 22.8 SSL Certificate Error

Verify the certificate:

```bash
openssl x509 -in cert.pem -text -noout
```

Verify the keystore:

```bash
openssl pkcs12 -info -in keystore.p12 -noout
```

Also verify that:

```text
private.key
cert.pem
keystore.p12
```

are stored at the locations expected by the application.

---

## 22.9 Gmail / Email Authentication Error

If email functionality fails, verify:

```properties
mail.id=your_gmail_address
mail.secretkey=your_google_app_password
```

Make sure `mail.secretkey` contains the **Google App Password**, not the normal Gmail password.

If the App Password has been revoked, generate a new one from:

https://myaccount.google.com/apppasswords

---

## 22.10 Maven Build Fails

Check Java:

```bash
java -version
```

Check Maven:

```bash
mvn -version
```

Make sure Maven is using Java 25.

Then run:

```bash
mvn clean
```

followed by:

```bash
mvn clean package
```

---

# 23. Recommended Setup Order

For a completely new machine, follow these steps in order:

```text
1. Install Java 25
        ↓
2. Install Maven
        ↓
3. Install MySQL
        ↓
4. Install OpenSSL
        ↓
5. Install Git
        ↓
6. Clone the repository
        ↓
7. Start MySQL
        ↓
8. Create nettyproject2
        ↓
9. Create nettyadmin
        ↓
10. Grant database permissions
        ↓
11. Create the five database tables
        ↓
12. Generate private.key
        ↓
13. Generate cert.pem
        ↓
14. Generate keystore.p12
        ↓
15. Configure application.properties
        ↓
16. Configure security.properties
        ↓
17. Configure MySQL password
        ↓
18. Configure Google App Password if email is used
        ↓
19. Run mvn clean package
        ↓
20. Start the Java server
        ↓
21. Connect the client to localhost:9090
        ↓
22. Verify database/server/client operation
```

---

# 24. Final Configuration Checklist

Before running the server, verify:

### Software

* [ ] Java 25 installed
* [ ] Maven installed
* [ ] MySQL Server installed and running
* [ ] OpenSSL installed
* [ ] Git installed

### Repository

* [ ] Repository cloned
* [ ] Project directory opened

### MySQL

* [ ] `nettyproject2` database created
* [ ] `nettyadmin` user created
* [ ] MySQL password configured correctly
* [ ] `nettyadmin` has access to `nettyproject2`

### Database Tables

* [ ] `users` table exists
* [ ] `accounts` table exists
* [ ] `balances` table exists
* [ ] `transactions` table exists
* [ ] `failed_requests` table exists

### SSL

* [ ] `private.key` generated
* [ ] `cert.pem` generated
* [ ] `keystore.p12` generated
* [ ] SSL files are stored at the expected locations

### Application Configuration

* [ ] `application.properties` configured
* [ ] `security.properties` configured
* [ ] MySQL URL is correct
* [ ] MySQL username is correct
* [ ] MySQL password is correct
* [ ] JWT secret configured
* [ ] Gmail address configured if email functionality is used
* [ ] Google App Password configured if email functionality is used

### Build and Runtime

* [ ] `mvn clean package` succeeds
* [ ] Server starts successfully
* [ ] Server uses port `9090`
* [ ] Client uses `localhost:9090`

---

# 25. Quick Start

For users who have already installed all prerequisites.

## Step 1 — Clone

```bash
git clone https://github.com/abhishek1986444/transaction-system-simulation-server.git
cd transaction-system-simulation-server
```

## Step 2 — Create the Database

Log in to MySQL:

```bash
mysql -u root -p
```

Run:

```sql
CREATE DATABASE nettyproject2;

CREATE USER 'nettyadmin'@'localhost'
IDENTIFIED BY 'your_mysql_password';

GRANT ALL PRIVILEGES
ON nettyproject2.*
TO 'nettyadmin'@'localhost';

FLUSH PRIVILEGES;

USE nettyproject2;
```

## Step 3 — Create the Tables

Execute the five `CREATE TABLE` statements from **Section 6**.

Verify:

```sql
SHOW TABLES;
```

## Step 4 — Generate SSL Files

Generate the certificate:

```bash
openssl req -x509 -newkey rsa:2048 -nodes -keyout private.key -out cert.pem -days 365 -subj "/CN=localhost"
```

Generate the keystore:

```bash
openssl pkcs12 -export -in cert.pem -inkey private.key -out keystore.p12
```

## Step 5 — Configure the Application

Configure:

```text
src/main/resources/application.properties
```

and:

```text
src/main/resources/configurationfiles/security.properties
```

The database configuration should point to:

```properties
db.url=jdbc:mysql://localhost:3306/nettyproject2
db.user=nettyadmin
db.password=your_mysql_password
```

If email functionality is required:

```properties
mail.id=your_gmail_address
mail.secretkey=your_google_app_password
```

Generate the Google App Password from:

https://myaccount.google.com/apppasswords

## Step 6 — Build

Run:

```bash
mvn clean package
```

## Step 7 — Start the Server

Start the application using the project's configured startup command.

If an executable JAR is generated:

```bash
java -jar target/<generated-jar-name>.jar
```

## Step 8 — Connect the Client

Configure the client to use:

```text
Host: localhost
Port: 9090
```

The server should then be available at:

```text
localhost:9090
```

---

# 26. Expected Environment

After completing the setup, the system should contain the following components:

```text
                         Java Server
                              |
                         localhost:9090
                              |
                           Client
                              |
                              |
                         MySQL :3306
                              |
                         nettyproject2
                              |
              +---------------+---------------+
              |               |               |
            users          accounts        balances
                              |
                         transactions
                              |
                       failed_requests
```

The main local configuration is:

```text
Java Version    : 25
Build Tool      : Maven
Database        : MySQL
Database Name   : nettyproject2
Database User   : nettyadmin
Database Host   : localhost
Database Port   : 3306
Server Host     : localhost
Server Port     : 9090
```

---

# 27. Important Security Reminder

Before pushing the project to a public Git repository, make sure the repository does **not** contain real:

```text
MySQL passwords
Google App Passwords
JWT secrets
Private SSL keys
Keystore passwords
Email credentials
```

Use placeholders such as:

```properties
db.password=your_mysql_password
jwt.secretkey=your-secret-key
mail.id=your_gmail_address
mail.secretkey=your_google_app_password
```

Keep the real values only in your local environment/configuration.

Never use your normal Google Account password as the Gmail application password. Use a Google-generated **App Password** when the application requires Gmail authentication.

---

# 28. Setup Summary

The complete setup can be summarized as:

```text
Install prerequisites
        ↓
Clone repository
        ↓
Create MySQL database
        ↓
Create nettyadmin user
        ↓
Grant database permissions
        ↓
Create users table
        ↓
Create accounts table
        ↓
Create balances table
        ↓
Create transactions table
        ↓
Create failed_requests table
        ↓
Generate SSL certificate
        ↓
Generate PKCS#12 keystore
        ↓
Configure application.properties
        ↓
Configure security.properties
        ↓
Configure MySQL credentials
        ↓
Configure Google App Password
        ↓
Build with Maven
        ↓
Start Java server
        ↓
Connect client
        ↓
Verify database and server
```

The application is ready when the server successfully starts on:

```text
localhost:9090
```
