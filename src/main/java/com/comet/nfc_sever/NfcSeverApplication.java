package com.comet.nfc_sever;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class NfcSeverApplication {

    public static void main(String[] args) {
        SpringApplication.run(NfcSeverApplication.class, args);
    }

}
