package webcrawler.exception;

import webcrawler.rule.ECrawlRule;

public class ValidationAfterParseException extends RuntimeException {
    private final ECrawlRule crawlRule;
    public ValidationAfterParseException(final ECrawlRule crawlRule, final String message){
        super(message);
        this.crawlRule = crawlRule;
    }

    public ECrawlRule getCrawlRule() {
        return crawlRule;
    }
}
