package edu.uw.data.dao;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.derby.jdbc.ClientDataSource;

import java.util.Properties;

/**
 * Created by credmond on 11/4/2015.
 */
public class DataSourceHelper {
    public static ClientDataSource buildClientDataSourceFromFile() {
        ClientDataSource dataSource = new ClientDataSource();

        // load properties from a file
        Properties props = AbstractUserDao.loadJdbcProperties("jdbc.properties");
        String username = props.getProperty("jdbc.username");
        String password = props.getProperty("jdbc.password");
        String serverName = props.getProperty("server.name");
        String databaseName = props.getProperty("database.name");

        //build datasource
        dataSource.setServerName(serverName);
        dataSource.setDatabaseName(databaseName);   //location of derby database file.
        dataSource.setUser(username);
        dataSource.setPassword(password);
        return dataSource;
    }


    public static BasicDataSource buildDebugClientDataSourceFromFile() {

        BasicDataSource dataSource = new BasicDataSource();

        // load properties from a file
        Properties props = AbstractUserDao.loadJdbcProperties("jdbc.properties");
        String url = props.getProperty("p6spy.url");
        String username = props.getProperty("jdbc.username");
        String password = props.getProperty("jdbc.password");

        //build datasource
        //

        System.out.println("url="+url);
        System.out.println("username="+username);
        System.out.println("password="+password);

        dataSource.setDriverClassName("com.p6spy.engine.spy.P6SpyDriver"); //p6 driver logs lines, passes on to derby driver defined in spy.properties
        dataSource.setUrl(url);   //location of derby database file.
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}
