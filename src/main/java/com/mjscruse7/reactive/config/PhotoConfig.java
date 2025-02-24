package com.mjscruse7.reactive.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class PhotoConfig {

    @Value("${config.uploads.path}")
    private String uploadPath;
}
