package webcrawler.rule;

import webcrawler.exception.ValidationAfterParseException;
import webcrawler.exception.ValidationBeforeParseException;
import webcrawler.model.UrlInfo;
import webcrawler.rule.afterparse.RuleEnglishLanguage;
import webcrawler.rule.afterparse.RuleNullEmptyBody;
import webcrawler.rule.beforeparse.RuleAlreadyVisited;
import webcrawler.rule.beforeparse.RuleContentTypeHtmlText;
import webcrawler.rule.beforeparse.RuleLongCrawlDelay;
import webcrawler.rule.beforeparse.RuleRedirectUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class RuleValidateExecutor {
    private static final Logger logger = LoggerFactory.getLogger(RuleValidateExecutor.class);

    private final List<IRuleValidator> beforeContentParseValidators;
    private final List<IRuleValidator> afterContentParseValidators;

    public RuleValidateExecutor() {
        beforeContentParseValidators = new LinkedList<>();
        beforeContentParseValidators.add(new RuleAlreadyVisited());
        beforeContentParseValidators.add(new RuleContentTypeHtmlText());
        beforeContentParseValidators.add(new RuleLongCrawlDelay());
        beforeContentParseValidators.add(new RuleRedirectUrl());

        afterContentParseValidators = new LinkedList<>();
        afterContentParseValidators.add(new RuleNullEmptyBody());
        afterContentParseValidators.add(new RuleEnglishLanguage());
    }

    public void validateBeforeContentParse(final UrlInfo urlInfo) throws ValidationBeforeParseException {
        ECrawlRule crawlRule = validate(urlInfo, beforeContentParseValidators);
        if (crawlRule != null) {
            final String message = String.format("Craw rule validation %s failed for URL: %s ",
                    crawlRule,
                    urlInfo.getNormalizedUrlWithProtocol());
            throw new ValidationBeforeParseException(crawlRule, message);
        }
    }

    public void validateAfterContentParse(final UrlInfo urlInfo) throws ValidationAfterParseException {
        ECrawlRule crawlRule = validate(urlInfo, afterContentParseValidators);
        if (crawlRule != null) {
            final String message = String.format("Craw rule validation %s failed for URL: %s ",
                    crawlRule,
                    urlInfo.getNormalizedUrlWithProtocol());
            throw new ValidationAfterParseException(crawlRule, message);
        }
    }

    private ECrawlRule validate(final UrlInfo urlInfo,
                                final List<IRuleValidator> ruleValidators) {
        for (IRuleValidator ruleValidator : ruleValidators) {
            if (!ruleValidator.validate(urlInfo)) {
                return ruleValidator.getCrawlRule();
            }
        }
        return null;
    }
}
