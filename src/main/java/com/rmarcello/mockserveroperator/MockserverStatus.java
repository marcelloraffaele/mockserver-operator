package com.rmarcello.mockserveroperator;

import io.javaoperatorsdk.operator.api.ObservedGenerationAwareStatus;

public class MockserverStatus extends ObservedGenerationAwareStatus {
    private Boolean areWeGood;
    private String testUrl;
    private String managementUrl;
    private String errorMessage;

    public Boolean getAreWeGood() {
        return areWeGood;
    }

    public MockserverStatus setAreWeGood(Boolean areWeGood) {
        this.areWeGood = areWeGood;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public MockserverStatus setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public Boolean isAreWeGood() {
        return this.areWeGood;
    }

    public String getTestUrl() {
        return this.testUrl;
    }

    public MockserverStatus setTestUrl(String testUrl) {
        this.testUrl = testUrl;
        return this;
    }

    public String getManagementUrl() {
        return this.managementUrl;
    }

    public MockserverStatus setManagementUrl(String managementUrl) {
        this.managementUrl = managementUrl;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                " areWeGood='" + isAreWeGood() + "'" +
                ", testUrl='" + getTestUrl() + "'" +
                ", managementUrl='" + getManagementUrl() + "'" +
                ", errorMessage='" + getErrorMessage() + "'" +
                "}";
    }

}
