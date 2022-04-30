#!/bin/sh
cd target/generated-docs
git init
git add .
git commit -m "Deploy documentation"
git checkout -b gh-pages
git push -f "https://`dbget GHP_TOKEN`@github.com/instancio/instancio" gh-pages
