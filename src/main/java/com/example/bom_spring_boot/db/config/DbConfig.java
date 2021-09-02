package com.example.bom_spring_boot.db.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
    entityManagerFactoryRef = "dbEntityManagerFactory",
    transactionManagerRef = "dbTransactionManager",
        basePackages = {"com.example.bom_spring_boot.repository"}
)
public class DbConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.db")
    public DataSource dbDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean dbEntityManagerFactory(
        EntityManagerFactoryBuilder builder,
        @Qualifier("dbDataSource") DataSource dataSource
    ) {
        return
            builder.dataSource(dataSource)
                .packages("com.example.bom_spring_boot.entity")
                .persistenceUnit("db")
                .build();
    }

    @Bean
    public PlatformTransactionManager dbTransactionManager(
        @Qualifier("dbEntityManagerFactory") EntityManagerFactory dbEntityManagerFactory
    ) {
        return new JpaTransactionManager(dbEntityManagerFactory);
    }
}
