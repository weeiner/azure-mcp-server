package com.example.azuredevopsmcpserver.azuredevops;

public class AzureDevOpsCredentials {
    private final String organization;
    private final String personalAccessToken;

    public AzureDevOpsCredentials(String organization, String personalAccessToken) {
        this.organization = organization;
        this.personalAccessToken = personalAccessToken;
    }

    public String getOrganization() {
        return organization;
    }

    public String getPersonalAccessToken() {
        return personalAccessToken;
    }
}
