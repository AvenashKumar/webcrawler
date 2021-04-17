package webcrawler.service.roboparser;

import webcrawler.constants.CRoboParser;
import webcrawler.exception.InvalidUrlException;
import webcrawler.exception.RoboParserException;
import webcrawler.service.normalization.CanonicalizationImpl;
import webcrawler.service.normalization.ICanonicalization;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;

public class RobotRulesParserImpl implements IRobotRulesParser {
    private final ICanonicalization urlNormalizer;
    private final SimpleRobotRulesParser simpleRobotRulesParser;
    private final static Logger logger = LoggerFactory.getLogger(RobotRulesParserImpl.class);

    public RobotRulesParserImpl() {
        this.urlNormalizer = new CanonicalizationImpl();
        this.simpleRobotRulesParser = new SimpleRobotRulesParser();
    }

    @Override
    public BaseRobotRules parse(String robotUrl) {
        logger.debug("Start: Parsing - " + robotUrl);

        URLConnection connection = openConnection(robotUrl);
        try {
            byte[] content = IOUtils.toByteArray(connection);
            if (!robotUrl.matches("^https?://")) {
                // use artificial URL to avoid problems resolving relative
                // sitemap paths for file:/ URLs
                robotUrl = "http://example.com/robots.txt";
            }
            return simpleRobotRulesParser.parseContent(robotUrl, content, "text/plain", CRoboParser.AGENT_NAME);
        } catch (IOException e) {
            if (connection instanceof HttpURLConnection) {
                int code;
                try {
                    code = ((HttpURLConnection) connection).getResponseCode();
                } catch (IOException ioException) {
                    logger.error("Failed to get http response code due to invalid response");
                    ioException.printStackTrace();
                    throw new InvalidUrlException(ioException.getMessage());
                }
                logger.error("Fetch of " + robotUrl + " failed with HTTP status code " + code);
                return simpleRobotRulesParser.failedFetch(code);
            } else {
                throw new RoboParserException(e.getMessage());
            }
        }
    }
}
