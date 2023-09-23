CREATE SCHEMA IF NOT EXISTS users_services_scheme;

CREATE TABLE IF NOT EXISTS users_services_scheme.user_my_chat(
                                                   id SERIAL PRIMARY KEY,
                                                   name TEXT NOT NULL,
                                                   surname TEXT NOT NULL,
                                                   email TEXT NOT NULL,
                                                   password TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS users_services_scheme.role_user_my_chat(
                                                                 id SERIAL PRIMARY KEY,
                                                                 description TEXT NOT NULL
);



