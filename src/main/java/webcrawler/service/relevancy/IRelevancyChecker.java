package webcrawler.service.relevancy;

import java.util.Set;

public interface IRelevancyChecker {
    int check(final Set<String> relevantKeywords, final String body);
}
