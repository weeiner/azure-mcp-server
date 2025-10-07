package com.example.azuredevopsmcpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class AzureDevOpsMcpServerApplication {

    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // Set system properties for Spring Boot to use
        String org = dotenv.get("AZURE_DEVOPS_ORGANIZATION");
        String pat = dotenv.get("AZURE_DEVOPS_PAT");

        if (org != null && !org.isEmpty()) {
            System.setProperty("AZURE_DEVOPS_ORGANIZATION", org);
        }
        if (pat != null && !pat.isEmpty()) {
            System.setProperty("AZURE_DEVOPS_PAT", pat);
        }

        SpringApplication.run(AzureDevOpsMcpServerApplication.class, args);
    }

}