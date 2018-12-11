(defproject denisidoro/quark "0.4.2"

  :description "Clojure(Script) utility belt"
  :url "https://github.com/denisidoro/quark"

  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :plugins [[lein-cloverage "1.0.13"]
            [lein-shell "0.5.0"]
            ;[lein-ancient "0.6.15"]
            [lein-tools-deps "0.4.1"]
            [lein-changelog "0.3.2"]]

  :profiles {:dev {:dependencies [[org.clojure/clojure "1.9.0"]
                                  [org.clojure/clojurescript "1.10.145"]]}}

  :middleware [lein-tools-deps.plugin/resolve-dependencies-with-deps-edn]

  :lein-tools-deps/config {:config-files [:install :user :project "deps.edn"]}

  :deploy-repositories [["releases" :clojars]]

  :aliases {"update-readme-version" ["shell" "sed" "-i" "s/\\\\[denisidoro\\\\/quark \"[0-9.]*\"\\\\]/[denisidoro\\\\/quark \"${:version}\"]/" "README.md"]}

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
