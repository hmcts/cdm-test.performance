# cdm-test.performance
Gatling performance tests for CCD

In order to run the CCD Performance tests, you will need to ensure that you have access to one of the performance VMs in order to run the test - performance tests should not be run from your local machine!

1. Clone the repo to your local, ensure that you have Gradle configured and set up
2. Open the project in your preferred IDE (IntelliJ is best though)
3. The simulation file /scenarios/simulations/CCDUIPTSimulation controls how the test runs, you can change the number of iterations by editing the repeat value for each scenario
4. The number of users and ramp up settings are controller under the setUp towards the bottom of the script
5. Thinktimes and pacing settings are controlled under /scenarios/utils/Environment
6. If you make changes to the default runtime settings, then you will need to push your changes back to the repo and then clone or *git pull* on the VM (depending if you already have this repo cloned on the VM or not)
