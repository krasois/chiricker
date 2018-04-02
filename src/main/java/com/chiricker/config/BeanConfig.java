package com.chiricker.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class BeanConfig {

    private static final String ACCESS_TOKEN = "PHECHZR-GXAAAAAAAAAAB0b0X3A73gKz2HlrxT2v_hjxBqbCAycrDbrCfZcX8oog";

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DbxRequestConfig config() {
        return new DbxRequestConfig("dropbox/chiricker");
    }

    @Bean
    public DbxClientV2 client() throws Exception {
        return new DbxClientV2(this.config(), ACCESS_TOKEN);
    }
}