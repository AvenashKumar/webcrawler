package webcrawler.model;

import webcrawler.rule.ECrawlRule;

public class FailedUrl {
    private final String failedUrl;
    private ECrawlRule crawlRule;
    private String description;

    public FailedUrl(String failedUrl, ECrawlRule crawlRule, String description) {
        this.failedUrl = failedUrl;
        this.crawlRule = crawlRule;
        this.description = description;
    }

    public FailedUrl(String failedUrl, ECrawlRule crawlRule) {
        this(failedUrl, crawlRule, "");
    }

    public FailedUrl(final String failedUrl, final String description) {
        this(failedUrl, null, description);
    }

    public String getFailedUrl() {
        return failedUrl;
    }

    public ECrawlRule getCrawlRule() {
        return crawlRule;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
