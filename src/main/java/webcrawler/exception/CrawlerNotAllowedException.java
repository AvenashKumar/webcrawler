package webcrawler.exception;

public class CrawlerNotAllowedException extends RuntimeException{
    public CrawlerNotAllowedException(final String message){
        super(message);
    }
}
