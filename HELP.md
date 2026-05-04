# Getting Started

## Reference Documentation

For general guidance, please see the [Java Stack Starter Documentation][java-stack-starter-docs].

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)

### Guides

The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)

## Building

From the root project folder, execute the following command:

```shell
mvn clean package
```

## IntelliJ Conveniences

### Run/Debug Configurations

Several IntelliJ configurations have been created for your convenience.

| IntelliJ Configuration Name | Description                                                                                            |
|:----------------------------|:-------------------------------------------------------------------------------------------------------|
| `Application`               | Start the main application class. The application port is running on 5000 as it runs in AWS Beanstalk. |
| `All Unit/Component Tests`  | Run all unit/component tests in the project                                                            |
| `All Acceptance Tests`      | Run all acceptance tests in the project after starting the main application                            |

These configurations have been placed at the root of the project in the .run
folder.

> Note: the .run configuration was introduced in [2020.1's release][intellij-2020-1-release].
These configurations should not be edited directly here, but through the IntelliJ
"Run | Edit Configurations..." menu.

The `Tests` configurations use an "AcceptanceTest" tag as applied in the
java-stack-test starter's [AcceptanceTest autoconfiguration][java-stack-test-auto-configure] to
filter execution of Acceptance Tests vs. Unit & Component Tests (which do not
have this tag). This assumes that all future acceptance tests will use the
java-stack-tests `@AcceptanceTest` annotation.

> [!NOTE]
> If this is a new project, you may need to ensure that the module (Java SDK) is set in the run configurations.

> [!NOTE]
> When executing the `All Acceptance Tests` run configuration, the `Application` must be
running. A command line build to build the Enunciate docs should also be done.

### Copyright profile

The standard Family History Department copyright profile has been included.
Ensure you select `Update copyright` in your commit dialog settings to get
automatic updates.

## Locally Simulating our Continuous Delivery Pipelines' Acceptance Execution

The validation pipeline stands up the application under test and then executes `mvn verify`
using the `acceptance.testing` Maven profile. Environment variables, as
specified in your blueprint's `validate` section, are passed to the process.

The following command line executions simulate the pipeline execution.

* Build the application locally
  * `mvn clean install`
* In one terminal, run the application locally
  * `cd ./webapp/target/`
  * `java -jar springboard-webapp.jar`
* In another terminal
  * `cd ./acceptance`
  * `mvn -Dacceptance.testing verify`
  * For debug, add the following to the command line: ` -Dmaven.failsafe.debug`

Note in this project, there are two application property
files, `application.properties` and `application-acceptance.properties`.
The `application.properties` file is loaded by default as executed in the above
commands and is generally where you should put settings for local development.

With these properties `fs.test.acceptance.client.service-root` property is set
to `http://localhost:5000` by default. (There is no need to set
the `<upper case system name>_<uppercase application service name>_URL` or in
our case `ACCEPTANCE_WEBAPP_URL`). The `application-acceptance.properties` file represents the
file to be used by CI/CD GitHub Actions Validations pipeline specified by the blueprint validate
validation_systems value `acceptance` and makes use of the host returned from the deployment and
passed through our `ACCEPTANCE_WEBAPP_URL` in the property
setting `fs.test.acceptance.client.service-root=http://${ACCEPTANCE_WEBAPP_URL}` where it
resolves to the ELB address such
as `ACCEPTANCE_WEBAPP_URL=internal-awseb-e-i-AWSEBLoa-RQGZ9H9XFG47-1265919073.us-east-1.elb.amazonaws.com`
. If you are changing your blueprint validate type to `Post-Integrate v1_0`, you
may set `fs.test.acceptance.client.service-root` to your binding set URL.

For more information, see the [Java Stack Starter Test Documentation][java-stack-starter-test-docs].

### Generating CI/CD Pipelines

The provided blueprint.yml is a version 1.1 blueprint.  The initializer also provides a
CI/CD pipeline bootstrapping file `.github/workflows/cicd_pipelines_sync-blueprint_pipeline-generation_v1.yaml`.

To create the CI/CD Pipelines, follow the instructions on the Confluence page:
[Bootstrapping Pipelines for a new blueprint][bootstrapping-cicd].  Note under the section
`Create a Blueprint File and Bootstrap File`, steps 1 & 2 have been completed for you
by the initializer.  Continue with step 3.

### SAS Configuration for Test CIS Sessions

Acceptance tests use [TestDataService][test-data-service] by default. This requires a SAS token to create user sessions.
Get the `Test-Data-Encrypt-Key` from a QA team member and add it to your SAS override directory.

If acceptance tests do not require the creation of user sessions, you can disable it as follows:

* Turn SessionAccess off globally by setting `fs.test.acceptance.session.enabled=false` in your acceptance module
  properties files. This disables the sessionEnabled attribute of the `@AcceptanceTest` annotation.

For more information on SAS and the [TestDataService][test-data-service],
please see the [Making Service Requests Using a Test User Session of the Java
Stack Starter Test Documentation][java-stack-starter-docs-test-user-session]

## Using the generated dashboard XML to create your first Splunk dashboard

To bootstrap a Splunk dashboard using the generated dashboard source, please see the [README](.splunk/README.md)
in the `.splunk` directory.

© 2026 by Intellectual Reserve, Inc. All rights reserved.

[java-stack-starter-docs]: https://github.com/fs-eng/java-stack/blob/master/java-stack-docs/README.md
[java-stack-starter-test-docs]: https://github.com/fs-eng/java-stack/blob/master/java-stack-docs/starters/java-stack-starter-test.md
[java-stack-starter-docs-test-user-session]: https://github.com/fs-eng/java-stack/blob/master/java-stack-docs/starters/java-stack-starter-test.md#making-service-requests-using-a-test-user-session
[test-data-service]: https://github.com/fs-eng/test-data-service
[java-stack-test-auto-configure]: https://github.com/fs-eng/java-stack/blob/master/java-stack-project/java-stack-test-autoconfigure/src/main/java/org/familysearch/stack/java/test/autoconfigure/acceptance/AcceptanceTest.java
[intellij-2020-1-release]: https://blog.jetbrains.com/idea/2020/03/intellij-idea-2020-1-beta2/
[bootstrapping-cicd]: https://icseng.atlassian.net/wiki/x/FoReBw
