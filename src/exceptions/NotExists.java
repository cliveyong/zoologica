package exceptions;

public class NotExists extends Exception {
    public NotExists(String errorMessage) {
        super(errorMessage);
    }
}
