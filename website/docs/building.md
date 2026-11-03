---
hide:
  - navigation
  - toc
description: "Instructions for building Instancio from sources and MkDocs documentation site."
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

To build the site, you will need <a href="https://docs.astral.sh/uv/">uv</a>.
It installs the exact versions of MkDocs and its plugins from `uv.lock` on first use.

To run the site locally: `cd website && uv run mkdocs serve`. To generate static HTML: `cd website && uv run mkdocs build`.