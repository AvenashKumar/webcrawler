package webcrawler.exception;

public class InProcessUrlQueueEmptyException extends RuntimeException {
    public InProcessUrlQueueEmptyException(final String message){
        super(message);
    }
}
