package com.example.code.version1.dto;


public class RequestStatusResponse {

    private boolean found;

    // SUCCESS / FAILED / NOT_FOUND
    private String status;

    private String requestId;

    private String transactionId;

    private String fromUsername;
    private String toUsername;

    private String fromAccount;
    private String toAccount;

    private double amount;

    // Used only for failed requests
    private String reason;

    public RequestStatusResponse() {
        found = false;
        status = "NOT_FOUND";
    }

    // =====================
    // GETTERS
    // =====================

    public boolean isFound() {
        return found;
    }

    public String getStatus() {
        return status;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public String getToUsername() {
        return toUsername;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public double getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    // =====================
    // SETTERS
    // =====================

    public void setFound(boolean found) {
        this.found = found;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public void setToUsername(String toUsername) {
        this.toUsername = toUsername;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}