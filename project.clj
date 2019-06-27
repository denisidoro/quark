(defproject denisidoro/quark "0.8.2"

  :description "Clojure(Script) utility belt"
  :url "https://github.com/denisidoro/quark"

  :license {:name "The Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"
            :distribution :repo}

  :dependencies [[cheshire "5.8.1"]]

  :plugins [[lein-cloverage "1.0.13"]
            [lein-shell "0.5.0"]
            ;[lein-ancient "0.6.15"]
            [lein-auto "0.1.3"]
            [lein-changelog "0.3.2"]]

  :profiles {:dev {:dependencies [[org.clojure/clojure "1.10.0"]
                                  [org.clojure/clojurescript "1.10.145"]]}}

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
