package net.smileycorp.mounts.config.data;

public class ParsingException extends Exception {

    private String script;

    public ParsingException(String message) {
        super(message);
    }

}
