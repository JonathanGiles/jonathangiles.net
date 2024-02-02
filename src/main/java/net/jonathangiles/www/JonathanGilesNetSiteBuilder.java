package net.jonathangiles.www;

import net.jonathangiles.tools.sitebuilder.SiteBuilder;
import net.jonathangiles.tools.teenyhttpd.TeenyHttpd;
import net.jonathangiles.www.pages.PostsPage;

import java.io.File;

public class JonathanGilesNetSiteBuilder extends SiteBuilder {

    public static void main(String[] args)  {
        SiteBuilder sb = new JonathanGilesNetSiteBuilder();

        sb.init();

        // register the posts page, which includes links to all posts ever written
        sb.registerContent(new PostsPage("/posts", sb.getAllPosts(), false));
        sb.registerContent(new PostsPage("/all-posts", sb.getAllPosts(), true));

        sb.run();

        if (args == null || args.length == 0) {
            return;
        }

        String command = args[0];
        if ("runserver".equals(command)) {
            // start up a TeenyHttpd server to browse the generated site
            TeenyHttpd httpd = new TeenyHttpd(8080);
            httpd.addFileRoute("/", new File("target/output"));
            httpd.start();
        }
    }
}
