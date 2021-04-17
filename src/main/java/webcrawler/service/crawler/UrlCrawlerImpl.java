package webcrawler.service.crawler;

import webcrawler.exception.ValidationAfterParseException;
import webcrawler.exception.ValidationBeforeParseException;
import webcrawler.model.DomainInfo;
import webcrawler.model.ParsedUrlEx;
import webcrawler.model.UrlInfo;
import webcrawler.rule.RuleValidateExecutor;
import webcrawler.service.DomainManager;
import webcrawler.service.normalization.CanonicalizationImpl;
import webcrawler.service.normalization.ICanonicalization;
import webcrawler.service.relevancy.IRelevancyChecker;
import webcrawler.service.relevancy.RelevancyCheckerImpl;
import webcrawler.service.weight.IWeightCalculator;
import webcrawler.service.weight.WeightCalculatorImpl;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static webcrawler.constants.CCrawler.URL_CONNECT_TIMEOUT;

public class UrlCrawlerImpl implements IUrlCrawler {
    private static final Logger logger = LoggerFactory.getLogger(UrlCrawlerImpl.class);

    private static final DomainManager domainManager = DomainManager.getInstance();
    private static final RuleValidateExecutor ruleValidateExecutor = new RuleValidateExecutor();
    private final ICanonicalization urlNormalizer;

    private final UrlInfo urlInfo;
    private Connection.Response jsoupResponse;


    public UrlCrawlerImpl(final UrlInfo urlInfo) {
        this.urlInfo = urlInfo;
        urlNormalizer = new CanonicalizationImpl();
    }

    private void preCrawl() throws ValidationBeforeParseException, IOException, InterruptedException {
        //Get Normalized Url
        final String normalizedUrl = urlInfo.getNormalizedUrlWithProtocol();

        //Finding domain information
        DomainInfo domainInfo = domainManager.add(normalizedUrl);
        urlInfo.setDomainInfo(domainInfo);

        //Make sure politeness
        waitBeforeCrawlIfRequired();

        //Connect with URL
        jsoupResponse = Jsoup.connect(normalizedUrl).timeout(URL_CONNECT_TIMEOUT).execute();

        //Update access time
        final String domain = urlInfo.getDomainInfo().getDomain();
        domainManager.updateAccessTime(domain);

        //Setting content type
        urlInfo.setContentType(jsoupResponse.contentType());

        //Either redirect?
        urlInfo.setRedirect(jsoupResponse.hasHeader("location"));

        //Validate all rules, this call will throw exception
        ruleValidateExecutor.validateBeforeContentParse(urlInfo);
    }

    private void waitBeforeCrawlIfRequired() throws InterruptedException {
        final String domain = urlInfo.getDomainInfo().getDomain();
        long lastAccessTimeDiff = domainManager.findLastAccessTimeDiff(domain);
        if (lastAccessTimeDiff > 0)
            Thread.sleep(lastAccessTimeDiff);
    }

    private void postCrawl(final Document document) throws ValidationAfterParseException {
        //Validate all rules that are applicable for after parsing content
        //This call will throw exception
        ruleValidateExecutor.validateAfterContentParse(urlInfo);

        //Fetch out links
        this.fetchOutLinks(document);
    }

    @Override
    public void crawl() throws ValidationBeforeParseException,
            ValidationAfterParseException,
            InterruptedException,
            IOException {

        //Check either to crawl or not
        preCrawl();

        //Parse/Crawl content
        final Document document = jsoupResponse.parse();

        //Fetch necessary information
        urlInfo.setTitle(document.title());
        urlInfo.setBodyText(document.body().text());

        postCrawl(document);
    }

    private void fetchOutLinks(Document document) {
        Elements links = document.select("a");
        for (Element link : links) {
            String absHref = link.attr("abs:href").trim();
            if (!absHref.trim().isEmpty() && (absHref.contains("http") || absHref.contains("https"))) {
                final ParsedUrlEx parsedUrlEx = urlNormalizer.canonicalize(null, absHref);
                urlInfo.addOutLink(parsedUrlEx.toString());
            }
        }
    }
}
