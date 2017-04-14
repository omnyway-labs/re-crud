(ns re-crud.native-components
  (:require [reagent.core :as reagent]
            [cljsjs.reactable]))

(def table (reagent/adapt-react-class js/Reactable.Table))
(def thead (reagent/adapt-react-class js/Reactable.Thead))
(def tr (reagent/adapt-react-class js/Reactable.Tr))
(def td (reagent/adapt-react-class js/Reactable.Td))
(def th (reagent/adapt-react-class js/Reactable.Th))
