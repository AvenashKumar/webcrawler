package webcrawler.service.crawler;

import java.io.IOException;

public interface IUrlCrawler {
    void crawl() throws InterruptedException, IOException;
}
