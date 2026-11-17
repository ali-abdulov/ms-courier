package com.ali.mscourier.exception;

public class CourierNotFoundException extends RuntimeException {
    public CourierNotFoundException(Long courierId) {
        super("Courier not found with id: " + courierId);
    }
}
