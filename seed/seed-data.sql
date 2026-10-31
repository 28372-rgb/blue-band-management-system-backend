-- Seed data for the Blue Band Management System (Karengera Fred, 28372).
-- Run once on an empty database after the application has created the tables:
--     psql -U postgres -d blueband -f seed/seed-data.sql
-- Use this file or the Postman folder Step 0, not both.
-- The orders are inserted directly because the save endpoint refuses past dates
-- and always starts an order as PLACED.

BEGIN;

-- Product lines
INSERT INTO product_line (id, name) VALUES ('f98e8085-d01f-5da8-bb09-ae87dd643710', 'Blue Band Original');
INSERT INTO product_line (id, name) VALUES ('23885fa1-6e22-56fd-8a85-23b3b4755b74', 'Blue Band Low Fat');
INSERT INTO product_line (id, name) VALUES ('3c5a4c63-7d56-5a77-9afb-256bedaf3153', 'Blue Band Spread');
INSERT INTO product_line (id, name) VALUES ('f18a7357-e49e-5f4a-806d-88213d9955ff', 'Blue Band Mini');

-- Warehouses (number 4 has no distributor)
INSERT INTO warehouse (id, warehouse_number, name) VALUES ('3b62b137-7287-5c97-9c0e-b0d6b7d4fab5', 1, 'Kigali Central');
INSERT INTO warehouse (id, warehouse_number, name) VALUES ('073eaced-d9f0-5757-8219-d3f790832b5d', 2, 'Nyabugogo');
INSERT INTO warehouse (id, warehouse_number, name) VALUES ('e29fa8da-01e9-5c9b-9fc3-373267116d5f', 3, 'Huye');
INSERT INTO warehouse (id, warehouse_number, name) VALUES ('320371a7-5d73-538d-8dd7-fa43f707111f', 4, 'Musanze');

-- Distributors (each has a warehouse)
INSERT INTO distributor (id, company_name, contact_name, phone, warehouse_id) VALUES ('b1f57c06-06c4-5bc2-b2a9-fbf9aea5fb12', 'Inyange Distribution Ltd', 'Uwase Claudine', '0788111111', '3b62b137-7287-5c97-9c0e-b0d6b7d4fab5');
INSERT INTO distributor (id, company_name, contact_name, phone, warehouse_id) VALUES ('55a7e16c-6341-55b3-90fe-0ff594d24fae', 'Kivu Foods Supply', 'Habimana Joseph', '0788222222', '073eaced-d9f0-5757-8219-d3f790832b5d');
INSERT INTO distributor (id, company_name, contact_name, phone, warehouse_id) VALUES ('c2719173-f73f-5101-8896-cf50343e2d6a', 'Umubano Traders', 'Mugisha Patrick', '0788333333', 'e29fa8da-01e9-5c9b-9fc3-373267116d5f');

-- Product lines held by each distributor (Inyange holds two, the others one, Mini nobody)
INSERT INTO distributor_product_line (distributor_id, product_line_id) VALUES ('b1f57c06-06c4-5bc2-b2a9-fbf9aea5fb12', 'f98e8085-d01f-5da8-bb09-ae87dd643710');
INSERT INTO distributor_product_line (distributor_id, product_line_id) VALUES ('b1f57c06-06c4-5bc2-b2a9-fbf9aea5fb12', '23885fa1-6e22-56fd-8a85-23b3b4755b74');
INSERT INTO distributor_product_line (distributor_id, product_line_id) VALUES ('55a7e16c-6341-55b3-90fe-0ff594d24fae', '3c5a4c63-7d56-5a77-9afb-256bedaf3153');
INSERT INTO distributor_product_line (distributor_id, product_line_id) VALUES ('c2719173-f73f-5101-8896-cf50343e2d6a', 'f98e8085-d01f-5da8-bb09-ae87dd643710');

-- Retailers
INSERT INTO retailer (id, shop_name, owner_name, shop_code, phone) VALUES ('f45cc207-9071-5049-b23d-22557c3a1a48', 'Karengera Mini Market', 'Karengera', 'RT-0097', '0788000097');
INSERT INTO retailer (id, shop_name, owner_name, shop_code, phone) VALUES ('5a6c599e-5a5b-5ef0-8849-18baa28421a5', 'Remera Corner Shop', 'Karengera', 'RT-0112', '0788000112');
INSERT INTO retailer (id, shop_name, owner_name, shop_code, phone) VALUES ('fbdde626-3937-53f3-bec6-cd590b7e644d', 'Nyamirambo Store', 'Mukamana Jeanne', 'RT-0120', '0788000120');
INSERT INTO retailer (id, shop_name, owner_name, shop_code, phone) VALUES ('6141f47d-f25d-585f-9e04-6b1863925b0f', 'Kimironko General', 'Habimana Eric', 'RT-0134', '0788000134');
INSERT INTO retailer (id, shop_name, owner_name, shop_code, phone) VALUES ('2f0c2786-cbe9-5f6d-a290-378003d732d3', 'Gisozi Supplies', 'Uwimana Alice', 'RT-0141', '0788000141');

-- Supply orders
INSERT INTO supply_order (id, order_date, status, cartons, retailer_id, distributor_id) VALUES ('2c667096-9e23-5571-bd39-2ee190432e91', '2026-10-05', 'DELIVERED', 20, 'f45cc207-9071-5049-b23d-22557c3a1a48', 'b1f57c06-06c4-5bc2-b2a9-fbf9aea5fb12');
INSERT INTO supply_order (id, order_date, status, cartons, retailer_id, distributor_id) VALUES ('e585243c-c46e-5236-9863-093a70ecbfd2', '2026-10-09', 'DELIVERED', 15, 'f45cc207-9071-5049-b23d-22557c3a1a48', '55a7e16c-6341-55b3-90fe-0ff594d24fae');
INSERT INTO supply_order (id, order_date, status, cartons, retailer_id, distributor_id) VALUES ('0de84e7d-eb19-58a8-9f9c-ff0a630e00e5', '2026-10-14', 'DISPATCHED', 30, 'f45cc207-9071-5049-b23d-22557c3a1a48', 'c2719173-f73f-5101-8896-cf50343e2d6a');
INSERT INTO supply_order (id, order_date, status, cartons, retailer_id, distributor_id) VALUES ('1ddf1118-5b1b-5c1e-8fdd-1d9b9c3b4cac', '2026-10-20', 'CANCELLED', 12, '5a6c599e-5a5b-5ef0-8849-18baa28421a5', 'b1f57c06-06c4-5bc2-b2a9-fbf9aea5fb12');
INSERT INTO supply_order (id, order_date, status, cartons, retailer_id, distributor_id) VALUES ('1c2255df-81b5-5e0b-b2f0-7984217dd553', '2026-10-20', 'PLACED', 25, 'fbdde626-3937-53f3-bec6-cd590b7e644d', 'b1f57c06-06c4-5bc2-b2a9-fbf9aea5fb12');
INSERT INTO supply_order (id, order_date, status, cartons, retailer_id, distributor_id) VALUES ('4adb0722-ce4d-5ddb-8974-bd2af713f883', '2026-10-26', 'DELIVERED', 40, '6141f47d-f25d-585f-9e04-6b1863925b0f', '55a7e16c-6341-55b3-90fe-0ff594d24fae');
INSERT INTO supply_order (id, order_date, status, cartons, retailer_id, distributor_id) VALUES ('e271215a-d51f-59dc-809d-2471ab0db497', '2026-11-03', 'PLACED', 10, '2f0c2786-cbe9-5f6d-a290-378003d732d3', '55a7e16c-6341-55b3-90fe-0ff594d24fae');
INSERT INTO supply_order (id, order_date, status, cartons, retailer_id, distributor_id) VALUES ('05b3d2ff-15f6-5a33-bd4c-81ca6fac651e', '2026-11-10', 'DISPATCHED', 18, '5a6c599e-5a5b-5ef0-8849-18baa28421a5', 'c2719173-f73f-5101-8896-cf50343e2d6a');
INSERT INTO supply_order (id, order_date, status, cartons, retailer_id, distributor_id) VALUES ('f8353900-3c56-55aa-b40c-c8c61e42d8e8', '2026-11-17', 'PLACED', 22, 'fbdde626-3937-53f3-bec6-cd590b7e644d', 'c2719173-f73f-5101-8896-cf50343e2d6a');
INSERT INTO supply_order (id, order_date, status, cartons, retailer_id, distributor_id) VALUES ('849ff626-303e-59e2-a118-7a44cc8a9dc4', '2026-11-24', 'PLACED', 16, '6141f47d-f25d-585f-9e04-6b1863925b0f', 'b1f57c06-06c4-5bc2-b2a9-fbf9aea5fb12');

-- Stock batches (only for DELIVERED supply orders)
INSERT INTO stock_batch (id, batch_code, remarks, cartons, received_date, supply_order_id) VALUES ('92b5e13c-c010-5770-9e80-af643545f5b7', 'BB-2026-101', 'Expires in 8 months', 12, '2026-10-06', '2c667096-9e23-5571-bd39-2ee190432e91');
INSERT INTO stock_batch (id, batch_code, remarks, cartons, received_date, supply_order_id) VALUES ('b6f0e595-c6ef-531f-ad6e-b3e39542e5ca', 'BB-2026-102', 'Second pallet', 8, '2026-10-06', '2c667096-9e23-5571-bd39-2ee190432e91');
INSERT INTO stock_batch (id, batch_code, remarks, cartons, received_date, supply_order_id) VALUES ('90661789-9529-5e05-9302-beea169e0c4e', 'BB-2026-103', 'Checked on arrival', 15, '2026-10-10', 'e585243c-c46e-5236-9863-093a70ecbfd2');
INSERT INTO stock_batch (id, batch_code, remarks, cartons, received_date, supply_order_id) VALUES ('88ffa2d6-d8b7-5cc8-8cb3-1b92d4b8a35d', 'BB-2026-104', 'Fresh stock', 40, '2026-10-27', '4adb0722-ce4d-5ddb-8974-bd2af713f883');

COMMIT;
