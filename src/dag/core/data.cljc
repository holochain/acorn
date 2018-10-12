(ns dag.core.data)

(def example-items
 [{:id "foo"
   :targets ["bar" "baz"]}
  {:id "bar"}
  {:id "baz"
   :targets ["foo"]}])

(def example-elements
 [{:data {:id "foo"}}
  {:data "foobar"
   :source "foo"
   :target "bar"}
  {:data "foobaz", :source :foo, :target :baz}
  {:data {:id :bar}} {:data {:id :baz}}
  {:data ":baz:foo", :source :baz, :target :foo}])
