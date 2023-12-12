package net.jonathangiles.www.pages;

import net.jonathangiles.tools.sitebuilder.models.Page;
import net.jonathangiles.tools.sitebuilder.models.Post;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class PostsPage extends Page {

    public PostsPage(String slug, Set<Post> allPosts, boolean includeDesktopLinks) {
        final StringBuilder allPostsHtml = new StringBuilder();

        allPostsHtml.append("<div class=\"alert alert-info\" role=\"alert\">");
        if (includeDesktopLinks) {
            allPostsHtml.append("Don't want to see all of the Java desktop links posts? Go <a href=\"posts.html\">here</a>.");
        } else {
            allPostsHtml.append("Looking for the Java desktop links posts? They're <a href=\"all-posts.html\">here</a>.");
        }
        allPostsHtml.append("</div>\n\n");

        // group posts into years
        Map<Integer, List<Post>> postsPerYear = allPosts
             .stream()
             .filter(post -> includeDesktopLinks || !post.getTitle().contains("links of the"))
             .collect(Collectors.groupingBy(post -> post.getDate().getYear()));

        // sort the years so most recent are at the top
        SortedSet<Integer> sortedYears = new TreeSet<>(Comparator.comparing(Integer::intValue).reversed());
        sortedYears.addAll(postsPerYear.keySet());

        for (Integer year : sortedYears) {
            allPostsHtml.append("<h3>").append(year).append("</h3>");
            postsPerYear.get(year).forEach(post -> {
                String path = post.getRelativePath();//.getParent().toString();

                // FIXME hacky
                // strip out the 'output/' from the path
                //path = path.substring(path.indexOf("/") + 1);

                allPostsHtml.append(post.getDate())
                    .append(": ")
                    .append("<a href=\"/")
                    .append(path)
                    .append("/\">")
                    .append(post.getTitle())
                    .append("</a><br/>\n");
            });
            allPostsHtml.append("<br/>\n\n");
        }

        setSlug(slug);
        setTitle("Posts");
        setTemplate("page");
        getProperties().put("content", allPostsHtml.toString());
    }
}
