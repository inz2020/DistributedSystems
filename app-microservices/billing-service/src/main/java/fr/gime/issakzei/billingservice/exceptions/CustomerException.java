package fr.gime.issakzei.billingservice.exceptions;


public class CustomerException extends RuntimeException {

    public  CustomerException(String message){
        super(message);
    }
}