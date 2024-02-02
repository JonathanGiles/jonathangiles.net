---
slug: simple-http-server
title: Building a simple HTTP server
date: 2024-01-15
---
Often I talk to people who are new to the software industry, and they don't know how to get a leg in the door. One thing I say is that hacking on small toy projects can be a great way to build skills, interact with others, and to advertise yourself. I've been lucky in my career to work on a number of high profile projects, be they existing projects like [OpenJDK](http://openjdk.org) and [JavaFX](https://openjfx.io/), or projects I've been involved in since the beginning, like my work on [Azure SDK for Java](https://github.com/Azure/azure-sdk-for-java), or on projects I created that have grown and been adopted by many, such as [ControlsFX](https://github.com/controlsfx/controlsfx) and [Scenic View](https://github.com/jonathangiles/scenic-view). 

Besides these projects, I like to work on my own little playgrounds when time permits, and so I thought today I would write about why and how I created TeenyHttpd, a simple HTTP server written in Java. I don't write this to demonstrate my expert skills (because, they are not on display!), but because it shows a path that I think junior developers can follow to build their own skills, experience, and opportunities.

## Why build a HTTP server?

If you squint your eyes, it is possible to write an HTTP server from primitives in Java with only a few lines of code. Of course, claiming this to be a fully-fledged HTTP server does a huge disservice to actual HTTP servers, as the complexities and range of features they support are far beyond what I will be covering here. However, it is a fun exercise to see how far we can get with a few lines of code.

Over the last few years I have *very* intermittently been writing an HTTP server of my own. I don't quite remember why I started it - I think it was to consider whether a 'proper' project I was working on should roll its own HTTP server implementation, or use an existing one. Of course I would never be so naive to suggest someone use *my* HTTP server implementation, but I enjoy the opportunity to design something minimally and add features to it...even if I never really need the features. This kind of project also means I can expand in whichever direction I want to go, without anyone else's opinions getting in the way!

Anyway, when I first built this server, which I call TeenyHttpd, I was surprised at how little code it took to get something working. I've since rewritten it a few times, and it's now a little more complex, but still not much. I thought it would be fun to write a blog post about how it works, and how it has evolved over time. You can [find the GitHub repo here](https://github.com/JonathanGiles/TeenyHttpd).

## The first version

If you [look at the repository as it was initially formed in April 2020](https://github.com/JonathanGiles/TeenyHttpd/tree/4373da89a67c17f5de1fc28c764c82489a2b1120/src/net/jonathangiles/teenyhttpd), there was very little to see. To use the code, you would write something like this:

```java
public class Main {
    public static void main(String[] args) {
        final int PORT = 80;
        TeenyHttpd server = new TeenyHttpd(PORT) {
            @Override public Response serve(final Request request) {
                return new StringResponse(request, StatusCode.OK, "Hello!");
            }
        };
        server.start();
    }
}
```

In other words, you just waited to serve, whenever a request came in! You could read the `Request` and learn what was being asked for, and then return a `Response`. There was different kinds of `Response` you could send - `ByteResponse`, `FileResponse`, and `StringResponse`.

The interesting one was `FileResponse` - it just took the `Request`, and tried to map it onto a file available on the local filesystem. If it found the file, it would return it, otherwise it would return a 404. It was enough to serve a static website - which is exactly what I then put the code to use for. I upgraded my personal website generator so that it would run TeenyHttpd as part of the build process, so that I could review my site locally before deploying it. This worked beautifully, and I was quite proud of myself for how much bikeshedding I had achieved, between my own static [website generator](https://github.com/JonathanGiles/sitebuilder) and my own [HTTP server](https://github.com/JonathanGiles/TeenyHttpd).

## Releasing to Maven

A month later, in May 2020, I must have found some more time, as I put in some polish and got a 1.0.0 release of the server put up on Maven Central. Most of that [work occurred in a single PR](https://github.com/JonathanGiles/TeenyHttpd/commit/55941c4fdfd54f1d91492985a3e72058e53f0160). I also added the ability to just specify a webroot location, to serve static content. Clearly I was enjoying my static website generator at this time:


```java
final int PORT = 80;
TeenyHttpd server = new TeenyHttpd(PORT);
server.setWebroot(new File("/Users/jonathan/Code/jonathangiles.net"));
server.start();
```

## Supporting content types

Clearly my static website was starting to get more content types hosted on it, as in June 2020 I added support for content types. The first commit was very primitive - [I guess I was just testing things out](https://github.com/JonathanGiles/TeenyHttpd/commit/dea23ba22af4dd856b40f1825ba040bfdd1c4835), but this was followed up two weeks later in mid-June with a ['better' implementation of content types](https://github.com/JonathanGiles/TeenyHttpd/commit/56894526876078f16b05e12bd3a95a464a99ec7d). This follow-up is still simplistic - it just maps file extensions to content types, by looking up a text file that provides the mappings. If that mapping doesn't exist, there is fallback code to try to get Java to do something sensible. This is a very naive approach, but it works well enough for my purposes.

## Getting the itch again

The coding above all took place mostly in 2020, with minor efforts in 2021 and 2023 to fix file handling on Windows (guess who just built a Windows desktop around that time!) and to update a dependency. I'm hardly setting the world on fire with this project and its pace, but who cares - no one uses it but me!

That changed a little bit late last year. Not because someone came banging on my door wanting me to finish it off, but because I purchased myself a Apple MacBook Pro M3 with a relatively maxed out configuration. I was so delighted to be back on macOS, and I felt compelled to do some more coding. I thought a fun challenge would be to improve TeenyHttpd to support a few more features. In particular, I thought it would be cool to support parameterised paths. In other words, I wanted to have a path like `/posts/:year/:slug` and have the server be able to extract the `year` and `slug` parameters from the request, and provide them to me so that I could use them to form the response. I had no idea how to do this, but I thought it would be a fun challenge to try to figure it out.

For a more concrete example, I wanted to be able to write code like this:

```java
server.addGetRoute("/user/:id/details", request -> {
    String id = request.getPathParams().get("id");
    return new StringResponse(StatusCode.OK, "User ID: " + id);
});
```

Now a request to `/user/jonathan/details` would return a response with the text `User ID: jonathan`. Hardly amazing, and I actually have no use case for it - but it was a fun problem to think through.

### The solution

I decided to use regular expressions to solve this problem. I'm not a huge fan of regular expressions, but they are a powerful tool, and I thought they would be a good fit for this problem. The problem was, I had no idea how to use them to solve my problem. I thought though, if I could somehow convert a path like `/user/:id/details` into a regular expression, then I could use that regular expression to match against the incoming request path, and extract the parameters. I kind of hoped there existed a 'named parameters' feature in regular expressions, so that I could name the `id` parameter, and then extract it by name. I had no idea if this existed, but I thought it was worth a try.

After only a little Googling, I came across what looked to be exactly what I was after - [regular expression named capturing groups](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Regular_expressions/Named_capturing_group). You can essentially put a name in front of a regular expression, and then use that name to capture the result by that name. The syntax is simply `(?<name>pattern)`. After a bit of hacking, I ended up with the [very minimal code that I needed to get this working](https://github.com/JonathanGiles/TeenyHttpd/blob/853f9e5a863c41090756323f7da83c6ca4cdd8f1/src/main/java/net/jonathangiles/tools/teenyhttpd/TeenyHttpd.java#L336-L368).

For example, given the `/user/:id/details` route, the regular expression that is created is `/user/(?<id>[^/]*+)/details`. This regular expression is then used to [match the incoming request path against the incoming request](https://github.com/JonathanGiles/TeenyHttpd/blob/master/src/main/java/net/jonathangiles/tools/teenyhttpd/TeenyHttpd.java#L261-L270) path. If the request path is `/user/jonathan/details`, then the regular expression matches, and the `id` parameter is extracted and added to the `Request` object.

If the path is more complex, for example if there are multiple parameters, it still works! For example, with the code below:

```java
server.addGetRoute("/foo/:bar/:baz", request -> {
    String bar = request.getPathParams().get("bar");
    String baz = request.getPathParams().get("baz");
    return new StringResponse(StatusCode.OK, "bar: " + bar + ", baz: " + baz);
});
```

The path is `/foo/:bar/:baz`, and the regular expression that is created inside the server to respond to incoming requests is `/foo/(?<bar>[^/]*+)/(?<baz>[^/]*+)`. If the incoming request path is `/foo/123/456`, then the regular expression matches, and the `bar` and `baz` parameters are extracted and added to the `Request` object with the values `123` and `456` respectively.

## Improving code with GitHub CoPilot

Having hacked this all together, I was quite pleased with myself. One thing I was not pleased about was the total lack of unit tests. Because I had acess to GitHub CoPilot from within IntelliJ, I thought I would use the CoPilot Chat feature to do the work for me. I pointed it to TeenyHttpd, told CoPilot Chat that it was an HTTP server, and that I wanted to write as many unit tests as I could to test all the various permutations of request style. I then sat back and watched as CoPilot Chat [wrote a bunch of unit tests for me](https://github.com/JonathanGiles/TeenyHttpd/blob/master/src/test/java/net/jonathangiles/tools/teenyhttpd/TeenyHttpdTest.java). I was very impressed with the results. All I had to do was keep asking for more, and suggesting ideas I had of areas that could have more tests written. It gave me a lot of confidence in the quality of my simplistic implementation, although any professional HTTP server implementation would have a lot more tests than this. I recall that the tests even demonstrated one or two bugs in my code that I needed to resolve, which was a nice bonus.

## Using GitHub Actions

GitHub Actions are another thing I always tell junior engineers to play with. It is great to ensure that the thing you're working on actually works on more than just your machine, and GitHub Actions lets you do this! I just recently added GitHub Actions CI builds to TeenyHttpd. Again, it is really nothing special - just [a single YAML file](https://github.com/JonathanGiles/TeenyHttpd/blob/master/.github/workflows/main.yml) with a few steps in it to set up Java, build and test TeenyHttpd, and publish a report. This runs whenever I ask for it, and also on every push to the master branch in the repo. I actually had to fight with GitHub Actions for a while to get it to work - on my machine all the tests passed all the time, but this wasn't true on GitHub, and it exposed a [subtle timing bug in TeenyHttpd that I had to fix](https://github.com/JonathanGiles/TeenyHttpd/commit/77854ebc8d5551a228b0d4d20004b59e5985cbe6). I think my machine was so fast I never encountered it. I was very happy to have this bug exposed, and to have the opportunity to fix it.

## Simplifying serving content

When I [tweeted about TeenyHttpd the other day](https://twitter.com/JonathanGiles/status/1749595450775339095), I [received a comment](https://twitter.com/maxandersen/status/1749707820109299845) that it would be cool to be able to drop the TeenyHttpd executable into a directory with existing static content, and just have it start serving whatever it finds. I thought this was a great idea, and so I spent about 20 minutes hacking out the feature. I ended up with a very simple implementation that does exactly this. It was the first 'customer-driven requirement', even though I know the requestor never has any actual intention of using it! I added a section into the [readme file](https://github.com/JonathanGiles/TeenyHttpd?tab=readme-ov-file#serving-static-content) about serving static content, and I was done. I was very happy with how quickly I was able to add this feature, and how simple it was to use.

## In summary

I'm not sure I'll do anything more on this project any time soon - but that's ok - it just means that if I have an itch to scratch in the future I have another project to scratch it with. Would I put this code on my CV as code that is demonstrative of my skill? No, of course not. It is a toy project, and it is not a good example of my skills. However, it is a good example of how I like to learn, and how I like to experiment with code. I think it is important to have these kinds of projects, and I think it is important to have a place to put them. I'm certain I'll never use TeenyHttpd for anything other than my own personal use, but I'm glad I have it, and I'm glad I have the opportunity to share it with others.

If you have any questions about this project, or any other projects I've worked on, please feel free to reach out to me on [Twitter](https://twitter.com/jonathangiles). I'm always happy to chat about code, and to help others learn and grow.

Now that I've written this long markdown file, it's time for me to run my build script and review it locally - using TeenyHttpd! I hope you enjoyed this post, and I hope you have a great day!
