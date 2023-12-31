DROP TABLE IF EXISTS users, categories, locations, events, participation_requests, compilations, compilations_events, friend_requests;

CREATE TABLE IF NOT EXISTS users ( 
	id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 
	name varchar(250) NOT NULL,
	email varchar(254) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS categories ( 
	id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 
	name varchar(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS locations ( 
	id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 
	lat real NOT NULL,
	lon real NOT NULL
);

CREATE TABLE IF NOT EXISTS events (
	id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 
	annotation varchar(2000) NOT NULL,
	category_id BIGINT NOT NULL REFERENCES categories (id),
	description varchar(7000) NOT NULL,
	event_date TIMESTAMP NOT NULL,
	location_id BIGINT NOT NULL REFERENCES locations (id),
	paid Boolean NOT NULL,
	participant_limit int NOT NULL,
	request_moderation Boolean NOT NULL,
	title varchar(120) NOT NULL,
	created_on TIMESTAMP NOT NULL,
	initiator_id BIGINT NOT NULL REFERENCES users (id),
	state varchar(9) NOT NULL,
	published_on TIMESTAMP
);

CREATE TABLE IF NOT EXISTS participation_requests (
	id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 
	created TIMESTAMP NOT NULL,
	event_id BIGINT NOT NULL REFERENCES events (id),
	requester_id BIGINT NOT NULL REFERENCES users (id),
	status varchar(9) NOT NULL,
	CONSTRAINT uq_request UNIQUE(event_id, requester_id)
);

CREATE TABLE IF NOT EXISTS compilations (
	id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 
	pinned Boolean NOT NULL,
    title varchar(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS compilations_events (
	compilation_id BIGINT REFERENCES compilations (id),
	event_id BIGINT REFERENCES events (id),
	PRIMARY KEY (compilation_id, event_id)
);

CREATE TABLE IF NOT EXISTS friend_requests (
	id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	requester_id BIGINT NOT NULL REFERENCES users (id),
    friend_id BIGINT NOT NULL REFERENCES users (id),
    status varchar(9) NOT NULL,
    created TIMESTAMP NOT NULL,
    CONSTRAINT uq_friendship UNIQUE(requester_id, friend_id)
)



