package com.redhat.demo.saga.insurance.rest;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ErrorMessage {

    private String message;
    private String severity;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
