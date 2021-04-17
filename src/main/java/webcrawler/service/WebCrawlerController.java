package webcrawler.service;

import com.google.gson.Gson;
import webcrawler.exception.RelevancyKeywordsNotFoundException;
import webcrawler.exception.SeedsNotFoundException;
import webcrawler.exception.ValidationAfterParseException;
import webcrawler.exception.ValidationBeforeParseException;
import webcrawler.model.FailedUrl;
import webcrawler.model.UrlInfo;
import webcrawler.service.crawler.UrlCrawlerImpl;
import webcrawler.service.crawler.IUrlCrawler;
import webcrawler.utils.FileUtilsEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static webcrawler.constants.CCrawler.TOTAL_URLS_TO_CRAWL;

public class WebCrawlerController {
    private static final Logger logger = LoggerFactory.getLogger(WebCrawlerController.class);

    private final FailedUrlsManager failedUrlsManager;
    private final DocumentsManager documentsManager;
    private final Set<String> relevantKeywords;
    private final String seedUrlsFilePath;
    private final String relevantKeywordsFilePath;
    private final Map<String, Integer> seedUrls;
    private UrlFrontier urlFrontier;
    private final String resourceDir;
    private final String failedUrlDir;
    private final String docDataDir;
    private final Gson gson;

    public WebCrawlerController(final String resourceDir, final String seedUrlsFileName) throws IOException {
        this.resourceDir = resourceDir;
        this.failedUrlDir = this.resourceDir + File.separator + "failed-urls";
        this.docDataDir = this.resourceDir + File.separator + "aneeta-doc-collection";
        this.seedUrlsFilePath = this.resourceDir + File.separator + seedUrlsFileName;
        this.relevantKeywordsFilePath = this.resourceDir + File.separator + "relevancy-keywords.txt";
        this.failedUrlsManager = new FailedUrlsManager(this.failedUrlDir);
        this.documentsManager = new DocumentsManager(this.docDataDir);
        relevantKeywords = new LinkedHashSet<>();
        seedUrls = new LinkedHashMap<>();
        gson = new Gson();
        validate();
        init();
    }

    private void readSeedUrlsData() throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(seedUrlsFilePath));
        Map<String, Double> mapUrlsWithDepth = gson.fromJson(reader, Map.class);
        seedUrls.putAll(mapUrlsWithDepth.entrySet().stream().collect(Collectors.toMap(
                entry -> entry.getKey(),
                entry -> entry.getValue().intValue())));
    }

    private void init() throws IOException {
        readSeedUrlsData();
        relevantKeywords.addAll(FileUtilsEx.readFileAllLines(relevantKeywordsFilePath));
        urlFrontier = UrlFrontier.getInstance(seedUrls, relevantKeywords);
    }

    private void validate() {
        if (!FileUtilsEx.isFileExists(this.seedUrlsFilePath))
            throw new SeedsNotFoundException("File not found - " + this.seedUrlsFilePath);

        if (!FileUtilsEx.isFileExists(this.relevantKeywordsFilePath))
            throw new RelevancyKeywordsNotFoundException("File not found - " + this.relevantKeywordsFilePath);
    }

    private void start() {
        while (this.urlFrontier.getTotalUrlsCrawledSuccessfully() < TOTAL_URLS_TO_CRAWL) {
            if (this.urlFrontier.isUrlsToCrawlEmpty()) {
                this.urlFrontier.transferNewUrls2InProcessUrlsQueue();
            }

            final UrlInfo urlCurrCrawl = this.urlFrontier.popFromInProcessUrlsQueue();
            final String normalizedCurrUrl = urlCurrCrawl.getNormalizedUrlWithProtocol();
            try {

                IUrlCrawler crawler = new UrlCrawlerImpl(urlCurrCrawl);
                crawler.crawl();
                urlFrontier.updateUrlsAlreadyCrawledMap(urlCurrCrawl);
                urlFrontier.updateUrlsToBeCrawlQueue(normalizedCurrUrl, urlCurrCrawl.getOutLinks(), urlCurrCrawl);
                this.documentsManager.serialize(urlCurrCrawl, urlFrontier.getMapUrlsToBeStartCrawling());
                String logMessage = String.format("Successfully crawled %s URL - %s",
                        this.urlFrontier.getTotalUrlsCrawledSuccessfully(),
                        normalizedCurrUrl);
                logger.info(logMessage);
            } catch (ValidationBeforeParseException validationBeforeParseException) {
                logger.error("Error received when crawling URL - " + normalizedCurrUrl);
                logger.error(validationBeforeParseException.getMessage());
                this.failedUrlsManager.serialize(new FailedUrl(normalizedCurrUrl, validationBeforeParseException.getCrawlRule()));
            } catch (ValidationAfterParseException validationAfterParseException) {
                logger.error("Error received when crawling URL - " + normalizedCurrUrl);
                logger.error(validationAfterParseException.getMessage());
                this.failedUrlsManager.serialize(new FailedUrl(normalizedCurrUrl, validationAfterParseException.getCrawlRule()));
            } catch (InterruptedException ie) {
                logger.error("Error received when crawling URL - " + normalizedCurrUrl);
                logger.error(ie.getMessage());
                this.failedUrlsManager.serialize(new FailedUrl(normalizedCurrUrl, ie.getMessage()));
            } catch (IOException io) {
                logger.error("Error received when crawling URL - " + normalizedCurrUrl);
                logger.error(io.getMessage());
                this.failedUrlsManager.serialize(new FailedUrl(normalizedCurrUrl, io.getMessage()));
            } catch (Exception e) {
                logger.error("Error received when crawling URL - " + normalizedCurrUrl);
                logger.error(e.getMessage());
                this.failedUrlsManager.serialize(new FailedUrl(normalizedCurrUrl, e.getMessage()));
            }
        }
    }

    private void postStart() {

    }

    public void trigger() throws IOException {
        this.start();
        this.postStart();
    }
}
