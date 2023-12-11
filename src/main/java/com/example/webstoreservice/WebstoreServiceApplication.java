package com.example.webstoreservice;

import com.example.commoncode.config.CommonCodeConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication(scanBasePackageClasses = {WebstoreServiceApplication.class,
    CommonCodeConfig.class})
public class WebstoreServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(WebstoreServiceApplication.class, args);
  }

}
