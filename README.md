# 📚"Online Bookstore API" !

### 👓Project description

*The bookstore application is designed to organize the sale and purchase of books on the Internet. The store allows you
to create a request to display books based on a selection of titles, authors, codes, and price range. The buyer can
create a personal account with the ability to place orders and view order history. The seller can place an offer with
books indicating detailed information about the book with a description.*

---

### 🖥️Technologies and tools used

- Java Version: 17
- Maven Version: 4.0.0
- Spring Boot Framework Version: 3.1.2
- Spring Data JPA
- Spring MVC
- Security
    - Spring Security
    - JSON Web Tokens (JWT)
- MySQL database
- Liquibase Migration Version: 4.23.1
- Testing
    - JUnit Version: Jupiter
    - Testcontainers Version: 1.18.3
    - Mockito
- JSON
- Code Generation and Mapping
    - Lombok
    - MapStruct Version: 1.5.3.Final
- Swagger Version: 2.1.0
- REST

---

### 🧭Describing functionalities of controllers

#### **1. Authentication Controller**

- **Description:** Provides endpoints for user authentication and registration.
    - **Login Endpoint:**  *POST*`/api/auth/login`
        - requests for user and admin
        - ````
          {
             "email": "user1234@mail.com",
             "password": "1234" 
          }
      - ````
          {
             "email": "admin1234@mail.com",
             "password": "1234" 
          }
    - **Register Endpoint:** *POST*`/api/auth/register`
        - ````
          {
             "email": "artem1234@email.com",
             "password": "1234",
             "repeatPassword": "1234",
             "firstName": "Artem",
             "lastName": "User",
             "shippingAddress": "right here"
          }

- **Functionality:**
    - Allows users to log in by providing their email address and password.
        - Enables users to register by providing their email, password, first name, last name, and shipping address.

#### **2. Book Management Controller**

- **Description:** Manages book-related operations.
- **Endpoints:**
    - **Create Book:** *POST*`/api/books` (Accessible to ROLE_ADMIN only)
      - ````
        {
        "title": "title of book",
        "author": "author book",
        "isbn": "book code 123456",
        "price": 21.99,
        "description": "This is a sample book description.",
        "coverImage": "http://example.com/cover1.jpg",
        "categories": [1,3]
        }
    - **Get All Books:** *GET*`/api/books` (Accessible to ROLE_USER)
    - **Get Book by ID:** *GET*`/api/books/{id}` (Accessible to ROLE_USER)
    - **Update Book:** *PUT*`/api/books/{id}` (Accessible to ROLE_ADMIN)
      - ````
        {
        "title": "new title of book",
        "author": "author book",
        "isbn": "book code 123456",
        "price": 21.99,
        "description": "This is a sample book description.",
        "coverImage": "http://example.com/cover1.jpg",
        "categories": [1,3]
        }
    - **Delete Book:** *DELETE*`/api/books/{id}` (Accessible to ROLE_ADMIN)
    - **Search Books:** *GET*`/api/books/search?price=10,20` (Accessible to ROLE_USER)
- **Functionality:**
    - Allows the creation, retrieval, updating, and deletion of books.
    - Supports searching books based on various parameters.

#### **3. Category Management Controller**

- **Description:** Manages book categories.
- **Endpoints:**
    - **Create Category:** *POST*`/api/categories` (Accessible to ROLE_ADMIN only)
      - ````
        {
        "name":"name",
        "description":"description"
        }
    - **Get All Categories:** *GET*`/api/categories` (Accessible to ROLE_USER)
    - **Get Category by ID:** *GET*`/api/categories/{id}` (Accessible to ROLE_USER)
    - **Update Category:** *PUT*`/api/categories/{id}` (Accessible to ROLE_ADMIN)
      - ````
        {
        "name":"updeting name",
        "description":"updeting description"
        }
    - **Delete Category:** *DELETE*`/api/categories/{id}` (Accessible to ROLE_ADMIN)
    - **Get Books by Category ID:** *GET*`/api/categories/{categoryId}/books` (Accessible to ROLE_USER)
- **Functionality:**
    - Supports creating, retrieving, updating, and deleting book categories.
    - Allows retrieving books belonging to a specific category.

#### **4. Order Management Controller**

- **Description:** Manages user orders and order items.
- **Endpoints:**
    - **Place Order:** *POST*`/api/orders` (Accessible to ROLE_USER)
      - ````
        {
        "shippingAddress": "Kyiv, Shevchenko ave, 1"
        }
    - **View Order History:** *GET*`/api/orders` (Accessible to ROLE_USER)
    - **Get Order Items:** *GET* `/api/orders/{orderId}/items` (Accessible to ROLE_USER)
    - **Get Specific Order Item:** *GET*`/api/orders/{orderId}/items/{itemId}` (Accessible to ROLE_USER)
    - **Update Order Status:** *PATCH*`/api/orders/{orderId}` (Accessible to ROLE_ADMIN)
      - ````
        {
        "status": "DELIVERED"
        }
- **Functionality:**
    - Allows users to place orders, view their order history, and retrieve order items.
    - Supports updating the status of orders by administrators.

#### **5. Shopping Cart Controller**

- **Description:** Manages user shopping carts.
- **Endpoints:**
    - **Get Shopping Cart:** *GET*`/api/cart` (Accessible to ROLE_USER)
    - **Add Book to Cart:** *POST*`/api/cart` (Accessible to ROLE_USER)
    - ````
      {
       "quantity":120
       "quantity":50
      }

    - **Update Book Quantity in Cart:** *PUT*`/api/cart/cart-items/{cartItemId}` (Accessible to ROLE_USER)
  - ````
      {
       "quantity":55
      }
- **Remove Book from Cart:** *DEL*`/api/cart/cart-items/{cartItemId}` (Accessible to ROLE_USER)
- **Functionality:**
    - Allows users to retrieve their shopping carts, add books to the cart, update book quantities, and remove books
      from the cart.

---

### ▶️How to set up and start the project

- **Soft requirements**
    - Java Development Kit (JDK) version 11 or higher.
    - Maven Version: 4.0.0
    - Git
    - MySQL
    - Docker
    - PostMan
- **Instalation**
    - Clone the repository from github:
  ```shell
  git clone git@github.com:https://github.com/artemmakovskyy/onlineBookStore
   ```
    - Start the Docker
    - Configure the database parameters in the .env file
    - Open a terminal and navigate to the root directory of your project
    - Into the terminal use command to build the container and start project
  ```shell
    docker-compose up
   ```
    - First way to use the BookStoreApi it is SWAGGER

   ```shell
     http://localhost:8088/api/swagger-ui/index.html#/
   ```
    - Second way to use the BookStoreApi it is PostMan
---
### 🎞️ A short video tutorial on how to use the bookstore app
▷ https://www.loom.com/share/c710183cf6674e00b12708721fb76cc3
