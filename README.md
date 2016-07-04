# predictor2-svc

Uses DropWizard to create wrap a service around the ExperimentPrimeRunner.

After building predictor2-core, build with

    mvn clean install package

When that finishes running, launch with

    java -jar target/predictor-svc-0.0.1-SNAPSHOT.jar server src/main/resources/predictor-svc.yml