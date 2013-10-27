# Development

This file documents the development process and cycles for this
project, how to contribute through Git, and how to use the Maven build
process.


Source repository
-----------------
The source repository is on GitHub, at http://github.com/expath/expath-pkg. 


Building EXPath PKG from Source
-------------------------------
PKG itself is written predominantly in Java 6. The build system is [Maven](http://maven.apache.org/ "The Apache Maven Project").

To build EXPath PKG:

- Checkout the Git Repository
- Execute `mvn package`

```bash
$ git clone git@github.com:expath/expath-pkg.git
$ cd expath-pkg-java
$ mvn package
```

**HINT:** 
In the example above, we use the SSH form of the GitHub repo URL to clone PKG. However, if you're behind a HTTP proxy and your organisation doesn't allow outgoing SSH connections, try the HTTPS URL for our GitHub repo <https://github.com/expath/expath-pkg.git>.

From here, you now have a compiled version of PKG.


Contributing to EXPath
----------------------
We welcome all contributions to EXPath! 

We strongly suggest that you join the [EXPath Mailing List](http://groups.google.com/group/expath "EXPath Mailing List") list, so that you can collaborate with the EXPath community.

If you wish to contribute, the general approach is:

- Fork the repo on GitHub 
- `git clone` your fork to your machine
- Do your stuff! :-)
- Commit to your repo. We like small, atomic commits that don't mix concerns!
- Push to your GitHub
- Send us a Pull Request

Pull Requests are reviewed and tested before they're merged by the core development team.
However, we have one golden rule, adhered to even by the project founders: **never merge your own pull request**. This simple-but-important rule ensures that at least two people have considered the change. 

The main things that get a Pull Request accepted quickly are:

- **Only change what you need to.** If you must reformat code, keep it in a separate commit to any syntax or functionality changes.
- **Test.** If you fix something prove it, write a test that illustrates the issue before you fix the issue and validate the test. If you add a new feature it needs tests, so that we can understand its intent and try to avoid regressions in future as much as possible.
- **Make sure the appropriate licence header appears at the top of your source code file.** We exclusively use [MPL v1.0](http://opensource.org/licenses/MPL-1.0 "The Mozilla Public License (MPL), version 1.0 (MPL-1.0)") for EXPath.
- **Run the full the test suite!** We don't accept code that causes regressions.


Build Management with Maven
===========================
If you are not familiar with Maven, we cover a few topics here that will help you work with EXPath PKG.

Tests
-----
Maven's SureFire plugin will run any JUnit tests placed under `src/test/java` automatically during its build lifecycle, as such simply adding tests to packages in `src/test/java` is enough to incorporate them into the EXPath PKG test-suite. 

If you wish to just run the test-suite you can just execute `mvn test`.

For further details on Maven's build lifecycle see http://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#Lifecycle_Reference.


Version Numbers
---------------
Artifacts produced by maven are always postfixed with a version number. For specific version numbers, the EXPath project follows the [Semantic Versioning Scheme](http://www.semver.org). Maven has two types of versioning, ***RELEASE*** and ***SNAPSHOT***:

- ***RELEASE***: Is a concrete release e.g. version `2.1`. For example for the `expath-pkg-java` module this would give you the artifact `expath-pkg-java-2.1.jar`.

- ***SNAPSHOT***: Is a development version release. e.g. You may specify the version `2.0-SNAPSHOT` to Maven, which is interpreted as a version which will eventually become `2.0`. When you do an actual build of a SNAPSHOT, Maven injects a timestamp into the version number to differentiate one development build version from another. For example for the `expath-pkg-java` module, with version number `2.0-SNAPSHOT`, if you were to ***release*** the snapshot you may get the artifact `expath-pkg-java-1.0-20131027.124609-1.jar`.

Whilst the EXPath PKG project is a multi-module project, the version number of the EXPath PKG is common for all modules in the project. As the entire project is released, rather than individual modules. As such the version number can be found in `expath-pkg-parent/pom.xml`. ***NOTE***: The Version Number should not be manually changed, rather the Maven Release plugin should be used (see [Release Process](#release-process) below).


Release Process
---------------
A release may only be done from the main EXPath PKG repository, as such you will need write access to that GitHub repository. Releasing also involves uploading the release artifacts to Maven Central which will be taken care of for you by Maven, however you do need to have setup your GPG key and configured your Maven `settings.xml` with your Maven Central account, see steps (*2*), (*3*) and (*7a.1 settings.xml*) here: https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide.

To create an EXPath PKG release, you may use the Maven Release plugin which is already configured for the project. FYI - performing a release will take the following steps for you:

- Set the version number of the release
- Build and Test
- Tag the release in Git and push to GitHub
- Bump the version numbers in the pom.xml's commit to `master` branch in Git and push to GitHub.
- Upload the release artifacts to Maven Central.

The Maven Release plugin is intelligent, and will prompt you for any answers that it needs. However before use it is strongly recommended that you read it's [documentation](http://maven.apache.org/maven-release/maven-release-plugin/) so that you understand what it is doing for you!
To actually perform a release simply run the following steps:

```bash
$ mvn release prepare
```

If something goes wrong, you should rollback by running:
```bash
$ mvn release rollback
```

Otherwise you can complete the release by running:

```bash
$ mvn release perform
```


Building Offline
----------------
Maven will download on demand the dependencies of the EXPath PKG project, a copy of these will be placed in your local repository which is `~/.m2/repository` on Linux/Unix/Mac platforms or `%USERPROFILE%/.m2/repositorty` on Windows platforms. Once these are downloaded, they will not be retrieved again. However Maven will check occasionally whether newer versions are available. If you are working offline you can pass the `-O` option to `mvn` to stop it from trying to download dependencies from remote sources or checking for updates, for example:

```bash
$mvn -O package
```


Working behind a Proxy Server
-----------------------------
Maven uses HTTP (or HTTPS) to download the depenencies needed for the EXPath PKG project, if you are behind a proxy server (typical in a corporate environment) you need to tell Maven to send its traffic through your proxy server. This is done by putting the proxy server details into Maven's `settins.xml` file. The settings.xml file is in `~/.m2/settings.xml` on Linux/Unix/Mac platforms or `%USERPROFILE%/.m2/settings.xml` on Windows platforms. For example:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
	<proxies>
   		<proxy>
      		<active>true</active>
      		<protocol>http</protocol>
      		<host>proxy.somewhere.com</host>
      		<port>8080</port>
      		<username>proxyuser</username>		<!-- optional, remove if you do not need to authenticate -->
      		<password>somepassword</password>	<!-- optional, remove if you do not need to authenticate -->
      		<nonProxyHosts>*.somewhere.com</nonProxyHosts>	<!-- optional, any internal maven repository addresses, remove if you do not need -->
    	</proxy>
  	</proxies>
</settings>
```


Further Information
-------------------
Maven should make life simple, if you are struggling, chances are that something is wrong! For further details on Maven, there are some excellent free books by Sonatype: http://www.sonatype.com/resources/books.
