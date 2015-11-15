package edu.uw.data.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Created by Conor on 4/12/2015.
 */
@Configuration
@PropertySource("classpath:jdbc.properties")
@EnableTransactionManagement
public class DataSourceConfig {
  @Autowired
  private Environment environment;

  @Bean
  public DataSource dataSource() {
    DriverManagerDataSource ds = new DriverManagerDataSource();
    ds.setDriverClassName(environment.getProperty("jdbc.driverClassName"));
    ds.setUrl(environment.getProperty("jdbc.url"));
    ds.setUsername(environment.getProperty("jdbc.username"));
    ds.setPassword(environment.getProperty("jdbc.password"));
    return ds;
  }

  @Bean
  public PlatformTransactionManager txManager() {
    return new DataSourceTransactionManager(dataSource());
  }


}
