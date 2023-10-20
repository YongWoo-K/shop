package com.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing  //-> JPA의 Auditing기능을 활성화 해주는 어노테이션
public class AuditConfig {
    @Bean
    public AuditorAware<String> auditorProvider(){
        return new AuditorAwareImpl();
    }
    //-> 등록자와 수정자를 처리해주는 AuditorAware를 Bean으로 등록한다.
}
