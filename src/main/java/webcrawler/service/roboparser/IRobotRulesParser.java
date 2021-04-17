package webcrawler.service.roboparser;

import webcrawler.constants.CRoboParser;
import webcrawler.exception.HttpConnectionException;
import webcrawler.exception.InvalidUrlException;
import crawlercommons.robots.BaseRobotRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public interface IRobotRulesParser {
    Logger logger = LoggerFactory.getLogger(IRobotRulesParser.class);

    BaseRobotRules parse(final String url);

    default URLConnection openConnection(final String url){
        int retryCount=0;
        while(retryCount < CRoboParser.RETRY_LIMIT){
            try {
                return new URL(url).openConnection();
            } catch (MalformedURLException malformedURLException){
                logger.error("Malformed URL: ", url);
                logger.error(malformedURLException.getMessage());
                throw new InvalidUrlException(malformedURLException.getMessage());
            } catch (IOException ioException) {
                logger.error(ioException.getMessage());
                logger.info("Retry count - " + retryCount);
                ioException.printStackTrace();
            }
            ++retryCount;
        }

        throw new HttpConnectionException("[RoboRulesParser]: Unable to parse - " + url);
    }
}
