---
slug: /jbp/index
title: Java Best Practices
---

Java has been around for an exceptionally long time, and continues to find massive support from developers and businesses worldwide. It has a proven track record of reliably working in mission critical projects, where performance, supportability, wider ecosystem support, and Java's continued evolution see its adoption growing.

The Java Best Practices website is for all Java developers. The goal is to distill years of Java experience down to a set of best practices that help developers build high-quality Java applications and libraries.

The best practices are split into two sections. The first applies to all Java developers, and covers topics such as dependency management, tooling, and choosing a JDK. The second is aimed at developers of Java libraries (which, as [JLBP-1](/jbp/jlbp-1.html) argues, is more of us than we might think), and focuses on designing APIs that are a pleasure to use and that can evolve gracefully over time.

*Last reviewed November 2026, against Java 25 (the current LTS release).*

## Prefer to watch a video?

You're in luck - you can [watch (or review the slides from) some of my presentations online](/java-api-design-best-practices.html). These date from 2018, so some details have moved on, but the core API design principles remain the same.

## Best Practices for Java Developers

JBP-1: [Use BOMs whenever available](/jbp/jbp-1.html)\
JBP-2: [Minimize dependencies](/jbp/jbp-2.html)\
JBP-3: [Don't float dependency versions](/jbp/jbp-3.html)\
JBP-4: [Keep dependencies up to date](/jbp/jbp-4.html)\
JBP-5: [Make use of logging APIs](/jbp/jbp-5.html)\
JBP-6: [Configure build plugins](/jbp/jbp-6.html)\
JBP-7: [Use the latest Java Long-Term Support release](/jbp/jbp-7.html)\
JBP-8: [A little copying is better than a new dependency](/jbp/jbp-8.html)\
JBP-9: [Secure your software supply chain](/jbp/jbp-9.html)

## Best Practices for Java Library Developers

JLBP-1: [Characteristics of good API](/jbp/jlbp-1.html)\
JLBP-2: [Embrace modules](/jbp/jlbp-2.html)\
JLBP-3: [Release to Maven Central](/jbp/jlbp-3.html)\
JLBP-4: [Publish BOMs for multi-module projects](/jbp/jlbp-4.html)\
JLBP-5: [Use semantic versioning](/jbp/jlbp-5.html)\
JLBP-6: [Resist breaking changes](/jbp/jlbp-6.html)\
JLBP-7: [Minimize API visibility](/jbp/jlbp-7.html)\
JLBP-8: [Minimize API](/jbp/jlbp-8.html)\
JLBP-9: [Design for extensibility](/jbp/jlbp-9.html)\
JLBP-10: [Avoid shading dependencies](/jbp/jlbp-10.html)\
JLBP-11: [Write great documentation](/jbp/jlbp-11.html)\
JLBP-12: [The importance of consistency](/jbp/jlbp-12.html)\
JLBP-13: [Eat your own dog food](/jbp/jlbp-13.html)\
JLBP-14: [Document your deprecation policy](/jbp/jlbp-14.html)\
JLBP-15: [Don't return null](/jbp/jlbp-15.html)\
JLBP-16: [Beware of boxing](/jbp/jlbp-16.html)\
JLBP-17: [Understand functional interfaces](/jbp/jlbp-17.html)\
JLBP-18: [Make nullness explicit](/jbp/jlbp-18.html)\
JLBP-19: [Prefer immutable value types](/jbp/jlbp-19.html)\
JLBP-20: [Choose your Java baseline deliberately](/jbp/jlbp-20.html)\
JLBP-21: [Avoid diamond dependency conflicts](/jbp/jlbp-21.html)\
JLBP-22: [Design for AI coding agents](/jbp/jlbp-22.html)
