package webcrawler.rule.beforeparse;

import webcrawler.constants.CCrawler;
import webcrawler.model.UrlInfo;
import webcrawler.rule.ECrawlRule;
import webcrawler.rule.IRuleValidator;

public class RuleLongCrawlDelay implements IRuleValidator {
    @Override
    public boolean validate(UrlInfo urlInfo) {
        IRuleValidator.super.preValidate();

        if(urlInfo.isSeedUrl())
            return urlInfo.getDomainInfo().getCrawlDelay() <= CCrawler.SEED_URL_LONG_CRAWL_DELAY_THRESHOLD;
        else
            return urlInfo.getDomainInfo().getCrawlDelay() <= CCrawler.NORMAL_URL_LONG_CRAWL_DELAY_THRESHOLD;
    }

    @Override
    public ECrawlRule getCrawlRule() {
        return ECrawlRule.AVOID_LONG_CRAWL_DELAY;
    }
}
