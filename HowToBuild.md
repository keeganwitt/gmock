You can use the following commands with the Gradle Wrapper in the root folder:

  * `gradlew clean`
> > remove the 'build' directory
  * `gradlew test`
> > run the unit tests
  * `gradlew`
> > the same as above
  * `gradlew check`
> > run all the tests including the unit tests and integration tests
  * `gradlew install`
> > install the artifact to the local Maven repository
  * `gradlew snapshot`
> > package the jar file and all other additional files (e.g. source codes), the result will be in 'build/distributions' directory
  * `gradlew release`
> > the same as above, but without '-SNAPSHOT' in the version
  * `gradlew eclipse`
> > generate eclipse project files
  * `gradlew idea`
> > generate idea project files