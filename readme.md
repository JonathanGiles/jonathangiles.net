# JonathanGiles.net

This GitHub repository contains the files used to generate [JonathanGiles.net](http://jonathangiles.net). It is a collection of pages, posts, templates, and static content such as images, css, JavaScript, and so on, that when passed through the [Site Builder](http://github.com/jonathangiles/sitebuilder) template tool will generate a completely static website.

This GitHub repository is configured so that every time a commit is made to the repository a [GitHub Action is executed](https://github.com/JonathanGiles/jonathangiles.net/blob/master/.github/workflows/main.yml) that will re-run the Site Builder tool, generate an entirely new version of the website, and then push it up into Azure Blob Storage where it is hosted behind an Azure CDN.

## Building

To build a static output of the site, run the following command:

```java
mvn clean package exec:java
```

To build the files and then start up a local server to host them, run the following command:

```java
mvn clean package exec:java -Dexec.args="runserver"
```