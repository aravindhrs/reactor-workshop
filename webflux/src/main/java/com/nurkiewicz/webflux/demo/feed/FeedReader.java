package com.nurkiewicz.webflux.demo.feed;

import com.google.common.io.CharStreams;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class FeedReader {

    public List<SyndEntry> fetch(URL url) throws IOException, FeedException, ParserConfigurationException, SAXException, URISyntaxException {
        final String feedBody = get(url);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(feedBody.getBytes(UTF_8)));
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(doc);
        return feed.getEntries();
    }

    /**
     *
     * Load data asynchronously using {@link org.springframework.web.reactive.function.client.WebClient}
     */
    private static String get(URL url) throws IOException {
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (conn.getResponseCode() == HttpStatus.MOVED_PERMANENTLY.value()) {
            return get(new URL(conn.getHeaderField("Location")));
        }
        try (final InputStreamReader reader = new InputStreamReader(conn.getInputStream(), UTF_8)) {
            return CharStreams.toString(reader);
        }
    }


}
