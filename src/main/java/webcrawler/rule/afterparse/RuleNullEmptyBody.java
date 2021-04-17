package webcrawler.rule.afterparse;

import webcrawler.model.UrlInfo;
import webcrawler.rule.ECrawlRule;
import webcrawler.rule.IRuleValidator;


public class RuleNullEmptyBody implements IRuleValidator {
    @Override
    public boolean validate(UrlInfo urlInfo) {
        IRuleValidator.super.preValidate();

        if (urlInfo.getBodyText() == null || urlInfo.getBodyText().trim().isEmpty())
            return false;

        return true;
    }

    @Override
    public ECrawlRule getCrawlRule() {
        return ECrawlRule.AVOID_NULL_EMPTY_BODY_CONTENT;
    }
}
