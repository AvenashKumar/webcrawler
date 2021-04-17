package webcrawler.rule.beforeparse;

import webcrawler.model.UrlInfo;
import webcrawler.rule.ECrawlRule;
import webcrawler.rule.IRuleValidator;

import java.util.LinkedList;
import java.util.List;

public class RuleContentTypeHtmlText implements IRuleValidator {

    final List<String> acceptableContentTypes;

    public RuleContentTypeHtmlText() {
        acceptableContentTypes=new LinkedList<>();
        acceptableContentTypes.add("text/html");
        acceptableContentTypes.add("text/plain");
    }

    private boolean isProvidedContentTypeContainsAcceptableFormats(final String inputContentType) {
        final String sameCaseInputContentType = inputContentType.toLowerCase();
        for (String acceptableContentType : acceptableContentTypes) {
            if (sameCaseInputContentType.contains(acceptableContentType))
                return true;
        }
        return false;
    }

    @Override
    public boolean validate(final UrlInfo urlInfo) {
        IRuleValidator.super.preValidate();
        final String urlContentType = urlInfo.getContentType();
        final boolean isAcceptableContentType = isProvidedContentTypeContainsAcceptableFormats(urlContentType);
        return isAcceptableContentType;
    }

    @Override
    public ECrawlRule getCrawlRule() {
        return ECrawlRule.CONTENT_TYPE_TEXT_HTML;
    }
}
