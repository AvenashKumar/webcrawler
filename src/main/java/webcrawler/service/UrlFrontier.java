package webcrawler.service;

import webcrawler.exception.InProcessUrlQueueEmptyException;
import webcrawler.model.ParsedUrlEx;
import webcrawler.model.UrlInfo;
import webcrawler.service.normalization.CanonicalizationImpl;
import webcrawler.service.normalization.ICanonicalization;
import webcrawler.service.relevancy.IRelevancyChecker;
import webcrawler.service.relevancy.RelevancyCheckerImpl;
import webcrawler.service.weight.IWeightCalculator;
import webcrawler.service.weight.WeightCalculatorImpl;

import java.util.*;

import static webcrawler.constants.CCrawler.TOTAL_URLS_COMBINE_IN_MEMORY;
import static webcrawler.constants.CCrawler.URLS_CRAWL_BATCH_SIZE;

public class UrlFrontier {
    private static UrlFrontier urlFrontier;

    private static final IWeightCalculator weightCalculator = new WeightCalculatorImpl();

    private final Set<String> relevantKeywords;

    private static final IRelevancyChecker relevancyChecker = new RelevancyCheckerImpl();

    //Initially this will contains seed URLs, after that will holds outlinks
    private final Map<String, UrlInfo> mapUrlsToBeStartCrawling;

    //Initially this will be empty
    private final Queue<UrlInfo> urlsCrawlingInProcess;

    //This map is responsible for storing URLs which are already crawled (this is for updating thinks like inLink latter)
    //Key=>Normalized Url, Value=>UrlInfo
    //This also doesn't contains any url with protocol i.e. http/https
    private final Map<String, UrlInfo> mapUrlsAlreadyCrawled;

    private static final ICanonicalization urlNormalizer = new CanonicalizationImpl();


    private UrlFrontier(final Map<String, Integer> seedUrls, final Set<String> relevantKeywords) {
        this.urlsCrawlingInProcess = new PriorityQueue<>();
        this.mapUrlsAlreadyCrawled = new HashMap<>();
        this.mapUrlsToBeStartCrawling = new LinkedHashMap<>();
        this.relevantKeywords = relevantKeywords;
        this.addSeedUrlsInToBeCrawlQueue(seedUrls);
    }

    public static UrlFrontier getInstance(final Map<String, Integer> seedUrls, final Set<String> relevantKeywords) {
        if (urlFrontier == null)
            urlFrontier = new UrlFrontier(seedUrls, relevantKeywords);

        return urlFrontier;
    }

    private void updateInLinksAndCalculateWeight(final UrlInfo urlInfo){
        final Set<String> outLinks = urlInfo.getOutLinks();

        for(final String outLink:outLinks){
            ParsedUrlEx parsedUrlEx = urlNormalizer.canonicalize(null, outLink);
            final String normalizedOutLink = parsedUrlEx.getUrlWithoutProtocol();

            //Check in ALREADY CRAWLED links.
            boolean isAnyOutLinkAlreadyCrawled = mapUrlsAlreadyCrawled.containsKey(normalizedOutLink);
            if(isAnyOutLinkAlreadyCrawled){
                final UrlInfo alreadyCrawledUrlInfo = mapUrlsAlreadyCrawled.get(normalizedOutLink);
                alreadyCrawledUrlInfo.addInLink(urlInfo.getNormalizedUrlWithProtocol());
            }

            //Check in TOBE CRAWLED map.
            boolean isAnyOutLinkExistsInToBeStartCrawlMap = mapUrlsToBeStartCrawling.containsKey(normalizedOutLink);
            if(isAnyOutLinkExistsInToBeStartCrawlMap){
                final UrlInfo toBeCrawlUrlInfo = mapUrlsToBeStartCrawling.get(normalizedOutLink);
                toBeCrawlUrlInfo.addInLink(urlInfo.getNormalizedUrlWithProtocol());

                final int relevancyCountUrl = relevancyChecker.check(relevantKeywords, urlInfo.getNormalizedUrlWithoutProtocol());
                urlInfo.setRelevancyCountUrl(relevancyCountUrl);

                final int relevancyCountTitle = relevancyChecker.check(relevantKeywords, urlInfo.getTitle());
                urlInfo.setRelevancyCountUrl(relevancyCountTitle);

                final double weight = weightCalculator.calculate(urlInfo);
                toBeCrawlUrlInfo.setWeight(weight);
            }
        }
    }

    private void updateUrlsToBeCrawlQueue(final String parentUrl,
                                          final String relativeOrAbsUrl,
                                          final UrlInfo sourceUrlInfo,
                                          final Integer providedDepth) {
        UrlInfo urlInfo = new UrlInfo(parentUrl, relativeOrAbsUrl, sourceUrlInfo);


        int depth;
        if (providedDepth != null) {
            depth = providedDepth;
        } else {
            depth = 0;
            if (sourceUrlInfo != null)
                depth = sourceUrlInfo.getDepth() + 1;
        }

        //Update depth/wave of the URL
        urlInfo.setDepth(depth);

        //Update visited flag
        final String normalizedUrl = urlInfo.getNormalizedUrlWithoutProtocol();
        if (this.mapUrlsAlreadyCrawled.containsKey(normalizedUrl))
            return;

        final int totalUrlsInMemory = this.mapUrlsAlreadyCrawled.size() + this.mapUrlsToBeStartCrawling.size();
        if (totalUrlsInMemory >= TOTAL_URLS_COMBINE_IN_MEMORY)
            return;

        this.mapUrlsToBeStartCrawling.put(normalizedUrl, urlInfo);
    }


    public void updateUrlsToBeCrawlQueue(final String parentUrl, final Set<String> urlsToBeCrawled, final UrlInfo sourceUrlInfo) {
        for (final String urlToBeCrawled : urlsToBeCrawled) {
            updateUrlsToBeCrawlQueue(parentUrl, urlToBeCrawled, sourceUrlInfo, null);
        }
    }

    public void updateUrlsToBeCrawlQueue(final String parentUrl, final Map<String, Integer> urlsToBeCrawled, final UrlInfo sourceUrlInfo) {
        for (final Map.Entry<String, Integer> urlWithDepth : urlsToBeCrawled.entrySet()) {
            updateUrlsToBeCrawlQueue(parentUrl, urlWithDepth.getKey(), sourceUrlInfo, urlWithDepth.getValue());
        }
    }

    private void addSeedUrlsInToBeCrawlQueue(final Map<String, Integer> urlsToBeCrawled){
        //Seed URL will not have any parent urls, nor they will have any sourceInfo.
        updateUrlsToBeCrawlQueue(null, urlsToBeCrawled, null);
    }

    public void updateUrlsAlreadyCrawledMap(final UrlInfo urlInfo) {
        urlInfo.setVisited(true);
        updateInLinksAndCalculateWeight(urlInfo);
        this.mapUrlsAlreadyCrawled.put(urlInfo.getNormalizedUrlWithoutProtocol(), urlInfo);
    }

    public boolean isUrlsToCrawlEmpty(){
        return this.urlsCrawlingInProcess.isEmpty();
    }

    public int getTotalUrlsCrawledSuccessfully(){
        return this.mapUrlsAlreadyCrawled.size();
    }

    public void transferNewUrls2InProcessUrlsQueue(){
        int totalTransfers = 0;
        Iterator<Map.Entry<String,UrlInfo>> iter = mapUrlsToBeStartCrawling.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String,UrlInfo> entry = iter.next();
            this.urlsCrawlingInProcess.add(entry.getValue());
            iter.remove();
            ++totalTransfers;

            if(totalTransfers >= URLS_CRAWL_BATCH_SIZE){
                break;
            }
        }
    }

    public UrlInfo popFromInProcessUrlsQueue(){
        if(this.urlsCrawlingInProcess.isEmpty()){
            throw new InProcessUrlQueueEmptyException("In Process URL queue is empty.");
        }

        return this.urlsCrawlingInProcess.poll();
    }

    public Queue<UrlInfo> getUrlsCrawlingInProcess() {
        return urlsCrawlingInProcess;
    }

    public Map<String, UrlInfo> getMapUrlsAlreadyCrawled() {
        return mapUrlsAlreadyCrawled;
    }

    public Collection<UrlInfo> getMapUrlsToBeStartCrawling() {
        return mapUrlsToBeStartCrawling.values();
    }
}
