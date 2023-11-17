package me.unfamousthomas.exceptions;

public class OperationReadingException extends RuntimeException{

    public OperationReadingException(String line) {
        super("Could not read operation with line: " + line);
    }
}
