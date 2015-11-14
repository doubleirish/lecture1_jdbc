[![Build Status](https://jenkins-irishred.rhcloud.com/buildStatus/icon?job=jdbc)](https://jenkins-irishred.rhcloud.com/job/jdbc)

# lecture1_jdbc (java Config)
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
- Spring JdbcTemplates eliminate low level boilerplate like Connections and prepareStaments,
    they also assist with checked exceptions. Classes like the ResultSetExtractor help us centralize
    and reuse the mapping from Sql result sets into Java classes.


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
- In the "Variable value" field enter the location where you installed the JDK e.g.  
    
    C:\Program Files\Java\jdk1.8.0_65


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
On windows  press WIN-R and type "cmd"
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
To verify your installation is correct.

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

https://cloud.githubusercontent.com/assets/9968550/11138674/1d0ddb78-897a-11e5-9950-cf3de1a043d7.png

![Install  Derby CMissing  Derby Client Driver?](https://cloud.githubusercontent.com/assets/9968550/11138674/1d0ddb78-897a-11e5-9950-cf3de1a043d7.png "Missing  Derby Client Driver ?") 

## Install  Derby Client Driver

- select the   "Apache Derby Client" entry and click the pencil icon to edit
- Select the “Extra Class Path” tab
- Click on “Add” and select the <DERBY_HOME>\lib\derbyClient.jar
- In the "Class Name"" dropdown select "org.apache.derby.jdbc.ClientDriver"" and then click the OK Button
- There should now be a blue tick mark in front of the Apache Derby Client driver.
-  
![Install  Derby Client Driver](https://cloud.githubusercontent.com/assets/9968550/11138666/11858a8a-897a-11e5-9faf-73deed19ff05.png "Install  Derby Client Driver") 
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

e.g URL on Linux

    jdbc:derby://localhost:1527//home/someuser/derbydata/lecture1;create=true

e.g Url on Windows

    jdbc:derby://localhost:1527/c:/derbydata/lecture1;create=true

- The DB URL has two qualities that you may not be familar with , if you've used other databases
firstly the DERBY_DATA_DIR component needs to map to an accessible file location on the derby server.
secondly the  create=true parameter will create a set of database files at the DERBY_DATA_DIR location if no database files already exist there.

- Click on “Test”and then “Connect” to verify connection
- Click on “OK” to save
- Open up a console and change directories to your <DERBY_DATA_DIR>  you used e.g
c:\derbydata and you should see a bunch of files there.

![Create Alias](https://cloud.githubusercontent.com/assets/9968550/11138664/0d34de40-897a-11e5-8a46-9063bd94811b.png "Create Alias") 

## Connect To database from Sql Squirrel Client
At this point we can make a connection to Derby using

## Create Tables
In Sql Squirrel open the "SQL" tab.
In your IDE open up the file

    lecture1_jdbc/src/test/resources/user_2create.sql file

 and paste the contents into  the SQL tab body and then click on the run-all icon or press the following keys sequence in squirrel
 ALT-SHIFT-ENTER.

The  contents of the user_2create.sql file are shown below for easy reference e.g

    CREATE TABLE ADDRESS (
      ID      INT          NOT NULL
                           GENERATED ALWAYS   AS IDENTITY
                           (START WITH 1, INCREMENT BY 1)
                           PRIMARY KEY,
      STREET  VARCHAR(255) NOT NULL,
      STREET2 VARCHAR(255),
      CITY    VARCHAR(255) NOT NULL,
      STATE   VARCHAR(2)   NOT NULL,
      ZIP     VARCHAR(10)  NOT NULL
    );


    /* build users table which  references address table */
    CREATE TABLE USERS (
      ID           INT          NOT NULL
                                GENERATED ALWAYS  AS IDENTITY
                                (START WITH 1, INCREMENT BY 1)
                                PRIMARY KEY,
      USER_NAME     VARCHAR(15) NOT NULL CONSTRAINT users_username_UC UNIQUE,
      FIRST_NAME    VARCHAR(50) NOT NULL,
      LAST_NAME     VARCHAR(50) NOT NULL,
      ACTIVE_SINCE DATE,
      ADDRESS_ID   INT   CONSTRAINT USR_ADDR_FK REFERENCES ADDRESS
    );

    CREATE TABLE PHONE (
      ID      INT          NOT NULL
                           GENERATED ALWAYS AS IDENTITY
                           (START WITH 1, INCREMENT BY 1)
                           PRIMARY KEY,
      USER_ID INT          CONSTRAINT PHONE_USR_FK REFERENCES USERS,
      label   VARCHAR(255) ,
      phone   VARCHAR(255) NOT NULL
    );

Note the "GENERATED ALWAYS AS IDENTITY" section makes derby solely responsible for generating primary key values.

In Squirrel Click on the "Objects" tab.  Open the APP folder and then the TABLE subfolder.
You should see three Tables created "ADDRESS", "PHONE" and "USERS"

Clicking on the "Columns" tab will show you the column definitions of that table
Clicking on the "Content" tab shows there is no content setup yet.

When creating and populate data , you should be aware that there are dependencies between the tables
e.g USERS table depends on ADDRESS and PHONE table depends on USERS

## Populate Data
In your IDE open up the following file

    lecture1_jdbc/src/test/resources/user_2create.sql

Copy the contents into your Sql Client  SQL TAB      e.g

       INSERT INTO ADDRESS (   ZIP,      STATE,  CITY,        STREET )
                    values (   '98052',  'WA',   'Seattle',    '9999 Belview Ave');

       INSERT INTO ADDRESS (   ZIP,      STATE,  CITY,        STREET )
                    values (    '98034',  'WA',   'Kirkland',    '123 Main St');


       INSERT INTO USERS (USER_NAME,   FIRST_NAME, LAST_NAME,  ACTIVE_SINCE, ADDRESS_ID)
                   values('credmond', 'Conor'  , 'Redmond', '2014-12-31',  1);

       INSERT INTO USERS (USER_NAME,   FIRST_NAME, LAST_NAME,  ACTIVE_SINCE, ADDRESS_ID)
                   values('jsmith', 'John'  ,    'Smith',   '2014-02-28',  2 );


       INSERT INTO USERS (USER_NAME,   FIRST_NAME, LAST_NAME,  ACTIVE_SINCE)
                   values('pdiddy', 'Puffy'  ,    'Combs',   '2014-07-04');


       INSERT INTO PHONE (USER_ID,   LABEL,      PHONE )
                  values (1,         'HOME',     '123-555-6789' );

       INSERT INTO PHONE (USER_ID,   LABEL,      PHONE )
                  values (1,         'CELL',     '555-555-1212' );




click on the Squirrel run-all icon
In the Squirrel Objects tab , OPen up the APP/TABLE/ADDRESS folder, clicking on the "Contents" tab should show 2 rows populated

    ID  STREET ...
    1	   9999 Belview Ave	...
    2	   123 Main St	...




## Query Data
Run the following Sql To verify that everything is set up correctly

    SELECT u.id, u.user_name, u.first_name ,u.last_name, u.active_since
           , a.street, a.city , a.state , a.zip
           , p.label , p.phone
    FROM users u
    LEFT OUTER JOIN address a on  a.id = u.id
    LEFT OUTER JOIN phone p on  p.user_Id = u.id
    ORDER BY u.id;


You should see 4 rows returned (the credmond user is returned twice due to the PHONE join)

# Java Client Application Setup
Now that we have a running populated database active, we can work on connecting a Java client to it
You'll need to download or clone this git repository and then open it in your IDE.
Use one of the following guides depending on your IDE :-

## importing this project in Spring Tool Suite or Eclipse
- Click on the following Menu options : File -> Import
- In the "Import" Dialog, expand the Maven folder and then select the contained "Existing Maven Projects" option and click Next.
- In the "Import Maven Projects" Dialog, click on the Browse button next to the "Root folder"
- Navigate to the project directory containing your pom.xml file and select that project directory folder.
- In the "projects" box the  you’ll now see the pom.xml file included there.
- Click on Finish


## importing this project in Intellij
- If you are in the initial  "Welcome" dialog then Click "Open"
          Otherwise
  If you are in the Main Window select the  menu items : "File" -> "Open..."


- Navigate to the directory containing your project.
- click on the pom.xml file and Click OK
- If given the options "open Existing Project"  and "Delete Existing Project and Import"    then choose the "Delete Existing.." option
- ( this is usually a cleaner option that doesn't make assumptions on the location of Java SDK's etc.)

## importing  this project in Netbeans
- Click on the following Menu options : File -> "Open Project"
- In the "Import Project" Dialog, Navigate to the project directory containing your pom.xml file and select that project directory folder.
 (folders that have a pom.xml file will have a "Ma" icon to it's left indicating it's a maven project folder.)
- Click on the "Open Project" button


## Configure client application connection
Now that this app is in your IDE , you can start
If you've built the derby database in a location other than c:\derbydata\lecture1 then you'll need to change
the database location in following two files.

 lecture1_jdbc/src/main/resources/jdbc.properties

    jdbc.driverClassName=org.apache.derby.jdbc.ClientDriver
    jdbc.url=jdbc:derby://localhost:1527/C:/derbydata/lecture1;create=true
    jdbc.username=app
    jdbc.password=app

if you are using a Derby Database directory that is not "C:/derbydata/lecture1"
then please change the jdbc.url entry in the file above to use your directory .

If you are running on a UNIX OS,  the URL might looks something like the following

    jdbc.url=jdbc:derby://localhost:1527//home/someuser/derbydata/lecture1;create=true

## Running Unit Tests

After updating the jdbc.properties file to match the location of your database data directory
you can now run some junit tests to make a connection from the Java client application to the database server

Open the Junit File in your IDE and Execute the Test
 lecture1_jdbc/src/test/java/edu/uw/data/UserDao1_4Test.java

Execute Tests in Intellij
Select the Test file and press CTRL-SHIFT-F10

### Troubleshooting Issues
The main types of errors you might see are covered below.

#### Database server not running ?
The following error usually means the database server is not running.

    java.sql.SQLNonTransientConnectionException: java.net.ConnectException : Error connecting to server localhost on port 1,527 with message Connection refused.

you may want to open the console window and make sure it's still running.
see the 'Run Derby Database' secion above for info on starting the server


#### Database was not found ?
If you see the following error

    java.sql.SQLNonTransientConnectionException: The connection was refused because the database C:/derbydata/lecture1 was not found.

Then you may want to verify if a directory exists at that location and it contains derby subdirectories e.g "log", "Seg0", "tmp" etc
If the directory does not exist you may want create one by ensuring the ";create=true" parameter is at the end of the database url
you used in the jdbc.properties file


#### Table USERS does not exist

If you see an error similar to the following , it means that ther derby server is running,
and that there is a datbase at the location defined in the url, but the TABLE you expected has not been created yet

    java.sql.SQLSyntaxErrorException: Table/View 'USERS' does not exist.

you can use the 'Create Tables' section above to create the ADDRESS,USERS, PHONE tables

#### No Users Found

If you have an assertion in your unit test that fails e.g the following assertion line

    assertThat("was expecting at least one user found", users.size(), is(greaterThan(0)));

results in the error below

    java.lang.AssertionError: was expecting at least one user found
    Expected: is a value greater than <0>

This likely means that you have no populated data in your database.
You can use the 'Populate Data' section above to add in the missing users

#### Other Issues ?
P6Spy is a library that can be used to wrap/proxy a datasource.
It will log all SQL statements to a file called spy.log.

This spy.log file is usually created in the working or toplevel  directory of the project.
P6spy  is currently enabled by default in all of the junit test classes.

Whats nice about p6Spy is that it shows the interpolated sql,
 i.e the combination of the preparedStatement with the "?" placeholders with the actual values submiteed in those placholders.

 The JDBC package debug logs don't show this combined information, so this is a real improvement.

    1446766987015|1|statement|connection 3|INSERT INTO USERS  ( USER_NAME, first_name, last_name, ACTIVE_SINCE) VALUES ( ?, ?, ?,?)| ...
      INSERT INTO USERS  ( USER_NAME, first_name, last_name, ACTIVE_SINCE) VALUES ( 'ksauce', 'Keyser', 'Soze','05-Nov-15')


So if you have an issue not covered above,
you may find it useful to examine the spy.log file
to see the  the raw  SQL statements going over the wire.

## Building the Client application

inside the maven build file we have includes a springboot plugin
that can package the project as an executable jar.
To build the executable jar from the command line,
change directory to your project directory (the dir containing the pom.xml file)
and then run the following (it assumes you have previously installed and configured Java SDK and Maven) :-

      mvn clean package -DskipTests spring-boot:repackage

After downloading a bunch of dependencies , It should eventually create a new "target subdirectory
 with the following jar file :-

        lecture1_jdbc-1.0-SNAPSHOT.jar

## Running the Client  application
To run the client application from the command line, change directory into the newly created target subdirectory and then run the following command

        java -jar  lecture1_jdbc-1.0-SNAPSHOT.jar

 Assuming that  you have already started and populated the derby database,
 you should see output something like the following :-

         C:\lecture1_jdbc\target>java -jar lecture1_jdbc-1.0-SNAPSHOT.jar
         16:31:05.097 [main] INFO  edu.uw.data.dao.UserDao1Orig - dburl=jdbc:derby://localhost:1527/C:/derbydata/lecture1;create=true;user=app;password=app
         User{id=1, userName='credmond', firstName='Conor', lastName='Redmond', activeSince=2014-12-31, address=null, phoneNumbers=[]}
         User{id=2, userName='jsmith', firstName='John2', lastName='Smith2', activeSince=2014-02-28, address=null, phoneNumbers=[]}
         User{id=3, userName='pdiddy', firstName='up', lastName='dated', activeSince=2014-07-04, address=null, phoneNumbers=[]}


