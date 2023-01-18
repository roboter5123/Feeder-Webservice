package com.roboter5123.feeder;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class FeederApplication {

    public static void main(String[] args) {

        SpringApplication.run(FeederApplication.class, args);
    }

}
