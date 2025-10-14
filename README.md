# CarGo - Car Rental Application

**Authors:**
- Kirththiga Murugupillai
- Aleksandro Echavarria-Mercier

---

## Project Description
CarGo is a Spring Boot web application that allows customers to browse, reserve, and pay for rental cars online.

- Customers can register or log in, view vehicles by category (Economy, SUV, Luxury, etc.), and make reservations.
- Reservations are validated against car availability to prevent overlaps.
- A secure Stripe payment system enables credit card payments for confirmed bookings.
- Role-based access with Spring Security ensures data protection:
    - CUSTOMER → browse cars, make reservations, and complete payments
    - ADMIN → manage vehicles, users, and reservations via CRUD operations
- File uploads (e.g. driver’s license images) are supported.
- Styled with Bootstrap 5 for a modern, responsive interface.
- Deployed on Heroku with an integrated MySQL database (AWS RDS).

---

## Live Demo
**Heroku Deployment:**  
[CarGo Application](https://cargo-prod-b8d4c89a3453.herokuapp.com/)  
*(Replace with your live Heroku URL if different.)*

---

## Key Features
- User registration, authentication, and role-based authorization
- Search vehicles by category or availability
- Prevents double-booking with overlap validation
- Secure Stripe payments (PaymentIntent workflow)
- Reservation and payment confirmation screens
- File upload for driver license verification
- Admin dashboard for managing users, cars, and reservations
- Real-time feedback (success/error alerts)

---

## Technologies Used
| Category | Tools / Frameworks                          |
|-----------|---------------------------------------------|
| Backend | Java 17, Spring Boot 3.x, Spring MVC        |
| Frontend | Thymeleaf, Bootstrap 5                      |
| Security | Spring Security (role-based authentication) |
| Database | MySQL (AWS RDS)                             |
| Payments | Stripe API (PaymentIntent + Webhooks)       |
| Deployment | Heroku (via Git + Maven)                    |
| Build Tool | Maven                                       |

---

## Architecture Overview
CarGo follows a Model–View–Controller (MVC) architecture:

- **Model** – JPA entities (User, Vehicle, Reservation, etc.)
- **View** – Thymeleaf templates styled with Bootstrap
- **Controller** – Spring MVC controllers handling business logic and data flow
- **Service Layer** – Encapsulates reservation validation, Stripe integration, and business rules

---

## Integrations
- **Stripe API** – Handles secure payment processing (PaymentIntent, confirmation, and webhooks).
- **Heroku + RDS** – Cloud-hosted deployment with MySQL backend.

---

## Challenges and Learnings
- Implementing reliable date-overlap detection for car reservations.
- Handling Stripe payment flows with asynchronous webhooks.
- Ensuring secure routes and CSRF protection for sensitive endpoints.
- Designing a visually clean and user-friendly interface.

---

## Application Routes

### General and Public
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/` | Home page (vehicle catalog) |
| GET | `/contact` | Contact information page |

---

### Authentication
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/login` | Display login form |
| POST | `/login` | Process login |
| GET | `/register` | Show registration form |
| POST | `/register` | Register new user |

---

### Admin Panel
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/admin/dashboard` | Admin dashboard |
| GET | `/admin/users` | View all users |
| POST | `/admin/users/{id}` | Promote user to Admin |
| POST | `/admin/demote/{id}` | Demote Admin to User |

---

### Vehicle Management
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/admin/vehicles` | View all vehicles |
| GET | `/admin/vehicle/add` | Show form to add a new vehicle |
| POST | `/admin/vehicle/add` | Save a new vehicle |
| GET | `/admin/vehicle/edit/{id}` | Edit a vehicle |
| POST | `/admin/vehicle/edit/{id}` | Update vehicle |
| POST | `/admin/vehicle/delete/{id}` | Delete vehicle |
| GET | `/vehicles` | Browse vehicles (public) |
| GET | `/vehicles/available` | Search for available vehicles |

---

### Category Management
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/admin/categories` | View all categories |
| GET | `/admin/category/add` | Show add category form |
| POST | `/admin/category/add` | Add category |
| GET | `/admin/category/edit/{id}` | Edit category |
| POST | `/admin/category/edit/{id}` | Update category |
| POST | `/admin/category/delete/{id}` | Delete category |

---

### Reservation Management
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/admin/reservations` | List all reservations |
| GET | `/admin/reservations/add` | Add reservation form |
| POST | `/admin/reservations/add` | Create reservation |
| GET | `/admin/reservations/edit/{id}` | Edit reservation form |
| POST | `/admin/reservations/edit/{id}` | Update reservation |
| POST | `/admin/reservations/delete/{id}` | Delete reservation |
| GET | `/user/reservations` | Show logged-in user's reservations |
| POST | `/user/reservations/delete/{id}` | Delete user's reservation |
| POST | `/reservations/quick-book` | Quick booking (user flow) |

---

### Payment (Stripe Integration)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/checkout/{reservationId}` | Checkout page for a reservation |
| POST | `/api/payments/create-intent` | Create Stripe PaymentIntent |
| GET | `/payment/confirm` | Payment confirmation screen |
| POST | `/stripe/webhook` | Stripe webhook handler for payment updates |

---

### Reservation Hold API
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/reservations/{id}/hold` | Check if reservation hold is still valid |

---

## Deployment
CarGo is deployed on Heroku using Git-based CI/CD.

- **App Server:** Heroku Dyno running Spring Boot
- **Database:** AWS (RDS) MySQL
- **Environment Variables:** Managed via Heroku Config Vars
- **Stripe Webhooks:** Configured for `/stripe/webhook` endpoint

### Example Deployment Steps
```bash
# Build project
mvn clean package

# Login to Heroku
heroku login

# Deploy
git push heroku main
```

## Project Status
The project is **currently under active development** — core features are functional, with payment and cloud integration coming next.

---

## Contributors
- [@kirththiga](https://github.com/kirththiga)
- [@aleksandro](https://github.com/aleksandro)

---

## 🪪 License
This project is intended for **academic and educational purposes**.  
© 2025 Kirththiga Murugupillai & Aleksandro Echavarria-Mercier. All rights reserved.