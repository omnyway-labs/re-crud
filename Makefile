default: help

test:: ## Run tests
	lein do clean, doo phantom test once

.PHONY: help

help:
	@echo "usage: make target ..."
	@echo "available targets:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) \
		| sort \
		| awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

deploy:: ## Deploy to clojars
	lein deploy clojars

rebuild-example:: ## Rebuild example-app
	bin/rebuild-example.sh

deploy-gh-pages:: ## Deploy latest assets from example-app to github-pages (WARNING: Resets your current git staging area)
	cp -r example-app/resources/public/* docs
	git reset
	git add docs
	git commit -m "Update github-pages example-app"
