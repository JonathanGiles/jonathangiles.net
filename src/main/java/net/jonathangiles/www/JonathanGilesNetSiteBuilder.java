package net.jonathangiles.www;

import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;
import net.jonathangiles.tools.sitebuilder.SiteBuilder;
import net.jonathangiles.tools.sitebuilder.models.SiteContent;
import net.jonathangiles.tools.teenyhttpd.TeenyHttpd;
import net.jonathangiles.www.pages.PostsPage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class JonathanGilesNetSiteBuilder extends SiteBuilder {
    private static final String OUTPUT_DIR = "target/output";
    private static final String RSS_AUTHOR = "Jonathan Giles";
    private static final String RSS_BASE_URL = "https://www.jonathangiles.net";

    public static void main(String[] args) {
        SiteBuilder sb = new JonathanGilesNetSiteBuilder();

        sb.init();

        // register the posts page, which includes links to all posts ever written
        sb.registerContent(new PostsPage("/posts", sb.getAllPosts(), false));
        sb.registerContent(new PostsPage("/all-posts", sb.getAllPosts(), true));

        try {
            // create RSS feed
            createRssFeed(sb);
        } catch (IOException | FeedException e) {
            e.printStackTrace();
        }

        sb.run();

        if (args == null || args.length == 0) {
            return;
        }

        String command = args[0];
        if ("runserver".equals(command)) {
            // start up a TeenyHttpd server to browse the generated site
            TeenyHttpd httpd = new TeenyHttpd(8080);
            httpd.addFileRoute("/", new File(OUTPUT_DIR));
            httpd.start();
        }
    }

    private static void createRssFeed(SiteBuilder sb) throws IOException, FeedException {
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_1.0");
        feed.setTitle(RSS_AUTHOR);
        feed.setLink(RSS_BASE_URL);
        feed.setDescription("Personal website of Jonathan Giles");

        SyndImage image = new SyndImageImpl();
        image.setUrl(RSS_BASE_URL + "/images/bio/jonathan-headshot-lowquality.jpg");
        image.setDescription("Headshot of Jonathan Giles");
        image.setTitle(RSS_AUTHOR);
        image.setLink(RSS_BASE_URL);
        feed.setImage(image);

        sb.getAllPosts().stream()
            .sorted(Comparator.comparing(SiteContent::getDate).reversed())
            .limit(10)
            .forEach(post -> {
                SyndEntry entry = new SyndEntryImpl();
                entry.setTitle(post.getTitle());
                entry.setAuthor(RSS_AUTHOR);
                entry.setLink(RSS_BASE_URL + "/" + post.getRelativePath());
                entry.setPublishedDate(Date.from(post.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                entry.setUri(RSS_BASE_URL + "/" + post.getRelativePath());

                // create a summary of the post, by taking the first 200 words
                SyndContent description = new SyndContentImpl();
                description.setType("text/html");
                description.setValue(getSummary(post.getContent(), 200));
                entry.setDescription(description);

                feed.getEntries().add(entry);
            });

        File outputFile = new File(OUTPUT_DIR + "/rss.xml");
        outputFile.getParentFile().mkdirs();

        Writer writer = new FileWriter(outputFile);
        SyndFeedOutput syndFeedOutput = new SyndFeedOutput();
        syndFeedOutput.output(feed, writer);
        writer.close();
    }

    private static String getSummary(String text, int length) {
        String[] words = text.split(" ");
        if (words.length > 200) {
            return String.join(" ", Arrays.copyOfRange(words, 0, 200));
        } else {
            return text;
        }
    }
}
