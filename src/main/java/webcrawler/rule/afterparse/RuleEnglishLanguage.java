package webcrawler.rule.afterparse;

import webcrawler.model.UrlInfo;
import webcrawler.rule.ECrawlRule;
import webcrawler.rule.IRuleValidator;
import org.apache.tika.language.LanguageIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleEnglishLanguage implements IRuleValidator {
    private static final Logger logger = LoggerFactory.getLogger(RuleEnglishLanguage.class);

    @Override
    public boolean validate(final UrlInfo urlInfo) {
        IRuleValidator.super.preValidate();
        final String body = urlInfo.getBodyText();
        LanguageIdentifier identifier = new LanguageIdentifier(body);
        String language = identifier.getLanguage().toLowerCase();
        return language.equals("en");
    }

    @Override
    public ECrawlRule getCrawlRule() {
        return ECrawlRule.ENGLISH_LANG;
    }
}
