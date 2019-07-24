(defproject denisidoro/quark "0.9.1"

  :description "Clojure(Script) utility belt"
  :url "https://github.com/denisidoro/quark"

  :license {:name         "The Apache License, Version 2.0"
            :url          "http://www.apache.org/licenses/LICENSE-2.0"
            :distribution :repo}

  :dependencies [; serialization
                 [cheshire "5.8.1"]

                 ; specs/schemas
                 [org.clojure/core.async "0.4.500"]
                 [metosin/schema-tools "0.12.0"]

                 ; graph
                 [com.wsscode/pathom "2.2.16"]

                 ; dependency injection
                 [com.stuartsierra/component "0.3.2"]

                 ; server
                 [io.pedestal/pedestal.service "0.5.5"]
                 [io.pedestal/pedestal.jetty "0.5.5"]
                 [io.pedestal/pedestal.service-tools "0.5.5"]
                 [http-kit "2.3.0"]
                 [io.aviso/pretty "0.1.34"]
                 [ch.qos.logback/logback-classic "1.1.8" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.22"]
                 [org.slf4j/jcl-over-slf4j "1.7.22"]
                 [org.slf4j/log4j-over-slf4j "1.7.22"]]

  :plugins [[lein-cloverage "1.0.13"]
            [lein-shell "0.5.0"]
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
