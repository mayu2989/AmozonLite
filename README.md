# 🛒 AmazonLite

> A full-stack e-commerce platform with role-based access, JWT authentication, wallet payments, and a live frontend — built with Java Spring Boot and deployed on Render.

[![Live API](https://img.shields.io/badge/API-Live%20on%20Render-brightgreen?style=for-the-badge)](https://amozonlite.onrender.com)
[![Frontend](https://img.shields.io/badge/Frontend-GitHub%20Pages-blue?style=for-the-badge)](https://mayu2989.github.io/AmozonLite/)
[![Java](https://img.shields.io/badge/Java-25-orange?style=for-the-badge&logo=java)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Neon-blue?style=for-the-badge&logo=postgresql)](https://neon.tech)

---

## 🔗 Live Links

| Resource | URL |
|---|---|
| 🌐 Frontend | https://mayu2989.github.io/AmozonLite/ |
| ⚙️ Backend API | https://amozonlite.onrender.com |
| 📋 API Testing | Use Postman with `Authorization: Bearer <token>` — endpoints are JWT protected |

> ⚠️ Hosted on Render free tier — first request may take ~30s to cold start.

---

## 📌 About

AmazonLite is a backend-focused e-commerce REST API that simulates a real marketplace. It supports three user roles — **Buyer**, **Seller**, and **Admin** — secured by JWT authentication and Spring Security. Users can list products, manage carts, place orders, top up wallets, make payments, and leave reviews.

---

## ✨ Features

### 🔐 Authentication
- Register and login as **Buyer**, **Seller**, or **Admin**
- JWT-based stateless authentication
- Role-based access control via Spring Security

### 🛍️ Seller Features
- Add, edit, and delete product listings
- Assign products to categories with stock and delivery date
- View orders placed on their products
- Update order status (PENDING → CONFIRMED → SHIPPED → DELIVERED)

### 🛒 Buyer Features
- Browse, search, and filter products by category or seller
- Add to cart, update quantities, remove items, or clear cart
- Place orders from cart or use **Buy Now** for instant purchase
- Cancel pending orders (stock is automatically restored)

### 💳 Wallet & Payments
- Each user has a wallet with a running `availableAmount`
- Top up wallet with any amount via query param
- Pay for orders directly from wallet balance
- Full payment history available

### ⭐ Reviews
- Post reviews with a rating (1–5) and comment on any product
- Per-product review listing; running average (`ratingAvg`) stored on Product
- Authors can delete their own reviews

### 🏷️ Categories
- Create, rename, and delete product categories
- Products filtered by `categoryId`

---

## 🗄️ Database Schema

![ER Diagram](er-diagram.png)
<img width="1055" height="777" alt="Screenshot From 2026-05-30 18-11-26" src="https://github.com/user-attachments/assets/1a533f63-f63e-46c8-aa3d-fcf27ee98c2f" />


**10 Tables:** `users` · `products` · `categories` · `cart` · `cart_items` · `orders` · `order_items` · `wallet` · `payment` · `review`

---

## 🧰 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 3.x |
| Security | Spring Security + JWT (jjwt 0.12.3) |
| Database | PostgreSQL (Neon) |
| ORM | Spring Data JPA / Hibernate |
| Build Tool | Maven |
| Deployment | Render (Backend), GitHub Pages (Frontend) |

---

## 📁 Project Structure

```
src/
└── main/
    └── java/
        └── com.example.amazonlite/
            ├── config/          # Security config, JWT filter, CORS
            ├── controller/      # REST controllers for each module
            ├── dto/             # Request and response DTOs
            ├── entity/          # JPA entities (10 tables)
            ├── exceptions/      # Global exception handler
            ├── repository/      # Spring Data JPA repositories
            ├── service/         # Business logic layer
            └── util/            # JWT utility, helpers
```

---

## 📡 API Reference

Base URL: `https://amozonlite.onrender.com/api/v1`

### 🔐 Auth — `/api/v1/auth`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/register` | Public | Register with `username`, `name`, `email`, `password`, `userType` (BUYER/SELLER/ADMIN). Returns JWT + user info. |
| POST | `/login` | Public | Login with `email` + `password`. Returns JWT token. Use as `Authorization: Bearer <token>`. |

### 📦 Products — `/api/v1/products`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/` | Auth | Create product (Seller only). Body: `nameOfProduct`, `categoryId`, `price`, `stock`, `description`, `deliveryDate`. |
| GET | `/` | Public | Get all products. |
| GET | `/{id}` | Public | Get product by `productId`. |
| GET | `/category/{categoryId}` | Public | Get products by category. |
| GET | `/seller/{sellerId}` | Public | Get products by seller (use seller's `userId`). |
| GET | `/search?keyword={q}` | Public | Search products by name (case-insensitive). |
| PUT | `/{id}` | Auth | Update product (owner only). Null fields ignored. |
| DELETE | `/{id}` | Auth | Delete product (owner only). |

### 🛒 Cart — `/api/v1/cart`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/add` | Auth | Add item or increase quantity. Body: `productId`, `quantity`. Validates stock. |
| GET | `/` | Auth | Get cart items. Returns `List<CartItems>` with `itemId`, `cartId`, `productId`, `quantity`. |
| PUT | `/update/{productId}?quantity=N` | Auth | Set new quantity for a cart item. Uses `productId` in path. |
| DELETE | `/remove/{productId}` | Auth | Remove a product from cart. |
| DELETE | `/clear` | Auth | Clear all items from cart. |

### 📋 Orders — `/api/v1/orders`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/place` | Auth | Place order from cart. Validates stock, creates order, clears cart. Returns `Order` with `PENDING` status. |
| POST | `/buy-now` | Auth | Instant buy (skip cart). Body: `productId`, `quantity`. |
| GET | `/my-orders` | Auth | Get all orders for current user. |
| GET | `/{orderId}` | Auth | Get order by ID (ownership enforced). |
| GET | `/{orderId}/items` | Auth | Get all items in an order. |
| PUT | `/{orderId}/cancel` | Auth | Cancel a PENDING order. Restores stock. |
| PUT | `/{orderId}/status?status=X` | Auth | Update order status (Seller/Admin). Values: `PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED`. |

### 💳 Payments — `/api/v1/payments`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/pay/{orderId}` | Auth | Pay for an order using wallet balance. |
| GET | `/order/{orderId}` | Auth | Get payment record for an order. |
| GET | `/history` | Auth | Get full payment history for current user. |

### 💰 Wallet — `/api/v1/wallet`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/create` | Auth | Create wallet (one per user). Initial `availableAmount` = 0. |
| GET | `/` | Auth | Get wallet. Key field: `availableAmount`. |
| PUT | `/add-money?amount=N` | Auth | Add funds to wallet. Amount as query param. |

### 🏷️ Categories — `/api/v1/categories`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/?categoryName=X` | Auth | Create category. Name as query param. Returns `categoryId`, `categoryName`, `createdAt`. |
| GET | `/` | Public | Get all categories. Key field: `categoryId`. |
| GET | `/{id}` | Public | Get category by `categoryId`. |
| PUT | `/{id}?categoryName=X` | Auth | Rename category. |
| DELETE | `/{id}` | Auth | Delete category. |

### ⭐ Reviews — `/api/v1/reviews`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/` | Auth | Post a review. Body: `productId`, `comment`, `rating` (1–5). Field returned: `individualProductRating`. |
| GET | `/product/{productId}` | Public | Get all reviews for a product. |
| GET | `/my-reviews` | Auth | Get all reviews by current user. |
| DELETE | `/{reviewId}` | Auth | Delete a review (author only). |

---

## 🚀 Running Locally

### Prerequisites
- Java 25
- Maven
- PostgreSQL (local or Neon cloud)

### Steps

```bash
# 1. Clone the repo
git clone https://github.com/mayu2989/AmozonLite.git
cd AmozonLite

# 2. Configure application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/amazonlite
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
jwt.secret=your_jwt_secret

# 3. Run
./mvnw spring-boot:run
```

API starts at `http://localhost:8080`

---

## ⚠️ Known Limitations / In Progress

- Seller wallet balance does not auto-credit when a buyer purchases their product
- Cart checkout (`/place`) and Buy Now both work, but edge cases around payment + order linkage are being refined

---

## 🛠️ Upcoming Features

- [ ] Seller earnings auto-credit on buyer purchase
- [ ] Product image upload via Cloudinary
- [ ] Paginated product listing
- [ ] Redis caching for product catalog (Upstash)
- [ ] Order tracking timeline UI

---

## 👨‍💻 Author

**Mayuresh Itankar** — Java Backend Developer  
[GitHub](https://github.com/mayu2989)

---

> Built as an internship-ready portfolio project demonstrating real-world backend architecture, security design, and cloud deployment.
