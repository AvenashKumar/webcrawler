package webcrawler.exception;

public class SeedsNotFoundException extends RuntimeException{
    public SeedsNotFoundException(final String message) {
        super(message);
    }
}