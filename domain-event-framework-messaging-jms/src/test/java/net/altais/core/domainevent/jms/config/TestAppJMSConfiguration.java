package net.altais.core.domainevent.jms.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

import javax.jms.ConnectionFactory;

@Configuration
@EnableJms
@EnableConfigurationProperties(ActiveMQProperties.class)
public class TestAppJMSConfiguration {

    @Bean
    public ConnectionFactory connectionFactory(ActiveMQProperties properties) {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(properties.getBrokerUrl());
        activeMQConnectionFactory.setUserName(properties.getUser());
        activeMQConnectionFactory.setPassword(properties.getPassword());
        return activeMQConnectionFactory;
    }
}
