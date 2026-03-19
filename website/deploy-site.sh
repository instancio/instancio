#!/bin/sh
rm -fr site
mkdocs build
cd site
git init
git checkout -b gh-pages
git add .
git commit -m "Deploy site"
git push -f "git@github.com:instancio/instancio.git" HEAD:gh-pages
echo "Done"
