1- add mssql-jdbc_auth-12.10.2.x64.dll to "bin" file of jdk

2- health => http://localhost:8080/api/health
3- swagger => http://localhost:8080/swagger-ui.html

4- account to login => 'user : admin' & 'password : admin' => or you can insert new user using Register service
   you can insert first admin user using below scripts (you can use https://bcrypt-generator.com/ to generate password):

   INSERT INTO users (username, email, password, first_name, last_name,enabled,deleted)
   VALUES ('admin', 'admin@aviation.com', '$2a$12$erhN573214mfCcxTzrsYw.JN4X4e2RDoRcvZz3UwUufMftvY6eNDm', 'System', 'Administrator',1,0);

   INSERT INTO user_roles (user_id, role) VALUES (3, 'ADMIN');
   INSERT INTO user_roles (user_id, role) VALUES (3, 'TECHNICIAN');
   INSERT INTO user_roles (user_id, role) VALUES (3, 'INSPECTOR');