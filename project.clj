(defproject denisidoro/quark "0.2.0"

  :description "Clojure(Script) utility belt"
  :url "https://github.com/denisidoro/quark"

  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[cheshire "5.8.1"]]

  :plugins [[lein-cloverage "1.0.13"]
            [lein-shell "0.5.0"]
            [lein-ancient "0.6.15"]
            [lein-changelog "0.3.2"]]

  :profiles {:dev {:dependencies [[org.clojure/clojure "1.9.0"]
                                  [midje "1.9.4" :exclusions [org.clojure/clojure]]]
                   :plugins      [[lein-midje "3.2.1"]]}}

  :deploy-repositories [["releases" :clojars]]

  :aliases {"update-readme-version" ["shell" "sed" "-i" "s/\\\\[denisidoro\\\\/quark \"[0-9.]*\"\\\\]/[denisidoro\\\\/quark \"${:version}\"]/" "README.md"]
  "test2" ["shell" "bash" "./scripts/test"]}

  :release-tasks [["shell" "git" "diff" "--exit-code"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["changelog" "release"]
                  ["update-readme-version"]
                  ["vcs" "commit"]
                  ["vcs" "tag"]
                  ["deploy"]
                  ["vcs" "push"]]

  :test-paths ["test/"])
