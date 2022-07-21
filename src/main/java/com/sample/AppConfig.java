package com.sample;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.sample.service.xero.XeroService;
import com.sample.service.xero.XeroServiceImpl;

@Configuration
@EnableJpaAuditing
public class AppConfig {

}