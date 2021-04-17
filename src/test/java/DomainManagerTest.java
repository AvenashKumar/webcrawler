import webcrawler.exception.CrawlerNotAllowedException;
import webcrawler.exception.InvalidUrlException;
import webcrawler.model.DomainInfo;
import webcrawler.service.DomainManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DomainManagerTest {

    private DomainManager domainManager;

    @Before
    public void setup(){
        domainManager = DomainManager.getInstance();
        domainManager.clear();
    }

    @Test(expected = InvalidUrlException.class)
    public void addInvalidUrlTest() {
        domainManager.add("not a url");
    }

    @Test
    public void addHttpsGoogleUrlTest() {
        final String inputUrl = "https://www.google.com";
        final DomainInfo domainInfo = domainManager.add(inputUrl);
        final String expectedRobotUrl = inputUrl + "/robots.txt";

        assertEquals(expectedRobotUrl, domainInfo.getRobotUrl());
        assertEquals("www.google.com", domainInfo.getDomain());
        assertEquals(inputUrl, domainInfo.getProtocolDomain());
        assertEquals("https", domainInfo.getProtocol());
        assertEquals(1000, domainInfo.getCrawlDelay());
        assertEquals(null, domainInfo.getLastAccessTime());
        assertEquals(1, domainManager.getMapDomainSize());
    }

    @Test
    public void addHttpGoogleUrlTest() {
        final String inputUrl = "http://www.google.com";
        final DomainInfo domainInfo = domainManager.add(inputUrl);
        final String expectedRobotUrl = inputUrl + "/robots.txt";

        assertEquals(expectedRobotUrl, domainInfo.getRobotUrl());
        assertEquals("www.google.com", domainInfo.getDomain());
        assertEquals(inputUrl, domainInfo.getProtocolDomain());
        assertEquals("http", domainInfo.getProtocol());
        assertEquals(1000, domainInfo.getCrawlDelay());
        assertEquals(null, domainInfo.getLastAccessTime());
        assertEquals(1, domainManager.getMapDomainSize());
    }

    @Test
    public void addCompleteUrl() {
        final String inputUrl = "http://en.wikipedia.org/wiki/American_Revolutionary_War";
        final DomainInfo domainInfo = domainManager.add(inputUrl);

        assertEquals("en.wikipedia.org", domainInfo.getDomain());
        assertEquals("http://en.wikipedia.org", domainInfo.getProtocolDomain());
        assertEquals("http", domainInfo.getProtocol());
        assertEquals(1000, domainInfo.getCrawlDelay());
        assertEquals(null, domainInfo.getLastAccessTime());
        assertEquals(1, domainManager.getMapDomainSize());

        final String expectedRobotUrl = domainInfo.getProtocolDomain() + "/robots.txt";
        assertEquals(expectedRobotUrl, domainInfo.getRobotUrl());
    }

    @Test(expected = CrawlerNotAllowedException.class)
    public void addCrawlerNotAllowedUrlTest(){
        final String crawlerNotAllowedUrl = "https://www.usmobile.com/help/article-categories/";
        final DomainInfo domainInfo = domainManager.add(crawlerNotAllowedUrl);
    }

    @Test
    public void addSameDomainWithAndWithoutHttps(){
        final String httpsUrl = "https://thepdfguru.com";
        final DomainInfo domainInfo1 = domainManager.add(httpsUrl);

        final String httpUrl = "http://thepdfguru.com";
        final DomainInfo domainInfo2 = domainManager.add(httpUrl);

        assertEquals(1, domainManager.getMapDomainSize());
        assertEquals(domainInfo1, domainInfo2);
    }

    @Test
    public void addNoProtocolUrl(){
        final String httpsUrl = "thepdfguru.com";
        final DomainInfo domainInfo1 = domainManager.add(httpsUrl);

        final String httpUrl = "http://thepdfguru.com";
        final DomainInfo domainInfo2 = domainManager.add(httpUrl);

        assertEquals(1, domainManager.getMapDomainSize());
        assertEquals(domainInfo1, domainInfo2);
    }

    @Test
    public void addDomainWithAndWithoutWWW(){
        final String httpsUrl = "www.thepdfguru.com";
        final DomainInfo domainInfo1 = domainManager.add(httpsUrl);

        final String httpUrl = "thepdfguru.com";
        final DomainInfo domainInfo2 = domainManager.add(httpUrl);

        assertEquals(1, domainManager.getMapDomainSize());
        assertEquals(true, domainInfo1 == domainInfo2);
    }

    @Test
    public void addProtocolDomainWithPortTest(){
        final String httpsUrl = "https://thepdfguru.com:80/image-to-pdf";
        final DomainInfo domainInfo1 = domainManager.add(httpsUrl);
        assertEquals(1, domainManager.getMapDomainSize());
    }

    @Test(expected = InvalidUrlException.class)
    public void addWithoutProtocolWithPortDomainTest(){
        final String httpsUrl = "www.thepdfguru.com:80/image-to-pdf";
        final DomainInfo domainInfo1 = domainManager.add(httpsUrl);
    }

    @Test
    public void addDomainFromUrlHavingPathTest(){
        final String httpsUrl = "www.thepdfguru.com/image-to-pdf";
        final DomainInfo domainInfo1 = domainManager.add(httpsUrl);
        assertEquals(1, domainManager.getMapDomainSize());
    }
}
