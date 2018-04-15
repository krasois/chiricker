package com.chiricker.config;

import com.chiricker.areas.users.utils.FileUploader;
import com.chiricker.areas.users.utils.FileUploaderImpl;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class BeanConfig {

    private static final String DROPBOX_APPLICATION_NAME = "dropbox/chiricker";
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
        return new DbxRequestConfig(DROPBOX_APPLICATION_NAME);
    }

    @Bean
    public DbxClientV2 client() {
        return new DbxClientV2(this.config(), ACCESS_TOKEN);
    }

    @Bean
    public FileUploader fileUploader() {
        return new FileUploaderImpl(this.client());
    }
}