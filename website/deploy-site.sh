#!/bin/sh
rm -fr site
mkdocs build
cd site
git init
git checkout -b gh-pages
git add .
git commit -m "Deploy site"
git push -f "https://`db-get GHP_TOKEN`@github.com/instancio/instancio" gh-pages
echo "Done"
