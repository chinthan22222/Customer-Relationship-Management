package org.assignment.crm.exception;

public class UserNameExists extends RuntimeException {
    public UserNameExists(String message) {
        super(message);
    }
}
