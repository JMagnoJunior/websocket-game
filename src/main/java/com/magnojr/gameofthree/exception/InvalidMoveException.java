package com.magnojr.gameofthree.exception;

public class InvalidMoveException extends RuntimeException {

    public InvalidMoveException(String msg) {
        super(msg);
    }
}
