# CarGo - Car Rental Application

# CarGo
Car Rental Web Application

**Authors:**
- Kirththiga Murugupillai
- Aleksandro Echavarria-Mercier

---

## Project Description
CarGo is a **Spring Boot web application** for browsing, booking, and reserving rental cars.

- Customers can **register/login** and search for vehicles by category (SUV, Luxury, Economy, etc.).
- Reservations are validated against car availability, with pricing based on rental duration.
- A secure **Spring Security** authentication system ensures role-based access control:
    - **CUSTOMER** → browse cars, make reservations, upload documents.
    - **ADMIN** → manage cars, reservations, and perform CRUD operations on users.
- File uploads (e.g., driver’s license photos/scans) are supported.
- Styled with **Bootstrap 5** for a professional UI, with real-time feedback on actions like successful bookings or login errors.
- The final version will be hosted on **AWS** (EC2, RDS, S3).

**Optional features (time-permitting):**
- AJAX for dynamic updates
- Email notifications
- Online payments (Stripe API)
- Third-party logins (e.g., Google OAuth)

---

## Technologies Used
- Spring Boot
- Thymeleaf
- Spring Security
- MySQL (or Amazon RDS)
- Bootstrap 5
- AWS EC2 / Elastic Beanstalk
- Amazon S3

---

## Additional Libraries & APIs
- Stripe API (for payments).

---

## Challenges
- Implementing car availability checks based on overlapping reservation end/start dates.

---

## Planned Application URLs
- `/login` → Login page
- `/register` → Registration page
- `/` → Home / index (car catalog)
- `/cars` → List & search cars
- `/dashboard` → Customer dashboard (bookings & uploads)
- `/admin/users` → CRUD for users (read-only)
- `/admin/cars` → CRUD for cars
- `/admin/cars/{id}` → Car details
- `/admin/reservations` → Manage reservations

---

## Database Design
*(ER diagram or schema will be added here once finalized.)*

---

## Use-Case Diagram
*(Include UML diagram image when available.)*

---

## Mockups
### Car Selection / Reservation Page
- Search by: start date, end date, vehicle type/subtype filter.
- Display available cars matching the criteria.

### Car Details Page
- Show car information, average rating, and number of times rented.

---

## Deployment (Planned)
- AWS EC2 for hosting the application.
- Amazon RDS for database.
- Amazon S3 for storing user uploads.

---

## Project Status
Currently in development phase. Stay tuned for updates. 

