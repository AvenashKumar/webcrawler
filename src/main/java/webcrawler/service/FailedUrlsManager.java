package webcrawler.service;

import webcrawler.model.FailedUrl;
import webcrawler.utils.FileUtilsEx;
import com.google.gson.Gson;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;

public class FailedUrlsManager {
    private static final Logger logger = LoggerFactory.getLogger(FailedUrlsManager.class);

    private final List<FailedUrl> failedUrls;

    private final String directoryPath;

    private final Gson gson;

    public FailedUrlsManager(final String directoryPath) throws IOException {
        this.failedUrls = new LinkedList<>();
        this.gson = new Gson();
        this.directoryPath = directoryPath;
        init();
    }

    private void init() throws IOException {
        FileUtilsEx.removeDir(this.directoryPath);
        FileUtilsEx.createDirs(directoryPath);
    }

    public void serialize(final FailedUrl failedUrl) {
        this.failedUrls.add(failedUrl);


        if (this.failedUrls.size() > 100) {
            try {
                this.flush();
                this.failedUrls.clear();
                logger.info("Flushed failed urls on disk.");
            } catch (IOException ioException) {
                logger.error("Couldn't able to flush failed urls info on disk - " + ioException.getMessage());
                ioException.printStackTrace();
            }
        }
    }


    private void flush() throws IOException {
        final String jsonString = this.gson.toJson(this.failedUrls);
        final String filePath = this.directoryPath + File.separator + FileUtilsEx.generateRandomFileName() + ".json";
        FileUtilsEx.writeFile(filePath, jsonString, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
