# PLC Reader
## Synopsis
PLC reader is a spring boot application to poll the real time values from the PLC sensors fitted in the canal barge assets and stores the information into IBM cloudant DB.

## Modules

### 1.Controller
Service to read the polling scheduling information from the DB and trigger the polling for all the interested assets.

### 2.Executor
Service to poll the sensor values from the given asset and store the retrieved information into DB.

## Installation

### To run the app in the development mode:

#### Prerequisite
1. Environment provisioning tool Vagrant by HashiCorp  (https://www.vagrantup.com) is required to bring upo the deve environment. 
2. Download and install the vagramt tool https://www.vagrantup.com/downloads.html.
#### Steps
1. Clone the repository locally.
2. cd to the root folder of the project.
3. Run 'vagrant up'.
4. Edit the windows host file C:\Windows\System32\drivers\etc and add the below entries.

>             172.28.128.3 cbsdev    
>             10.10.0.2 kubedev

5. Run `route add 10.0.0.0 mask 255.255.255.0 10.10.0.2` in Command window.         
6. Set the spring profile for both services to dev and run the applicaiton.


## API Reference

1. Controller:


 - **To start the polling.**
            http://localhost:8080//dev/plccontroller/start
 - **To stop the polling.**
            http://localhost:8080//dev/plccontroller/stop
  - **Cloudant dashboard.**
            http://cbsdev:8090/dashboard.html           
   - **Kubernetes dashboard.**
            http://kubedev:8080/api/v1/namespaces/kube-system/services/dashboard/proxy/#!/node?namespace=defaultl           



## Tests

Yet to be written


## License

License own by Canal barge Company
