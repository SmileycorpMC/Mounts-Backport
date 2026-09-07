package net.smileycorp.mounts.config.data;

public class ParsingException extends Exception {

    private String file;

    public ParsingException(String message) {
        super(message);
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }


}
