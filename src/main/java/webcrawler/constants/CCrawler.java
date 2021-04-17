package webcrawler.constants;

public class CCrawler {
    public static long CRAWL_DELAY = 1000;
    public static int URL_CONNECT_TIMEOUT = 10000;

    public static int TOTAL_URLS_COMBINE_IN_MEMORY = 80000;
    public static int TOTAL_URLS_TO_CRAWL = 40000;
    public static int TOTAL_DOCUMENTS_PER_FILE = 500;
    public static int URLS_CRAWL_BATCH_SIZE = 1000;

    public static long NORMAL_URL_LONG_CRAWL_DELAY_THRESHOLD = 5000;
    public static long SEED_URL_LONG_CRAWL_DELAY_THRESHOLD = 10000;
}
