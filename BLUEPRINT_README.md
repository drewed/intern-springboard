
The following blueprint is a proposal for moving to integration, test/beta/stable and prod system lanes.

For more information regarding the blueprint and Continuous Delivery, please
see: [Blueprints and Continuous Delivery](https://icseng.atlassian.net/wiki/x/LoVeBw)
For interactive questions and answers regarding the blueprint and platform, visit
the [#dpt-users](https://app.slack.com/client/T025G3P40/C07KDNCN7) channel on Slack.

```yaml
#
# © 2026 by Intellectual Reserve, Inc. All rights reserved.
#

name: intern-onboarding-springboard
version: '1.1'
builds:
  - name: maven-build
    type: build-artifacts
    version: '1'
    operations:
      - name: publish
    properties:
      build_tool: maven
      runtimes:
        java:
          distribution: corretto
          version: '25'

  - name: code-analysis
    type: code-analysis
    version: '1'
    operations:
      - name: analyze

systems:
  - name: acceptance # This system is a system that comes and goes specifically for acceptance test validations.
    type: sps
    version: '1'
    operations:
      - name: check
      - name: deploy
        needs:
          - builds.maven-build.publish
      - name: integrate
      # WARNING - TERMINATE SHOULD NOT INADVERTENTLY BE COPIED TO PRODUCTION SYSTEMS
      - name: terminate # This may be left off for troubleshooting. Terminate of this system may also be run in parallel with other system deployments using 'needs' to organized dependencies.
      - name: finally
    properties:
      services:
        webapp:
          type: Beanstalk for Runnable Jar Web Service v1_1
          target:
            type: Network Target v2_0
            location:
              environment: dev
              account: FH5-Development
              region: us-east-1
            network: primary
            # Note: if you add an Alias v1_0 or Service Group v1_0 binding set type, you must specify ‘public’ as your subnet.
            # For more information see: https://icseng.atlassian.net/wiki/x/6wGhL
            subnet: private
          path_to_artifact: webapp/target/springboard-webapp.jar
          platform:
            name: Corretto 25 running on 64bit Amazon Linux 2023
          instance_type: t3a.small
          jvm_options: '-Xms300m -Xmx300m'
          autoscale_options:
            min_instances: 1
            max_instances: 1
          deployment_policy:
            type: Rolling v1_0
            batch_size: 100
            batch_size_type: percentage

  - name: integ # This system may also be called stable.  It is always up for other services to integrate with.
    type: sps
    version: '1'
    operations:
      - name: check
      - name: deploy
        needs:
          - builds.maven-build.publish                  # We need the binaries from build.
      - name: integrate
      - name: finally
    properties:
      services:
        webapp:
          type: Beanstalk for Runnable Jar Web Service v1_1
          target:
            type: Network Target v2_0
            location:
              environment: dev
              account: FH5-Development
              region: us-east-1
            network: primary
            # Note: if you add an Alias v1_0 or Service Group v1_0 binding set type, you must specify ‘public’ as your subnet.
            # For more information see: https://icseng.atlassian.net/wiki/x/6wGhL
            subnet: private
          path_to_artifact: webapp/target/springboard-webapp.jar
          platform:
            name: Corretto 25 running on 64bit Amazon Linux 2023
          instance_type: t3a.small
          jvm_options: '-Xms300m -Xmx300m'
          autoscale_options:
            min_instances: 1
            max_instances: 1
          deployment_policy:
            type: Rolling v1_0
            batch_size: 100
            batch_size_type: percentage

  - name: beta # This system may be called test or staging.
    type: sps
    version: '1'
    operations:
      - name: check
      - name: deploy
        needs:
          - builds.maven-build.publish                  # We need the binaries from build.
      - name: integrate
      - name: finally
    properties:
      services:
        webapp:
          type: Beanstalk for Runnable Jar Web Service v1_1
          target:
            type: Network Target v2_0
            location:
              environment: test
              account: FH3-Test
              region: us-east-1
            network: primary
            # Note: if you add an Alias v1_0 or Service Group v1_0 binding set type, you must specify ‘public’ as your subnet.
            # For more information see: https://icseng.atlassian.net/wiki/x/6wGhL
            subnet: private
          path_to_artifact: webapp/target/springboard-webapp.jar
          platform:
            name: Corretto 25 running on 64bit Amazon Linux 2023
          instance_type: t3a.small
          jvm_options: '-Xms300m -Xmx300m'
          autoscale_options:
            min_instances: 1
            max_instances: 1
          deployment_policy:
            type: Rolling v1_0
            batch_size: 100
            batch_size_type: percentage

  - name: prod
    type: sps
    version: '1'
    operations:
      - name: check
      - name: deploy
        needs:
          - builds.maven-build.publish                  # We need the binaries from build.
      - name: integrate
      - name: finally
    properties:
      services:
        webapp:
          type: Beanstalk for Runnable Jar Web Service v1_1
          target:
            type: Network Target v2_0
            location:
              environment: prod
              account: FH1-Production
              region: us-east-1
            network: primary
            # Note: if you add an Alias v1_0 or Service Group v1_0 binding set type, you must specify ‘public’ as your subnet.
            # For more information see: https://icseng.atlassian.net/wiki/x/6wGhL
            subnet: private
          path_to_artifact: webapp/target/springboard-webapp.jar
          platform:
            name: Corretto 25 running on 64bit Amazon Linux 2023
          instance_type: t3a.small
          jvm_options: '-Xms300m -Xmx300m'
          autoscale_options:
            min_instances: 1
            max_instances: 1
          deployment_policy:
            type: Rolling v1_0
            batch_size: 100
            batch_size_type: percentage

  # This system is used for the CodeBuild Resources Validation of the acceptance system.
  # See the codebuild-resource type validation below.
  - name: accept-cbr
    type: sps
    version: '1'
    operations:
      - name: check
      - name: deploy
      - name: integrate
      - name: finally
    properties:
      services:
        cbr:
          type: CodeBuild Resources v1_0
          target:
            type: Network Target v2_0
            location:
              environment: dev
              account: FH5-Development
              region: us-east-1
            network: primary
          execution_type: validate
          concurrent_build_limit: 10
          approved_consumers:

validations:
  - name: acceptance-gate
    type: codebuild-resources
    version: '1'
    operations:
      - name: validate
        needs:
          - systems.acceptance.deploy
          - systems.accept-cbr.deploy
          - builds.maven-build.publish
    properties:
      validate_tool:
        type: maven-verify
        version: '1'
      compute_type: BUILD_GENERAL1_SMALL
      image: aws/codebuild/amazonlinux2-x86_64-standard:5.0
      runtimes:
        java: corretto25
      system_under_test: URI://intern-onboarding-springboard/acceptance
      codebuild_resources_ref: URI://intern-onboarding-springboard/accept-cbr/cbr
      environment: dev  # Must match codebuild_resources_ref's Target Environment
      directory: ./acceptance
      queued_timeout_in_minutes: 90
      timeout_in_minutes: 90
      reports:
        default:
          file_format: 'JUNITXML'
          file_paths:
            - '**/surefire-reports/TEST-*Test.xml'

pipelines:
  - name: pull-request-pipeline
    type: pull-request
    version: '1'
    operations:
      - name: start
    properties:
      triggers:
        - type: gha
          'on':
            pull_request:
              branches:
                - '*'
              paths-ignore:
                - '**/*.md'
                - '.run/**'
                - '.splunk/**'
                - '.idea/**'

  - name: build-pipeline
    type: primary-on-default-branch
    version: '1'
    operations:
      - name: start
      - name: finally
    properties:
      concurrency:
        type: gha
        group: build-pipeline
      includes:
        - builds.*.*
        - systems.*.check
      triggers:
        - type: pipeline-workflow-dispatch
        - type: gha
          on:
            push:
              branches:
                - master
              paths-ignore:
                - '**/*.md'
                - '.run/**'
                - '.splunk/**'
                - '.idea/**'

  - name: validate-pipeline
    type: secondary-on-default-branch
    version: '1'
    operations:
      - name: start
        needs:
          - pipelines.build-pipeline.finally
      - name: finally
    properties:
      includes:
        - systems.*.*
        - validations.*.*
      excludes:
        - systems.*.check
      pipeline_sequence:
        - operation: systems.acceptance.integrate
          waits_for:
            - validations.acceptance-gate.validate
        - operation: systems.integ.deploy
          waits_for:
            - validations.acceptance-gate.validate
        - operation: systems.beta.deploy
          waits_for:
            - validations.acceptance-gate.validate
        - operation: systems.prod.deploy
          waits_for:
            - validations.acceptance-gate.validate
      triggers:
        - type: pipeline-workflow-dispatch
        - type: pipeline-workflow-run

  - name: manual-deploy-pipeline
    type: manual-systems-deploy-on-default-branch
    version: '1'
    operations:
      - name: start
        needs:
          - pipelines.build-pipeline.finally
      - name: finally
    properties:
      triggers:
        - type: pipeline-workflow-dispatch


```
