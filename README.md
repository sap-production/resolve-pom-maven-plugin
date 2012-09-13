# resolve-pom-maven-plugin

## Motivation

As you might know Maven always uploads the original source `pom.xml` into the local and remote repository and does not 
replace the property placeholders by the actual values used during the build.
However, sometimes it would be really useful if a resolved version would be uploaded. A typical usage scenario is to 
pass in the artifact version from the outside (e.g. by the central build server).


### Use Case: Project Version Injection

A common requirement is to create an artifact built with Maven where the version name or suffix is determined by the 
build job. The widely spread approach to achieve this non Maven like behavior is to us a variable in the `version` 
element of the POM as shown in the following example: 


```xml
<project>
  ...
  <parent>
    <groupId>my.group</groupId>
    <artifactId>parent</version>
    <version>1.0.${injectedBuildNr}</version>
  </parent>

  <artifactId>child</version>
  
  <properties>
    <injectedBuildNr>0-SNAPSHOT</injectedBuildNr>
  </properties>
  ...
</project>

```  

Now Maven gets called by the build system with `mvn clean deploy -DinjectedBuildNr=42` and everything seems to be working, 
as the artifact ends up at the correct location with the correct version in the local and remote repository. However, 
if you look inside the uploaded pom file you'll discover that the source POM file has been uploaded that still 
contains the `{$injectedBuildNr}` property. This will lead to errors when the artifact gets consumed by another 
Maven build as the dependency resolution will fail when searching for the parent with version `1.0.${injectedBuildNr}`.

By using the resolve-pom-maven-plugin the resolved pom will be uploaded and everything works as desired.


## Usage

The plugin provides just one goal named `resolve-pom-props` that creates a file named `resolvedPom.xml` that is located 
in the same directory as the original POM where all property placeholders got replaced by their actual values. This
resolved POM will be uploaded into the local and remote repository.

Just add the following build configuration to your (parent) POM and the plugin will be executed in your build lifecycle:

  
```xml
  <build>
    <plugins>
      <plugin>
        <groupId>com.sap.prd.mobile.ios.maven.plugins</groupId>
        <artifactId>resolve-pom-maven-plugin</artifactId>
        <version>1.0</version>
        <executions>
          <execution>
            <id>resolve-pom-props</id>
            <goals>
              <goal>resolve-pom-props</goal>
            </goals>              
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
```


