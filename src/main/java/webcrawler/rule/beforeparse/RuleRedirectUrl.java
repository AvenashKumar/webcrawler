package webcrawler.rule.beforeparse;

import webcrawler.model.UrlInfo;
import webcrawler.rule.ECrawlRule;
import webcrawler.rule.IRuleValidator;

public class RuleRedirectUrl implements IRuleValidator {
    @Override
    public boolean validate(final UrlInfo urlInfo) {
        IRuleValidator.super.preValidate();
        return !urlInfo.isRedirect();
    }

    @Override
    public ECrawlRule getCrawlRule() {
        return ECrawlRule.AVOID_REDIRECT_URL;
    }
}
