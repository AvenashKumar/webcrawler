package webcrawler;

import webcrawler.service.WebCrawlerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            if (args.length < 2) {
                logger.error("Please provide resource directory and seed urls file (in order).");
                return;
            }

            final String resourceDir = args[0];
            final String seedUrlsFileName = args[1];

            WebCrawlerController webCrawlerController = new WebCrawlerController(resourceDir, seedUrlsFileName);
            webCrawlerController.trigger();

        } catch (IOException io) {
            logger.error("IOException received: " + io.getMessage());
            io.printStackTrace();
        } catch (Exception e) {
            logger.error("General Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
