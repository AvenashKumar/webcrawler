package webcrawler.service;

import webcrawler.exception.CrawlerNotAllowedException;
import webcrawler.exception.InvalidUrlException;
import webcrawler.model.DomainInfo;
import webcrawler.model.ParsedUrlEx;
import webcrawler.service.normalization.CanonicalizationImpl;
import webcrawler.service.normalization.ICanonicalization;
import webcrawler.service.roboparser.IRobotRulesParser;
import webcrawler.service.roboparser.RobotRulesParserImpl;
import crawlercommons.robots.BaseRobotRules;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static crawlercommons.robots.BaseRobotRules.UNSET_CRAWL_DELAY;

public class DomainManager {
    private static DomainManager domainManagerInstance;
    private final ICanonicalization urlNormalizer;
    private final IRobotRulesParser robotRulesParser;

    private final Map<String, DomainInfo> mapDomainInfo;

    private DomainManager() {
        mapDomainInfo = new ConcurrentHashMap<>();
        urlNormalizer = new CanonicalizationImpl();
        robotRulesParser = new RobotRulesParserImpl();
    }

    /**
     * This method is responsible for creating domain manager singleton object
     * @return domain manager object
     */
    public static DomainManager getInstance()
    {
        if (domainManagerInstance == null)
            domainManagerInstance = new DomainManager();

        return domainManagerInstance;
    }

    public int getMapDomainSize(){
        return mapDomainInfo.size();
    }

    public void clear(){
        mapDomainInfo.clear();
    }

    private void validateIsAllowedRule(final String absUrl, final BaseRobotRules baseRobotRules) throws CrawlerNotAllowedException {
        if(baseRobotRules==null)
            return;

        if(baseRobotRules.isAllowNone() || !baseRobotRules.isAllowed(absUrl))
            throw new CrawlerNotAllowedException("Crawler not allowed!");
    }

    private String createRobotUrl(final String protocolDomain){
        return protocolDomain + "/robots.txt";
    }

    private void preProcessing(final String normalizedUrl, final DomainInfo domainInfo){
        final ParsedUrlEx parsedUrl = urlNormalizer.canonicalize(null, normalizedUrl);

        final String domain = parsedUrl.getHost();
        if(domain==null || domain.trim().isEmpty()){
            throw new InvalidUrlException("Invalidate URL encountered.");
        }

        domainInfo.setSrcOriginalUrl(parsedUrl.toString());

        domainInfo.setParsedUrlEx(parsedUrl);

        final String protocolDomain = parsedUrl.getProtocolDomain();
        final String robotUrl = createRobotUrl(protocolDomain);
        domainInfo.setRobotUrl(robotUrl);
    }

    private BaseRobotRules parseRobotRules(final DomainInfo domainInfo){
        BaseRobotRules baseRobotRules = robotRulesParser.parse(domainInfo.getRobotUrl());
        final long crawlDelay = baseRobotRules.getCrawlDelay();
        if (crawlDelay != UNSET_CRAWL_DELAY)
            domainInfo.setCrawlDelay(crawlDelay);

        return baseRobotRules;
    }

    private void postProcessing(final String absUrl, final BaseRobotRules robotRules) throws CrawlerNotAllowedException {
        validateIsAllowedRule(absUrl, robotRules);
    }

    private boolean isDomainInMap(final String domain){
        return getDomainInfo(domain) != null;
    }

    private DomainInfo getDomainInfo(final String domain){
        if(this.mapDomainInfo.containsKey(domain))
            return this.mapDomainInfo.get(domain);

        final String without3W = domain.replaceFirst("^www.", "");
        if(this.mapDomainInfo.containsKey(without3W))
            return this.mapDomainInfo.get(without3W);

        final String with3W = "www." + without3W;
        if(this.mapDomainInfo.containsKey(with3W))
            return this.mapDomainInfo.get(with3W);

        return null;
    }

    public DomainInfo add(final String normalizedUrl) throws CrawlerNotAllowedException {
        final DomainInfo domainInfo = new DomainInfo();

        preProcessing(normalizedUrl, domainInfo);

        final String domain = domainInfo.getDomain();
        if(this.isDomainInMap(domain))
            return this.getDomainInfo(domain);

        final BaseRobotRules baseRobotRules = parseRobotRules(domainInfo);

        postProcessing(domainInfo.getSrcOriginalUrl(), baseRobotRules);

        this.mapDomainInfo.put(domain, domainInfo);

        return domainInfo;
    }






    public void updateAccessTime(final String domain){
        final DomainInfo domainInfo = this.getDomainInfo(domain);
        domainInfo.setLastAccessTime(LocalDateTime.now());
    }

    /**
     * This method will help to check politeness.
     * @param domain
     * @return either true or false
     */
    public long findLastAccessTimeDiff(final String domain) {
        if (!this.isDomainInMap(domain))
            return -1;

        final DomainInfo domainInfo = this.getDomainInfo(domain);
        LocalDateTime lastAccessTime = domainInfo.getLastAccessTime();
        if(lastAccessTime == null)
            return -1;

        LocalDateTime currentTime = LocalDateTime.now();
        long timeDiffLastAccessCurrTime = ChronoUnit.MILLIS.between(lastAccessTime, currentTime);
        if (timeDiffLastAccessCurrTime > domainInfo.getCrawlDelay())
            return -1;

        return domainInfo.getCrawlDelay() - timeDiffLastAccessCurrTime;
    }
}
