---
hide:
  - navigation
  - toc
---

# Building From Sources

Instancio can be used with Java 17 or higher.
Building Instancio from sources requires JDK 17 or higher:

```sh
git clone https://github.com/instancio/instancio.git
cd instancio
mvn verify
```

# Building the Website

This site is built using <a href="https://www.mkdocs.org">MkDocs</a>
and <a href="https://squidfunk.github.io/mkdocs-material">MkDocs Material</a> theme.

To build the site, you will need the following Python packages:

```sh
pip install mkdocs \
    mkdocs-material \
    mkdocs-macros-plugin \
    mkdocs-autolinks-plugin \
    mkdocs-minify-html-plugin
```

To run the site locally: `cd website && mkdocs serve`. To generate static HTML: `mkdocs build`.