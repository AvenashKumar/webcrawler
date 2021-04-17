package webcrawler.rule;

import webcrawler.model.UrlInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface IRuleValidator {
    Logger logger = LoggerFactory.getLogger(IRuleValidator.class);

    boolean validate(final UrlInfo urlInfo);

    default void preValidate(){
        logger.debug("Validating rule: " + getCrawlRule().name());
    }
    ECrawlRule getCrawlRule();

}
