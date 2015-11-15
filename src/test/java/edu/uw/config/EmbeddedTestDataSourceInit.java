package edu.uw.config;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Profile("dev")
//@PropertySource("classpath:dbtest.properties")
@Configuration
public class EmbeddedTestDataSourceInit {

//    @Autowired
//    private Environment environment;

    @Bean(name = "dataSource")
    public DataSource getDataSource(){
        DataSource dataSource = createDataSource();
        DatabasePopulatorUtils.execute(createDatabasePopulator(), dataSource);
        return dataSource;
    }

    private DatabasePopulator createDatabasePopulator() {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.setContinueOnError(true);
      //  databasePopulator.addScript(new ClassPathResource("book_drop.sql"));
        databasePopulator.addScript(new ClassPathResource("user_2create.sql"));
        databasePopulator.addScript(new ClassPathResource("user_3insert.sql"));
        return databasePopulator;
    }

    private DataSource createDataSource() {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("tempDb");
        ds.setCreateDatabase("create");
        ds.setUser("app");
        ds.setPassword("app");

        return ds;
    }
}