package webcrawler.rule.beforeparse;

import webcrawler.model.UrlInfo;
import webcrawler.rule.ECrawlRule;
import webcrawler.rule.IRuleValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleAlreadyVisited implements IRuleValidator {
    private static final Logger logger = LoggerFactory.getLogger(RuleAlreadyVisited.class);


    @Override
    public boolean validate(final UrlInfo urlInfo) {
        IRuleValidator.super.preValidate();
        return !urlInfo.isVisited();
    }

    @Override
    public ECrawlRule getCrawlRule() {
        return ECrawlRule.AVOID_ALREADY_VISITED_URL;
    }
}
