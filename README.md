Blue Band Management System - Backend

Student: Karengera Fred
Student ID: 28372
Project: Blue Band Management System
Course: INSY 8322 Web Technology and Internet, mid-term project

This is a Spring Boot REST API with Spring Data JPA and PostgreSQL. It follows the Blue Band
Management System mid-term paper: retailers place supply orders, distributors deliver them from
a warehouse, and stock batches are recorded once an order is delivered.

Project folders

    blue-band-management-system/    the Spring Boot project (controller, service, repository, domain)
    blue-band-management-system-midterm.postman_collection.json    Postman requests named by requirement id
    seed/seed-data.sql    optional seed data with the dates of the paper

Steps to run the API

1. Install JDK 25 and PostgreSQL. If you have another JDK, change java.version in pom.xml.

2. Create the database in PostgreSQL:

    CREATE DATABASE blueband;

3. Set the DB_PASSWORD environment variable to your PostgreSQL password. For PowerShell:

   $env:DB_PASSWORD = "your-postgres-password"

4. Start the application from the project folder:

    cd blue-band-management-system
    ./mvnw spring-boot:run

   On Windows use mvnw.cmd spring-boot:run. The API runs on http://localhost:8080 and Hibernate
   creates the tables when it starts.

5. Enter the seed data of Step 0 on an empty database, in one of two ways:
   - import the Postman collection and run the folder named Step 0, once and in order
   - or run seed/seed-data.sql with psql -U postgres -d blueband -f seed/seed-data.sql
   Do not use both.

6. Run the other folders of the Postman collection in order, Part A to Part F and then Bonus.
   Each request is named by its requirement id (A2, B1, C4, D2 ...) and the failing cases are
   named like "D2 error 400 ...".

Endpoints

CRUD for product-lines, warehouses, distributors, retailers and supply-orders:
POST /api/{path}/save, GET /api/{path}/all, GET /api/{path}/{id},
PUT /api/{path}/update/{id}, DELETE /api/{path}/delete/{id}

B1  GET /api/retailers/by-owner-name?ownerName=karengera
B2  GET /api/supply-orders/by-status?status=PLACED
B3  GET /api/supply-orders/between?start=2026-10-01&end=2026-10-31
C1  GET /api/distributors/by-product-line?name=original
C2  GET /api/warehouses/free
C3  GET /api/product-lines/unused
C4  GET /api/retailers/of-distributor/{distributorId}
D1  POST /api/supply-orders/save
D2  PATCH /api/supply-orders/{id}/status?status=DISPATCHED
D3  PUT /api/distributors/{distributorId}/product-lines/{productLineId}
D4  PATCH /api/distributors/{distributorId}/warehouse?warehouseNumber=4
E1  POST /api/stock-batches/save?supplyOrderId=...
E2  GET /api/stock-batches/retailer/{shopCode}
E3  DELETE /api/stock-batches/delete/{id}
F1  GET /api/supply-orders/stats/by-status
F2  PATCH /api/supply-orders/cancel-day?distributorId=...&date=2026-10-20
Bonus  GET /api/dashboard/summary and GET /api/supply-orders/page?page=0&size=5&sort=orderDate,desc
