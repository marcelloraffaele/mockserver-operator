package com.rmarcello.mockserveroperator;

import io.javaoperatorsdk.operator.api.ObservedGenerationAwareStatus;

public class MockserverStatus extends ObservedGenerationAwareStatus {
    private Boolean areWeGood;
    private String internalUrl;
    private String externalUrl;
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

    public String getInternalUrl() {
        return internalUrl;
    }

    public void setInternalUrl(String internalUrl) {
        this.internalUrl = internalUrl;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    @Override
    public String toString() {
        return "MockserverStatus [areWeGood=" + areWeGood + ", internalUrl=" + internalUrl + ", externalUrl="
                + externalUrl + ", errorMessage=" + errorMessage + "]";
    }

}
