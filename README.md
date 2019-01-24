# Java Apigee Utility
A standalone Java application that is built with Maven to leverage the Apigee Management API.

### Pre-requisites
- Java 8
- Maven

### Setup

To run the application, you will need to package the JAR file using Maven:

```bash
$ mvn package
```

### Running the Application

To execute the packaged JAR, you will need to run the following command:

```bash
$ java -jar apigee-utility.jar 
```

To use the application follow the prompts. E.g. 

```bash
$ $ java -jar target/apigee-utility.jar

  Usage: action [-hV] [COMMAND]
  action to be performed
    -h, --help      Show this help message and exit.
    -V, --version   Print version information and exit.
  Commands:
    login      login to the Apigee API
    proxy      proxy action to be performed
    product    product action to be performed
    app        app action to be performed
    developer  developer action to be performed
```

Examples of the files needed to use this application can be found in the examples folder.

### Usage

The following commands are examples of how to use the application:

##### Login
To authenticate yourself and retrieve a token to perform all requests, execute the following command:

```bash
$ java -jar apigee-utility.jar login -o {organisationName} -e {emailAddress} -p
```

_NOTE: the password field entry is interactive so there is no need to enter the password after the -p argument._

#### Proxy Commands

##### Import
To import a proxy from a ZIP file containing xml files with an API Proxy configuration, execute the following command:

```bash
$ java -jar apigee-utility.jar proxy import -n {proxyName} -l {zipFileLocation} -t {token}
```

##### Deploy
To deploy a specific revision of a registered API Proxy to a specific environment for an organization, execute the following command:

```bash
$ java -jar apigee-utility.jar proxy deploy -n {proxyName} -r {revisionNumber} -e {environment} -t {token}
```

##### Undeploy
To undeploy a specific revision of a registered API Proxy to a specific environment for an organization, execute the following command:

```bash
$ java -jar apigee-utility.jar proxy undeploy -n {proxyName} -r {revisionNumber} -e {environment} -t {token}
```

##### Delete
To delete a registered API Proxy from an organization, execute the following command:

```bash
$ java -jar apigee-utility.jar proxy delete -n {proxyName} -t {token}
```

#### Product Commands

##### Create
To create an API Product for an organization from a JSON file containing the required configuration, execute the following command:

```bash
$ java -jar apigee-utility.jar product create -l {jsonFileLocation} -t {token}
```

An example of a JSON file that will be valid is seen below:
```json
{
  "name" : "from_the_util",
  "displayName": "Util Test Product",
  "approvalType": "auto",
  "attributes": [
    {
      "name": "access",
      "value": "public"
    }
  ],
  "description": "This is a test API Product",
  "apiResources": [ "/test", "/dev/test" ],
  "environments": [ "dev", "prod" ],
  "proxies": ["test_from_util"]
}
```

##### Delete
To delete a registered API Product from an organization, execute the following command:

```bash
$ java -jar apigee-utility.jar product delete -n {productName} -t {token}
```

#### Developer Commands

##### Create
To create a developer for an organization from a JSON file containing the required configuration, execute the following command:

```bash
$ java -jar apigee-utility.jar developer create -l {jsonFileLocation} -t {token}
```

An example of a JSON file that will be valid is seen below:
```json
{
 "email" : "user@somewhere.com",
 "firstName" : "User",
 "lastName" : "Name",
 "userName" : "user"
}
```

##### Delete
To delete a registered developer from an organization, execute the following command:

```bash
$ java -jar apigee-utility.jar developer delete -e {developerEmail} -t {token}
```

#### App Commands

##### Create
To create an app for a registered developer for an organization from a JSON file containing the required configuration, execute the following command:

```bash
$ java -jar apigee-utility.jar app create -e {developerEmail} -l {jsonFileLocation} -t {token}
```

An example of a JSON file that will be valid is seen below:
```json
{
 "name" : "user_app",
 "apiProducts": [ "from_the_util" ],
 "attributes" : [
  {
   "name" : "DisplayName",
   "value" : "Users App"
  }
 ]
}
```

##### Delete
To delete an app registered to a developer for an organization, execute the following command:

```bash
$ java -jar apigee-utility.jar app delete -e {developerEmail} -n {appName} -t {token}
```

### Advanced Usage

If you need to execute all HTTP requests through an external proxy, this can be done for all commands by supplying the hostname and port number as arguments.

```bash
$ java -jar apigee-utility.jar login -o {organisationName} -e {emailAddress} -p --proxy-host http://zaproxy.mtn.com
$ java -jar apigee-utility.jar import -n {proxyName} -l {zipFileLocation} -t {token} --proxy-host http://zaproxy.mtn.com --proxy-port 9090
```
_NOTE: if the proxy port argument is not supplied with the proxy host argument, the port number will default to 8080._

### Want to contribute? 
Feel free to open a Pull Request and we can improve this little utility!

