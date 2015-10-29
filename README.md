[![Build Status](https://jenkins-irishred.rhcloud.com/buildStatus/icon?job=jdbc)](https://jenkins-irishred.rhcloud.com/job/jdbc)

# lecture1_jdbc (Spring Boot and Java Config)
JDBC is a decades old api and there it's usage has changed a lot througout the years.
The purpose of this project is to illustrate some of the different types of JDBC usage you may come across in legacy code from older examples with a lot of boilerplate
 to newer more streamlined examples.
Finally we show an example using  Spring's JdbcTemplate.

The build file is Maven using Spring boot dependencies.

Each of the JDBC usage examples contains a corresponding unit test so we can run it

Some of the tests require a running database service and so will require some setup as described below.

Other tests with "Embedded" in the name are designed to create a local database directory in the working directory and start an embedded database service.
These embedded tests are designed to self-populate this tempoary datebase at test time and so don't require any other setup work.




###Class UserDao1Orig
- an old,old school JDBC implementation with embedded connection params and plaintext passwords . never use in production !!  notice all the painful boiler plate to clean up resources.

###Class UserDao2Try 
- an improvement which uses the try-with-resources to auto-close Connections etc

###Class UserDao3D
- Uses an externally defined datasource so we don't have the database username,password defined in our code.

###Class UserDao4OneToOne  
- loading from a single Table to a single Class is easy , but what happens when we want to map a query to multiple objects
                        
- in this case we map a HAS-A or one-to-one relationship e.g User has-a Address

###Class UserDao5JdbcCrud 
- JDBC CRUD with some attempts at building a populated USER-ADDRESS-PHONE domain object graph

###Class UserDao5OneToMany 
- we show how JDBC is not great at loading one-to-many relationships  e.g a User has many phones

###Class UserDao5OneToMany
- Spring JdbcTemplates eliminate low level boilerplate like Connections and prepareStaments, they also assist with checked exceptions


# Client Server Setup.
In order to test a true client-server connection we need to setup and run a database , create some tables and add some data.
The following steps will walk us through the initial installation

# Derby
##Install Derby Database

##Run Derby Database

##Verify Derby Database is running


#Sql Squirrel
##Install Sql Squirrel Client

##Connect To database from Sql Squirrel Client

##Create Tables

##Populate Dates

##Query Data

#Client Application Setup

## Configure client application connection

## Running Unit Tests

## Building application

## Running  application

