package com.example.azuredevopsmcpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class AzureDevOpsMcpServerApplication {

    Dotenv dotenv = Dotenv.load();
    String org = dotenv.get("AZURE_DEVOPS_ORGANIZATION");
    String pat = dotenv.get("AZURE_DEVOPS_PAT");

    public static void main(String[] args) {
        SpringApplication.run(AzureDevOpsMcpServerApplication.class, args);
    }

}