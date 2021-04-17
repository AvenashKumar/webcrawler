package webcrawler.service.normalization;

import webcrawler.model.ParsedUrlEx;
import org.netpreserve.urlcanon.Canonicalizer;
import org.netpreserve.urlcanon.ParsedUrl;

public class CanonicalizationImpl implements ICanonicalization{
    @Override
    public ParsedUrlEx canonicalize(final String parentUrl, final String relativeOrAbsUrl) {
        /*if (relativeOrAbsUrl == null && parentUrl != null) {
            return canonicalize(parentUrl);
        }

        if (parentUrl == null && relativeOrAbsUrl != null) {
            return canonicalize(relativeOrAbsUrl);
        }*/

        if (parentUrl == null) {
            return canonicalize(relativeOrAbsUrl);
        }

        final ParsedUrl parsedBaseUrl = ParsedUrl.parseUrl(parentUrl);
        final ParsedUrl parsedRelativeUrl = ParsedUrl.parseUrl(relativeOrAbsUrl);
        final ParsedUrl resolvedUrl = parsedBaseUrl.resolve(parsedRelativeUrl);
        return canonicalize(resolvedUrl.toString());
    }

    private ParsedUrlEx canonicalize(final String inputUrl) {
        final ParsedUrl parsedUrl = ParsedUrl.parseUrl(inputUrl);
        Canonicalizer.SEMANTIC.canonicalize(parsedUrl);
        return new ParsedUrlEx(parsedUrl);
    }

    private boolean isRelativeUrl(final ParsedUrlEx parsedUrl){
        return parsedUrl.getHost().equals("0.0.0.0");
    }
}
