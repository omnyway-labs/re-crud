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
