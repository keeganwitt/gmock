Download the jar file of Gmock from the [download list](http://code.google.com/p/gmock/downloads/list), and drop it in the **lib** directory of your grails project. That's it. Then you can start writing your unit tests with Gmock.

Since Grails 1.2, a [dependency resolution](http://grails.org/doc/latest/guide/single.html#3.7%20Dependency%20Resolution) mechanism is introduced. You can simply specify the dependency of Gmock in the **grails-app/conf/BuildConfig.groovy** file of your grails project as below:

```
grails.project.dependency.resolution = {
    ...
    repositories {        
        grailsPlugins()
        grailsHome()
        mavenCentral() // Gmock is in the Maven Central Repository
    }
    dependencies {
        test 'org.gmock:gmock:0.8.0'
        test 'org.hamcrest:hamcrest-library:1.1' // Optionally, you can use hamcrest matchers
        ...
    }
}
```