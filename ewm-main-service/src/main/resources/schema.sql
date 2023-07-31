DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users ( 
	id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 
	name varchar(250) NOT NULL,
	email varchar(254) NOT NULL UNIQUE
)
