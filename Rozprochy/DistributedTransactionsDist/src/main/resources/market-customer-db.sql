CREATE TABLE customer(
  id SMALLINT PRIMARY KEY,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  address VARCHAR(255) NOT NULL,
  balance BIGINT NOT NULL
);

-- This table could be potentially used for remembering orders which were already processed
CREATE TABLE orders (
  id VARCHAR(255) PRIMARY KEY
);

