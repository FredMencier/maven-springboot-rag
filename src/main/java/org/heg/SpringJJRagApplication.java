package org.heg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SpringJJRagApplication {

    private static final Logger LOG = LoggerFactory.getLogger(SpringJJRagApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringJJRagApplication.class, args);
    }
}
