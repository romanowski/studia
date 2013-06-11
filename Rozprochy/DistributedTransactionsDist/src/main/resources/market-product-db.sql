CREATE TABLE product(
  id SMALLINT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  price SMALLINT NOT NULL,
  amount BIGINT NOT NULL
);

-- This table is used for logging information about customers.
-- The customer's address and the total price of customer's order is remembered.
-- It could be used in the future to identify where are the richest customer are located
-- which could give a clue about good locations for future warehouse departments.
CREATE TABLE product_audit (
  address VARCHAR(255) NOT NULL,
  total_price SMALLINT NOT NULL
);

-- This table could be potentially used for remembering orders which were already processed
CREATE TABLE orders (
  id VARCHAR(255) PRIMARY KEY
);