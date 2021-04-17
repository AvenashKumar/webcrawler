package webcrawler.service.relevancy;

import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public class RelevancyCheckerImpl implements IRelevancyChecker {

    public RelevancyCheckerImpl() {
    }

    @Override
    public int check(final Set<String> relevantKeywords, final String url) {
        String urlText = url.toLowerCase();
        int relevantCount = 0;
        for (final String term : relevantKeywords) {
            relevantCount += StringUtils.countMatches(urlText, term);
        }

        return relevantCount;
    }
}
