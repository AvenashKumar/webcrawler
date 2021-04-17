package webcrawler.service;

import com.google.gson.Gson;
import webcrawler.model.UrlInfo;
import webcrawler.utils.FileUtilsEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

import static webcrawler.constants.CCrawler.TOTAL_DOCUMENTS_PER_FILE;

public class DocumentsManager {
    private static final Logger logger = LoggerFactory.getLogger(DocumentsManager.class);

    private final List<String> urlsInfoInXmlFormat;

    private final String directoryPath;

    private final String docDirPath;

    private int fileCount ;

    private Gson gson;

    public DocumentsManager(final String directoryPath) throws IOException {
        this.urlsInfoInXmlFormat = new LinkedList<>();
        this.directoryPath = directoryPath;
        this.docDirPath = directoryPath + File.separator + FileUtilsEx.generateRandomFileName();
        this.fileCount = 1;
        this.gson = new Gson();
        init();
    }

    private void init() {
        logger.info("Creating new directory: " + docDirPath);
        FileUtilsEx.createDirs(docDirPath);
    }

    public void serialize(final UrlInfo urlInfo, final Collection<UrlInfo> urlsToBeCrawl) {
        final String urlInfoInXmlFormat = composeDocumentText(urlInfo);
        this.urlsInfoInXmlFormat.add(urlInfoInXmlFormat);

        if (this.urlsInfoInXmlFormat.size() > TOTAL_DOCUMENTS_PER_FILE) {
            try {
                StringBuilder allDocumentText = new StringBuilder();
                for(final String urlInfoXml : this.urlsInfoInXmlFormat){
                    allDocumentText.append(urlInfoXml);
                }
                this.flush(allDocumentText.toString());
                this.flushToBeCrawledUrls(urlsToBeCrawl);
                this.urlsInfoInXmlFormat.clear();
                logger.info("Flushed documents on disk.");
            } catch (IOException ioException) {
                logger.error("Couldn't able to flush failed document text on disk - " + ioException.getMessage());
                ioException.printStackTrace();
            }
        }
    }

    private void flushToBeCrawledUrls(final Collection<UrlInfo> queueUrlsToBeCrawl) throws IOException {
        Map<String, Integer> mapUrlsToBeCrawlWithDepth = new HashMap<>();
        for(UrlInfo urlInfo: queueUrlsToBeCrawl){
            final String normalizedUrl = urlInfo.getNormalizedUrlWithProtocol();
            final int depth = urlInfo.getDepth();
            mapUrlsToBeCrawlWithDepth.put(normalizedUrl, depth);
        }
        final String jsonString = this.gson.toJson(mapUrlsToBeCrawlWithDepth);
        final String parentDir = new File(this.directoryPath).getParent();
        final String filePath = parentDir + File.separator + "urls-to-be-crawl.json";
        FileUtilsEx.writeFile(filePath, jsonString);
    }

    private static String composeDocumentText(final UrlInfo urlInfo) {

        StringBuilder docText = new StringBuilder();

        docText.append("<DOC>");
        docText.append(System.lineSeparator());

        docText.append("<DOCNO>");
        docText.append(urlInfo.getNormalizedUrlWithProtocol());
        docText.append("</DOCNO>");
        docText.append(System.lineSeparator());

        docText.append("<URL>");
        docText.append(urlInfo.getRelativeOrAbsUrl());
        docText.append("</URL>");
        docText.append(System.lineSeparator());

        docText.append("<TITLE>");
        docText.append(urlInfo.getTitle());
        docText.append("</TITLE>");
        docText.append(System.lineSeparator());

        docText.append("<TEXT>");
        docText.append(urlInfo.getBodyText());
        docText.append("</TEXT>");
        docText.append(System.lineSeparator());

        docText.append("<OUTLINKS>");
        docText.append(urlInfo.getOutLinks().stream()
                .collect(Collectors.joining(";\n")));
        docText.append("</OUTLINKS>");
        docText.append(System.lineSeparator());

        docText.append("<INLINKS>");
        docText.append(urlInfo.getInLinks().stream()
                .collect(Collectors.joining(";\n")));
        docText.append("</INLINKS>");
        docText.append(System.lineSeparator());

        docText.append("<DEPTH>");
        docText.append(urlInfo.getDepth());
        docText.append("</DEPTH>");
        docText.append(System.lineSeparator());

        docText.append("<RC>");
        docText.append(urlInfo.getRelevancyCountUrl());
        docText.append("</RC>");
        docText.append(System.lineSeparator());

        docText.append("</DOC>");
        docText.append(System.lineSeparator());

        return docText.toString();
    }

    private void flush(final String allDocumentsText) throws IOException {
        final String fileName = String.format("DOC_%04d.txt", fileCount++);
        final String filePath = this.docDirPath + File.separator + fileName;
        FileUtilsEx.writeFile(filePath, allDocumentsText, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
