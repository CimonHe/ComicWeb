//package com.CimonHe.config;
//
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//
//@Configuration
//public class CorsConfig extends WebMvcConfigurationSupport {
//    @Override
//    protected void addCorsMappings(CorsRegistry registry) {
//        System.out.println("----------------------");
//        registry.addMapping("/**")
//                .allowedOrigins("*")
//                .allowCredentials(true)
//                .allowedMethods("GET", "POST", "DELETE", "PUT")
//                .maxAge(3600);
//    }
//}