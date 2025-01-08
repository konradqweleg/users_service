-- CREATE SCHEMA IF NOT EXISTS users_services_scheme;

CREATE TABLE IF NOT EXISTS user_my_chat(
                                                   id SERIAL PRIMARY KEY,
                                                   name TEXT NOT NULL,
                                                   surname TEXT NOT NULL,
                                                   email TEXT NOT NULL
);


CREATE TABLE IF NOT EXISTS code_verification(
                                                                      id SERIAL PRIMARY KEY,
                                                                      id_user SERIAL NOT NULL,
                                                                      code TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS reset_password_code(
                                                                      id SERIAL PRIMARY KEY,
                                                                      id_user SERIAL NOT NULL,
                                                                      code TEXT NOT NULL
);
