package com.pl.edu.wieik.flightScheduler.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.ZoneId;

@RequiredArgsConstructor
@Configuration
public class Config {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Clock clock(){
        return Clock.system(ZoneId.systemDefault());
    }


}
