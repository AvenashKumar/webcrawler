package webcrawler.model;

import webcrawler.constants.CCrawler;

import java.time.LocalDateTime;

public class DomainInfo {
    private LocalDateTime lastAccessTime;
    private long crawlDelay;
    private String robotUrl;
    private String srcOriginalUrl;
    private ParsedUrlEx parsedUrlEx;


    public DomainInfo() {
        this.crawlDelay = CCrawler.CRAWL_DELAY;
    }



    public String getDomain() {
        return this.getParsedUrlEx().getHost();
    }

    public LocalDateTime getLastAccessTime() {
        return lastAccessTime;
    }

    public long getCrawlDelay() {
        return crawlDelay;
    }

    public String getProtocol() {
        return this.parsedUrlEx.getScheme();
    }

    public void setLastAccessTime(LocalDateTime lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public void setCrawlDelay(long crawlDelay) {
        this.crawlDelay = crawlDelay;
    }

    public String getProtocolDomain() {
        return this.parsedUrlEx.getProtocolDomain();
    }

    public String getRobotUrl() {
        return robotUrl;
    }

    public void setRobotUrl(String robotUrl) {
        this.robotUrl = robotUrl;
    }

    public String getSrcOriginalUrl() {
        return srcOriginalUrl;
    }

    public ParsedUrlEx getParsedUrlEx() {
        return parsedUrlEx;
    }

    public void setParsedUrlEx(ParsedUrlEx parsedUrlEx) {
        this.parsedUrlEx = parsedUrlEx;
    }

    public void setSrcOriginalUrl(String srcOriginalUrl) {
        this.srcOriginalUrl = srcOriginalUrl;
    }
}
