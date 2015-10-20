# lecture1_jdbc (Spring Boot and Java Config)
A collection of various old JDBC based DAOs and a newer spring-jdbc-template based implementation
The build file is Maven using Spring boot dependencies

Class UserDao1Orig - an old,old school JDBC implementation with embedded connection params and plaintext passwords . never use in production !!
                      * notice all the painful boiler plate to clean up resources.

Class UserDao2Try - an improvement which uses the try-with-resources to auto-close Connections etc

Class UserDao3DS  - Uses an externally defined datasource so we don't have the database username,password defined in our code.

Class UserDao4OneToOne  - loading from a single Table to a single Class is easy , but what happens when we want to map a query to multiple objects
                        - in this case we map a HAS-A or one-to-one relationship e.g User has-a Address

Class UserDao5JdbcCrud  - JDBC CRUD with some attempts at building a populated USER-ADDRESS-PHONE domain object graph

Class UserDao5OneToMany  - we show how JDBC is not great at loading one-to-many relationships  e.g a User has many phones

Class UserDao5OneToMany - Spring JdbcTemplates eliminate low level boilerplate like Connections and prepareStaments, they also assist with checked exceptions


