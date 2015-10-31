[![Build Status](https://jenkins-irishred.rhcloud.com/buildStatus/icon?job=jdbc)](https://jenkins-irishred.rhcloud.com/job/jdbc)

# lecture1_jdbc (Spring Boot and Java Config)
This lecture will show how to setup and run a Database (Apache Derby aka JavaDB  )
, populate it with data
and use a separate Java based client application to connect to it and execute SQL queries
using the JDBC API .

JDBC is a decades old API and  it's usage has changed(improved!) a lot througout the years.
We'll use code samples so you can recognise  the different types of legacy JDBC code out there

Finally we'll look at more modern streamlined usages of JDBC
and finish up with an example using Spring's JdbcTemplate.





The Java Client Application used a Maven based build file ,with some Spring boot dependencies to simplify the number of direct dependencies.

# Client Application
Each of the JDBC usage examples contains a corresponding Junit test so we can run it.

Some of the unit tests require a running database service and so will require some setup as described below.

Other tests with "Embedded" in the name are designed to create a local derby database directory in the working directory
and start an embedded derby database service.
These embedded tests are designed to self-populate the temporary database at test startup time
and so don't require a running database to be already setup.




### Class UserDao1Orig
- an old,old school JDBC implementation with embedded connection params and plaintext passwords . never use in production !!  notice all the painful boiler plate to clean up resources.

### Class UserDao2Try
- an improvement which uses the try-with-resources to auto-close Connections etc

### Class UserDao3D
- Uses an externally defined datasource so we don't have the database username,password defined in our code.

### Class UserDao4OneToOne
- loading from a single Table to a single Class is easy , but what happens when we want to map a query to multiple objects
                        
- in this case we map a HAS-A or one-to-one relationship e.g User has-a Address

### Class UserDao5JdbcCrud
- JDBC CRUD with some attempts at building a populated USER-ADDRESS-PHONE domain object graph

### Class UserDao5OneToMany
- we show how JDBC is not great at loading one-to-many relationships  e.g a User has many phones

### Class UserDao5OneToMany
- Spring JdbcTemplates eliminate low level boilerplate like Connections and prepareStaments, they also assist with checked exceptions


# Client Server Setup.
In order to test a true client-server connection we need to setup and run a database , create some tables and add some data.
The following steps will walk us through the initial installation

## Java JDK
It is assumed you have already installed the Java JDK .
You'll need at Least JDK v 7 , JDK v8+ preferred

Tha Java JDK download can be found here

      http://www.oracle.com/technetwork/java/javase/downloads/index.html

Select the "Downaload" button for JDK and download the appropriate JDK for your environment

You should set a JAVA_HOME environmental variable and add the JDK bin directory to your PATH
see the following for instruction for JDK 8

    http://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html

### To set JAVA_HOME for Windows :
- Right click My Computer and select Properties.
- On the Advanced tab, select Environment Variables,
- Under the "System variables" section  click on the "New..." button
- In the "Variable name" enter the value JAVA_HOME
- In the "Variable value" field enter the location where you installed the JDK e.g.  C:\Program Files\Java\jdk1.8.0_65


### To update  PATH for Windows :
- Right click My Computer and select Properties.
- On the Advanced tab, select Environment Variables,
- Under the "System variables" section  search for a variable with the name "Path"
- Select the "path" variable and click "Edit..."
- scroll to the end of the text in the Variable Value field , add a semi colon and then add the location of the JDK bin directory
- to make it easier to change in the future consider using the JAVA_HOME system variable e.g ;%JAVA_HOME%\bin
- Your final path will depend on what else is installed but might end with something like the following
- c:\some\other\bin;%JAVA_HOME%\bin



# Derby
We choose the Derby database because it is multi-platform .

It also has the added benefit of being embeddable within java applications.

This allows us to create, populate and run databases on the fly.

This is especially useful for unit testing as we shall see later,
since we don't have to rely on the operation of an external database service.

## Install Derby Database
Download the latest offical release of serby from the following location

    http://db.apache.org/derby/derby_downloads.html

At time of publishing v10.12.1.1 is the latest version. Later versions should be ok too.

Derby has 4 distribution variants:-
- bin distribution - contains the documentation, javadoc, and jar files for Derby.
- lib distribution - contains only the jar files for Derby.
- lib-debug distribution - contains jar files for Derby with source line numbers.
- src distribution - contains the Derby source tree at the point which the binaries were built.

Select the bin variant, as you may want to read the docs later on.
Extract the contents of the zip/gz file to your system  e.g to the following location c:\derby or /opt/derby

## Set Environmental variables for DERBY
We'll create a DERBY_HOME environmental variable to point to your installation location

And we'll also add the derby bin directory to your path for easier execution

See the following for the official guide.

    http://db.apache.org/derby/docs/10.12/getstart/index.html

So on windows you'd have a system variables like the following

    DERBY_HOME=c:\derby
    PATH=c:\some\other\stuff;%DERBY_HOME%\bin

Derby comes with a command line client.  To make the client easier to use,  add the following optional Env Variable

    DERBY_OPTS=-Dij.protocol=jdbc:derby://localhost/


## Run Derby Database
You'll need to open a a new Console window on your syste,
On windows  press WIN-R and type cmd
(in the windows console type "set" to see if your environmental variables you just added show up on linux type "env")
Change Directory to your  <DERBY_HOME>\bin   directory


To cmdStart your Derby standalone server run the following
### on unix
    ./startNetworkServer

### On Windows
    startNetworkServer.bat

If all goes well you might see something like the following

    c:\derby\bin>startNetworkServer.bat
    Fri Oct 30 15:37:18 PDT 2015 : Security manager installed using the Basic server security policy.
    Fri Oct 30 15:37:18 PDT 2015 : Apache Derby Network Server - 10.12.1.1 - (1704137) started and ready to accept connections on port 1527

Leave the console window open and running, we'll return here later.

## Verify Derby installation
cd %DERBY_HOME%\bin
sysinfo.bat

you should logged output on your java, derby and locale settings.


# Sql Squirrel
Sql Squirrel is multi-platform (Windows/Unix/mac) SQL client with
multiple databases support MySql,Derby etc  once the corresponding driver is installed)

## Install Sql Squirrel Client
At time of writing the latest download files are located here

    http://sourceforge.net/projects/squirrel-sql/files/1-stable/3.7.0/

The offical installation instructions can be found here

    http://www.squirrelsql.org/#installation

On windows tou can simply doubleclick on the downloaded jar file to  being the install wizard.

alternatively
change directory to your download location and then run the following :-

    java -jar squirrel-sql-3.7-standard.jar

Follow the instructions in the Wizard to complete installation
On windows you can accept the suggested installation of :-

    C:\Program Files\squirrel-sql-3.7

The Wizard will ask you for the optional packs or plugins you want to install.
you should enable the checkboxes for the following packs :-
- Optional Plugin - Derby

When asked, You may want to add desktop shortcuts so it's easy to start Sql Squirrel

Click on  the newly created "SQuirrel SQL Client" Desktop Icon to start the client.

## Running Sql Squirrel Client
Click on  the newly created "SQuirrel SQL Client" Desktop Icon to start the client.

or in a windows console :-

    cd  c:\Program Files\squirrel-sql-3.7
    squirrel-sql.bat

## Verify Derby Driver Plugin was installed
click on the vertical "Drivers" tab .
if you see a red (x) symbol next to the "Apache Derby Client" driver then it means that driver is not yet installed.
to install the driver, otherwise you can skip to the "create Alias" step
## Install  Derby Client Driver

- select the   "Apache Derby Client" entry and click the pencil icon to edit
- Select the “Extra Class Path” tab
- Click on “Add” and select the <DERBY_HOME>\lib\derbyClient.jar
- In the "Class Name"" dropdown select "org.apache.derby.jdbc.ClientDriver"" and then click the OK Button
- There should now be a blue tick mark in front of the Apache Derby Client driver.





## Create Alias
In Squirrel An "Alias" is simply a nickname to wrap up the connection properties used to connect to a database
- Click on the "Aliases" vertical tab
- Click on the "+" icon to create a new Connection Alias
- Fill in the "Add Alias" dialog form fields as follows :-

      Name = derby client lecture1
      User Name = app
      Password = app


- The DB URL format for derby is as follows

    jdbc:derby://localhost:1527/<DERBY_DATA_DIR>/<DATABASE_NAME>;create=true

- You'll need to use a different value depending on whether you are unix or windows based.

e.g URL on Linux :  jdbc:derby://localhost:1527//home/someuser/derbydata/lecture1;create=true
e.g Url on Windows  :   jdbc:derby://localhost:1527/c:/derbydata/lecture1;create=true

- The DB URL has two qualities that you may not be familar with , if you've used other databases
firstly the DERBY_DATA_DIR component needs to map to an accessible file location on the derby server.
secondly the  create=true parameter will create a set of database files at the DERBY_DATA_DIR location if no database files already exist there.

- Click on “Test”and then “Connect” to verify connection
- Click on “OK” to save
- Open up a console and change directories to your <DERBY_DATA_DIR>  eyou used .g c:\derbydata and you should see a bunch of files there.



## Connect To database from Sql Squirrel Client
At this point we can make a connection to Derby using

##Create Tables

##Populate Dates

##Query Data

#Client Application Setup

## Configure client application connection

## Running Unit Tests

## Building application

## Running  application

