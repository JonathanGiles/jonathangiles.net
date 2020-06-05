package net.jonathangiles.www;

import net.jonathangiles.tools.sitebuilder.SiteBuilder;
import net.jonathangiles.tools.sitebuilder.models.Page;
import net.jonathangiles.www.pages.PostsPage;

import java.io.File;
import java.util.stream.Collectors;

public class JonathanGilesNetSiteBuilder extends SiteBuilder {

    public static void main(String[] args)  {
        new JonathanGilesNetSiteBuilder().run();

//        // start up a TeenyHttpd server to browse the generated site
//        TeenyHttpd httpd = new TeenyHttpd(80);
//        httpd.setWebroot(new File(("./output")));
//        httpd.start();
    }

    @Override
    protected void registerPages() {
        super.registerPages();

        // register the simple pages first
        registerPage(new Page("index")
                             .addProperty("title", "Jonathan Giles"));
        registerPage(new Page("contact")
                             .addProperty("title", "Contact | Jonathan Giles"));
        registerPage(new Page("media")
                             .addProperty("title", "Media | Jonathan Giles"));
        registerPage(new Page("presentations")
                             .addProperty("title", "Presentations | Jonathan Giles"));
        registerPage(new Page("java-api-design-best-practices")
                             .addProperty("title", "Java API Design Best Practices"));
        registerPage(new Page("projects")
                             .addProperty("title", "Projects | Jonathan Giles"));

        // and now register the posts page, which includes links to all posts ever written
        registerPage(new PostsPage("posts", getAllPosts(), false));
        registerPage(new PostsPage("all-posts", getAllPosts(), true));
    }
}
