package webcrawler.exception;

import webcrawler.rule.ECrawlRule;

public class ValidationBeforeParseException extends RuntimeException {
    private final ECrawlRule crawlRule;
    public ValidationBeforeParseException(final ECrawlRule crawlRule, final String message){
        super(message);
        this.crawlRule = crawlRule;
    }

    public ECrawlRule getCrawlRule() {
        return crawlRule;
    }
}
