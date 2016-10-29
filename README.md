# test-store [![Build Status](https://travis-ci.org/ybonjour/test-store.svg?branch=master)](https://travis-ci.org/ybonjour/test-store)
Stores your test results
## Run test-store server
1. Install `docker` and `docker-compose`.
2. Download `docker-compose-prod.yml`
3. Run `docker-compose -f /path/to/docker-compose-prod.yml`


## Store results from your gradle build
Adapt your build.gradle according to the following template

```
buildscript {
    repositories {
        maven {
            url 'https://plugins.gradle.org/m2/'
        }
    }
    dependencies {
        classpath 'gradle.plugin.ch.yvu.teststore:gradle-plugin:0.1'
    }
}

apply plugin: 'ch.yvu.teststore'

teststore {
    host '<your test store host>'
    port  8080
    testSuite '<your test suite id>'
    revision System.properties['revision']
    xmlReports ~/.*\/test-results\/TEST-.*\.xml/
    incremental false
}
```

## Store results at the end of a run
Run `./gradlew storeResults` to store your test results once you have executed your tests.

## Store results as soon as they are available
If you want to store the result of a test as soon as it finishes, you can set `incremental` to `true`. In this case you don't need to use the `storeResults` task.

