CREATE SCHEMA IF NOT EXISTS users_services_scheme;

CREATE TABLE IF NOT EXISTS users_services_scheme.user_my_chat(
                                                   id SERIAL PRIMARY KEY,
                                                   name TEXT NOT NULL,
                                                   surname TEXT NOT NULL,
                                                   email TEXT NOT NULL,
                                                   password TEXT NOT NULL,
                                                   id_role SERIAL NOT NULL,
                                                   CONSTRAINT FK_role FOREIGN KEY(id_role)
                                                   REFERENCES users_services_scheme.role_user_my_chat(id)
);

CREATE TABLE IF NOT EXISTS users_services_scheme.role_user_my_chat(
                                                                 id SERIAL PRIMARY KEY,
                                                                 description TEXT NOT NULL
);



