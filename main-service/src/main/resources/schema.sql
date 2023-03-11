DROP TABLE IF EXISTS users, event_categories, locations, events, compilations, compilation_event,
    participation_requests CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(50),
    email VARCHAR(200)                            NOT NULL unique,
    CONSTRAINT pk_user PRIMARY KEY (id)

);

CREATE TABLE IF NOT EXISTS event_categories
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(64) UNIQUE,
    CONSTRAINT pk_event_category PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS locations
(
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    lat DOUBLE PRECISION                        NOT NULL,
    lon DOUBLE PRECISION                        NOT NULL,
    CONSTRAINT pk_location PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS events
(
    id                   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title                VARCHAR(100),
    annotation           VARCHAR(500)                            NOT NULL,
    description          VARCHAR(1024)                           NOT NULL,
    category_id          BIGINT REFERENCES event_categories (id) ON DELETE CASCADE,
    created_date_time    TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    start_date_time      TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    owner_id             BIGINT REFERENCES USERS (id) ON DELETE CASCADE,
    is_paid              BOOLEAN                                 NOT NULL,
    participant_limit    INTEGER,
    published_date_time  TIMESTAMP WITHOUT TIME ZONE,
    is_needed_moderation BOOLEAN                                 NOT NULL,
    state                VARCHAR(16)                             NOT NULL,
    location_id          BIGINT REFERENCES locations (id),
    CONSTRAINT pk_event PRIMARY KEY (id)
);



CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title  VARCHAR(100),
    pinned BOOLEAN,
    CONSTRAINT pk_compilation PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS compilation_event
(
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    compilation_id BIGINT REFERENCES compilations (id) ON DELETE CASCADE,
    event_id       BIGINT REFERENCES events (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS participation_requests
(
    id                BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_date_time TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    event_id          BIGINT REFERENCES events (id) ON DELETE CASCADE,
    requester_id      BIGINT REFERENCES users (id) ON DELETE CASCADE,
    status            VARCHAR(16)                             NOT NULL,
    CONSTRAINT pk_participation_request PRIMARY KEY (id)
);


