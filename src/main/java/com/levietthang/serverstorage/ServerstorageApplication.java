package com.levietthang.serverstorage;

import com.levietthang.serverstorage.service.GoogleDriveService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.io.IOException;
import java.security.GeneralSecurityException;

@SpringBootApplication
@EntityScan(basePackages = {"com.levietthang.serverstorage", "com.levietthang.serverstorage.entities"})
public class ServerstorageApplication {

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        SpringApplication.run(ServerstorageApplication.class, args);
        GoogleDriveService.getDriveService();
    }

}
