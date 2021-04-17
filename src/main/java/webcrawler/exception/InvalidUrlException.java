package webcrawler.exception;

public class InvalidUrlException extends RuntimeException {
    public InvalidUrlException(final String message){
        super(message);
    }
}
