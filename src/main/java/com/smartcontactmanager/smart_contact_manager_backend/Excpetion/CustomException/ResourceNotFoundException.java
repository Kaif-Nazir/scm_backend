package com.smartcontactmanager.smart_contact_manager_backend.Excpetion.CustomException;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
