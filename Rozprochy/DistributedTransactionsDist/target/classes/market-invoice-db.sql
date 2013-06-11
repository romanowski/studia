CREATE TABLE invoice(
  id SMALLINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  customer_first_name VARCHAR(255) NOT NULL,
  customer_last_name VARCHAR(255) NOT NULL,
  customer_address VARCHAR(255) NOT NULL,
  total_price SMALLINT NOT NULL
);
