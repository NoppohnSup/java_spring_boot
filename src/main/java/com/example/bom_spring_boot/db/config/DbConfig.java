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
    entityManagerFactoryRef = "fmsEntityManagerFactory",
    transactionManagerRef = "fmsTransactionManager",
    basePackages = {"ascend.aden.db.fms.repository"}
)
public class DbConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.fms")
    public DataSource fmsDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean fmsEntityManagerFactory(
        EntityManagerFactoryBuilder builder,
        @Qualifier("fmsDataSource") DataSource dataSource
    ) {
        return
            builder.dataSource(dataSource)
                .packages("ascend.aden.db.fms.entity")
                .persistenceUnit("fms")
                .build();
    }

    @Bean
    public PlatformTransactionManager fmsTransactionManager(
        @Qualifier("fmsEntityManagerFactory") EntityManagerFactory fmsEntityManagerFactory
    ) {
        return new JpaTransactionManager(fmsEntityManagerFactory);
    }
}
