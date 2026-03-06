package com.smartcontactmanager.smart_contact_manager_backend.Excpetion.CustomException;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
