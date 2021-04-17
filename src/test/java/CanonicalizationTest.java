import webcrawler.model.ParsedUrlEx;
import webcrawler.service.normalization.CanonicalizationImpl;
import webcrawler.service.normalization.ICanonicalization;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class CanonicalizationTest {

    private ICanonicalization canonicalization = new CanonicalizationImpl();

    @Test
    public void test1(){
        final String inputUrl = "HTTP://www.Example.com/SomeFile.html";
        final ParsedUrlEx parsedUrl = canonicalization.canonicalize(null, inputUrl);
        final String expected = "http://www.example.com/SomeFile.html";
        final String actual = parsedUrl.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void test2(){
        final String inputUrl = "http://www.example.com:80";
        final ParsedUrlEx parsedUrl = canonicalization.canonicalize(null, inputUrl);
        final String expected = "http://www.example.com/";
        final String actual = parsedUrl.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void test3(){
        final String inputUrl = "http://www.example.com/a.html#anything";
        final ParsedUrlEx parsedUrl = canonicalization.canonicalize(null, inputUrl);
        final String expected = "http://www.example.com/a.html";
        final String actual = parsedUrl.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void test4(){
        final String inputUrl = "  http://www.example.com//a.html  ";
        final ParsedUrlEx parsedUrl = canonicalization.canonicalize(null, inputUrl);
        final String expected = "http://www.example.com/a.html";
        final String actual = parsedUrl.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void test5(){
        final String inputUrl = "http://www.example.com/a/../c.html";
        final ParsedUrlEx parsedUrl = canonicalization.canonicalize(null, inputUrl);
        final String expected = "http://www.example.com/c.html";
        final String actual = parsedUrl.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void test6(){
        final String inputUrl = "../c.html";
        final ParsedUrlEx parsedUrl = canonicalization.canonicalize(null, inputUrl);
        assertEquals("0.0.0.0", parsedUrl.getHost());
    }

    @Test
    public void test7(){
        final String inputUrl = "http://www.example.com/a/b.html";
        final ParsedUrlEx parsedUrl = canonicalization.canonicalize(null, inputUrl);
        assertEquals(inputUrl, parsedUrl.toString());
    }

    @Test
    public void test8(){
        final ParsedUrlEx parsedUrlEx = canonicalization.canonicalize("http://www.example.com/a/b.html", "../c.html");
        assertEquals("http://www.example.com/c.html", parsedUrlEx.toString());
    }

    @Test
    public void test10(){
        final String inputBaseUrl = "http://www.example.com/a/b.html";
        final String relativeUrl = "../c.html";

        final ParsedUrlEx parsedUrlEx = canonicalization.canonicalize(inputBaseUrl, relativeUrl);
        assertEquals("http://www.example.com/c.html", parsedUrlEx.toString());
    }

    @Test
    public void test9(){
        /*final String inputUrl = "https://s3.amazonaws.com/spectrumnews-web-assets/wp-content/uploads/2018/11/13154625/20181112-SHANK3monkey-844.jpg";
        ParsedUrl parsedUrl = ParsedUrl.parseUrl(inputUrl);
        Canonicalizer.SEMANTIC.canonicalize(parsedUrl);

        try {
            HTMLFields htmlFields = IndexFiles.getHTMLSource(inputUrl);
            System.out.println(htmlFields);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        assertEquals(inputUrl, parsedUrl.toString());*/
    }
}
