# PCF Elastic Runtime Service (ERS) Base Demo
Base application to demonstrate PCF ERS

## Credits and contributions
As you all know, we often transform other work into our own. This is all based from Andrew Ripka's [cf-workshop-spring-boot github repo](https://github.com/pivotal-cf-workshop/cf-workshop-spring-boot) with some basic modifications.

## Introduction
This base application is intended to demonstrate some of the basic functionality of PCF ERS:

* PCF api, target, login, and push
* PCF environment variables
  * Spring Cloud Profiles
* Scaling, self-healing, router and load balancing
* RDBMS service and application auto-configuration
* Blue green deployments

## Getting Started

**Prerequisites**
- [Cloud Foundry CLI](http://info.pivotal.io/p0R00I0eYJ011dAUCN06lR2)
- [Git Client](http://info.pivotal.io/i1RI0AUe6gN00C010l12J0R)
- An IDE, like [Spring Tool Suite](http://info.pivotal.io/f00RC0N0lh01eU21IAJ260R)
- [Java SE Development Kit](http://info.pivotal.io/n0I60i3021AN0JU0le10CRR)

**Building**
```
$ git clone [REPO]
$ cd [REPO]
$ ./mvnw clean install
``` 

### To run the application locally
The application is set to use an embedded H2 database in non-PaaS environments, and to take advantage of Pivotal CF's auto-configuration for services. To use a MySQL Dev service in PCF, simply create and bind a service to the app and restart the app. No additional configuration is necessary when running locally or in Pivotal CF.

In Pivotal CF, it is assumed that a Pivotal MySQL service will be used.

```
$ ./mvnw spring-boot:run
```

Then go to the http://localhost:8080 in your browser

### Running on Cloud Foundry
Take a look at the manifest file for the recommended setting. Adjust them as per your environment.

## Demo Scripts summary
The application tries to be self-descriptive. You'll see when you access the application.

### Jenkins/GIT/Pivotal Tracker Integration

**Install Jenkins (OSX)**
- (http://www.rubydoc.info/gems/pt/0.7.3)

**Install Minimal client to use Pivotal Tracker from the console.**
- (http://www.rubydoc.info/gems/pt/0.7.3)

**Create your own .pt file with Pivotal Tracker information**
- .pt example
```
---
:project_id: 1557179
:project_name: PCF Demo
:user_name: Victor Fonseca
:user_id: 6598619
:user_initials: VF
```
**Use jenkins_jobs.zip for jobs config example**

**Jenkins shell (Build, Pivotal Cloud Foundry push/bind/deploy and Pivotal Tracker task change)**
```
cf login -a $CF_SYSTEM_DOMAIN -u $CF_USER -p $CF_PASSWORD -o $CF_ORG -s $CF_SPACE --skip-ssl-validation

DEPLOYED_VERSION_CMD=$(CF_COLOR=false cf apps | grep $CF_APP- | cut -d" " -f1)
DEPLOYED_VERSION="$DEPLOYED_VERSION_CMD"
ROUTE_VERSION=$(echo "${BUILD_NUMBER}" | cut -d"." -f1-3 | tr '.' '-')
echo "Deployed Version: $DEPLOYED_VERSION"
echo "Route Version: $ROUTE_VERSION"

# push a new version and map the route
cf push "$CF_APP-$BUILD_NUMBER" -n "$CF_APP-$ROUTE_VERSION" -i 2 -p $CF_JAR --no-start
cf bind-service "$CF_APP-$BUILD_NUMBER" config-server
cf bind-service "$CF_APP-$BUILD_NUMBER" discovery-service
cf bind-service "$CF_APP-$BUILD_NUMBER" circuit-breaker-dashboard
cf bind-service "$CF_APP-$BUILD_NUMBER" mysql

cf map-route "$CF_APP-${BUILD_NUMBER}" $CF_APPS_DOMAIN -n $CF_APP
cf start "$CF_APP-$BUILD_NUMBER"

if [ ! -z "$DEPLOYED_VERSION" -a "$DEPLOYED_VERSION" != " " -a "$DEPLOYED_VERSION" != "$CF_APP-${BUILD_NUMBER}" ]; then
  echo "Performing zero-downtime cutover to $BUILD_NUMBER"
  echo "$DEPLOYED_VERSION" | while read line
  do
    if [ ! -z "$line" -a "$line" != " " -a "$line" != "$CF_APP-${BUILD_NUMBER}" ]; then
      echo "Scaling down, unmapping and removing $line"
      # Unmap the route and delete
      cf unmap-route "$line" $CF_APPS_DOMAIN -n $CF_APP
    else
      echo "Skipping $line"
    fi
  done
fi

APP_NAME="$CF_APP-$BUILD_NUMBER"
URL="$(cf app $APP_NAME | grep URLs| cut -c7-)"

pt comment $TASK_NUMBER "Build and Deploy for Dev Space. Use this URL: $URL"
pt finish $TASK_NUMBER
```
##Plus Test Hystrix Monitor
```
while true; do curl https://dev-app-url/testHystrix?error=something; done
```
##Plus Test Config Server
Update application.yml https://github.com/youraccount/pcf-ers-demo1-config
Change infoMessage for any message
```
curl -X POST https://dev-app-url/refresh
```

