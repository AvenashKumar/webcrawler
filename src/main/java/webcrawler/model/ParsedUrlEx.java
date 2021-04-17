package webcrawler.model;

import org.netpreserve.urlcanon.ParsedUrl;

public class ParsedUrlEx extends ParsedUrl {
    private final String protocolDomain;
    private final String urlWithoutProtocol;

    public ParsedUrlEx(ParsedUrl parsedUrl) {
        super(parsedUrl);
        protocolDomain = parsedUrl.getScheme() + parsedUrl.getColonAfterScheme() + parsedUrl.getSlashes() + parsedUrl.getHost();
        urlWithoutProtocol = parsedUrl.getHost() + parsedUrl.getPath();
    }

    public String getProtocolDomain() {
        return protocolDomain;
    }

    public String getUrlWithoutProtocol(){
        return urlWithoutProtocol;
    }
}
