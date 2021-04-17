package webcrawler.model;

import webcrawler.service.normalization.CanonicalizationImpl;
import webcrawler.service.normalization.ICanonicalization;

import java.util.*;

/**
 * This class is responsible for holding information related to URL.
 * This class will also implicitly 'normalize' provided url.
 */
public class UrlInfo implements Comparable<UrlInfo>{
    private static final ICanonicalization urlNormalizer = new CanonicalizationImpl();

    private DomainInfo domainInfo;
    private final boolean isSeedUrl;
    private double weight;
    private int depth;
    private boolean isVisited;
    private final Set<String> outLinks;
    private final Set<String> inLinks;
    private final String relativeOrAbsUrl;
    private final String parentUrl;
    private ParsedUrlEx parsedUrlEx;
    private String title;
    private String bodyText;
    private long relevancyCountUrl;
    private long relevancyCountTitle;
    private String contentType;
    private boolean isRedirect;

    // This field will have information for the source URL.
    // e.g. www.wikipedia.com/independence => www.xyz.com/america,
    // it will contain information for www.wikipedia.com/independence.
    private UrlInfo sourceUrlInfo;


    public UrlInfo(final String parentUrl, final String relativeOrAbsUrl, final UrlInfo sourceUrlInfo) {
        this.relativeOrAbsUrl = relativeOrAbsUrl;
        this.parentUrl = parentUrl;
        this.sourceUrlInfo = sourceUrlInfo;
        this.isSeedUrl = this.sourceUrlInfo == null;
        this.outLinks = new HashSet<>();
        this.inLinks = new HashSet<>();
        init();
    }

    private void init(){
        final ParsedUrlEx parsedUrlEx = urlNormalizer.canonicalize(parentUrl, relativeOrAbsUrl);
        this.setParsedUrlEx(parsedUrlEx);
    }

    public DomainInfo getDomainInfo() {
        return domainInfo;
    }

    public void setDomainInfo(DomainInfo domainInfo) {
        this.domainInfo = domainInfo;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }

    public void addOutLink(final String outLink){
        this.outLinks.add(outLink);
    }

    public void addInLink(final String inLink){
        this.inLinks.add(inLink);
    }

    public Set<String> getOutLinks() {
        return outLinks;
    }

    public Set<String> getInLinks() {
        return inLinks;
    }

    public String getRelativeOrAbsUrl() {
        return relativeOrAbsUrl;
    }

    public ParsedUrlEx getParsedUrlEx() {
        return parsedUrlEx;
    }

    public void setParsedUrlEx(ParsedUrlEx parsedUrlEx) {
        this.parsedUrlEx = parsedUrlEx;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public long getRelevancyCountUrl() {
        return relevancyCountUrl;
    }

    public void setRelevancyCountUrl(long relevancyCountUrl) {
        this.relevancyCountUrl = relevancyCountUrl;
    }

    public long getRelevancyCountTitle() {
        return relevancyCountTitle;
    }

    public void setRelevancyCountTitle(long relevancyCountTitle) {
        this.relevancyCountTitle = relevancyCountTitle;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isRedirect() {
        return isRedirect;
    }

    public void setRedirect(boolean redirect) {
        isRedirect = redirect;
    }

    public String getNormalizedUrlWithProtocol(){
        return this.parsedUrlEx.toString();
    }

    public String getNormalizedUrlWithoutProtocol(){
        return this.parsedUrlEx.getUrlWithoutProtocol();
    }

    @Override
    public int compareTo(UrlInfo o) {
        return Double.compare(this.weight, o.weight);
    }

    public boolean isSeedUrl() {
        return isSeedUrl;
    }


}
