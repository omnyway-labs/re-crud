(ns re-crud.test-runner
  (:require  [doo.runner :refer-macros [doo-tests]]
             [re-crud.core-test]))

(doo-tests 're-crud.core-test)
