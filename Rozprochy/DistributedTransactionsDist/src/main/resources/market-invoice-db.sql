CREATE TABLE invoice(
  id SMALLINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  customer_first_name VARCHAR(255) NOT NULL,
  customer_last_name VARCHAR(255) NOT NULL,
  customer_address VARCHAR(255) NOT NULL,
  total_price SMALLINT NOT NULL
);

-- This table could be potentially used for remembering orders which were already processed
CREATE TABLE orders (
  id VARCHAR(255) PRIMARY KEY
);