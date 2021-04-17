package webcrawler.exception;

public class HttpConnectionException extends RuntimeException{
    public HttpConnectionException(final String message) {
        super(message);
    }
}
