# Project Setup & Configuration Guide

Follow these steps to set up and run the upgraded Job Portal SaaS Platform.

## 1. Prerequisites
- Java 17 or higher
- MySQL 8.0+
- Maven 3.8+
- Gmail Account (for SMTP)

## 2. Database Configuration
1. Open MySQL Workbench or your preferred SQL client.
2. Create a database named `job_portal`.
3. Update `src/main/resources/application.properties` with your credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/job_portal
   spring.datasource.username=YOUR_USERNAME
   spring.datasource.password=YOUR_PASSWORD
   ```

## 3. Email (SMTP) Configuration
1. Go to your Gmail Account Settings -> Security.
2. Enable **2-Step Verification**.
3. Search for **App Passwords**.
4. Generate a new App Password for "Mail".
5. Update `application.properties`:
   ```properties
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-16-digit-app-password
   ```

## 4. Run the Application
1. Open the project in your IDE (IntelliJ/Eclipse).
2. Run `mvn clean install` to download dependencies.
3. Start the application by running `JobPortalApplication.java`.
4. The system will automatically create the tables on the first run.

## 5. Initial Data (Super Admin)
To create the first admin, register a normal user and then manually change their role in the `users` table to `ROLE_SUPER_ADMIN` or use the following SQL:
```sql
UPDATE users SET role = 'ROLE_SUPER_ADMIN', is_enabled = true WHERE email = 'admin@example.com';
```

## 6. Accessing the Platform
- **Home Page**: `http://localhost:8080/`
- **Login**: `http://localhost:8080/login`
- **Register**: `http://localhost:8080/register`

## 7. OTP Flow
- During registration, a 6-digit code is sent to the email.
- During login, 2FA is triggered, and a new code is sent.
- **Developer Tip**: Check the IDE console; OTPs are printed there for easier local testing.

## 8. Company Approval Flow
- New companies register as "Employer".
- They are redirected to OTP verification.
- Once verified, their status is `PENDING`.
- **Super Admin** logs in, goes to "Approval Center", and clicks **Approve**.
- Company receives an automated approval email and can then log in.
