# predictor2-svc

Uses DropWizard to create wrap a service around the ExperimentPrimeRunner.

After building predictor2-core, build with

    mvn clean install package

When that finishes running, launch with

    java -jar target/predictor-svc-0.0.1-SNAPSHOT.jar server src/main/resources/predictor-svc.yml

To enable remote JMX metrics on AWS, use:

    java -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=7081 -Dcom.sun.management.jmxremote.rmi.port=7082 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Djava.rmi.server.hostname=ec2-184-72-67-128.compute-1.amazonaws.com -Xmx512m -jar predictor-svc-0.0.1-SNAPSHOT.jar server predictor-svc.yml

The extra memory allowed it to run an experiment overnight.

## Current Workflow

When the PredictorService starts, it creates an OrganismStore with a capacity of 1000, but does not fill it until an experiment is started.

Start an experiment running in a background thread:

    http://localhost:8080/experiment/start

Get the status of the current running experiment, or the final status of the last experiment that ran:

    http://localhost:8080/experiment/start
    
Set the current experiment to continue running even after it has completed the number of cycles it was initialized with:

    http://localhost:8080/experiment/setContinueExperimenting/true
    
A more detailed summary of the last experiment (I should really make this the default status since I calculate it every feed cycle):

    http://localhost:8080/experiment/lastExperimentResult
    
Get a graph of the OrganismStore's best score over the progress of the last experiment:

    http://localhost:8080/experiment/lastExperimentBestScoreGraph

Get summary information for the OrganismStore:

    http://localhost:8080/store

Dump the top n Organisms to disk.  These will be used to fill the store the next time the service starts up:

    http://localhost:8080/store/dumpTop/{n}

Get a feed graph for the organism at index (starts at 0):

    http://localhost:8080/store/store/feedGraph/{index}

