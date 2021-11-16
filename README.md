# swagger-maven-plugin
This plugin is intended to be a one-stop-shop for everything swagger (OpenAPI) related. 
No dependencies are needed - you only need to add the plugin and let the code generation it makes use of do its work.

What you can/can configure:
* generation of swagger.json file (mandatory)
* generation of static documentation such as HTML and Confluence wiki markup text. (This is useful if you want your build to deploy the REST documentation to your company's Confluence server)
* Breaking API changes check - will fail the build if there are breaking REST API changes AND the major version of the pom has not been increased.
* generation of swagger-ui (optional. Also: the swagger-ui files can be customized)
* generation and deploy of client code (optional. I.e. the plugin can be configured to create and deploy (to say Nexus) a Java client when calling 'mvn clean deploy').

<pre>
	Description: One stop shop for documenting your java REST server: embedded swagger-ui + compile time generation of swagger.json + compile time generation of java client as separate jar.
		Compile time generate swagger doc + optional html
			Why compile time? Because
				a) otherwise tools that need this are dependent on a live server, that may be down.
				b) Some companies secure their build servers by only allowing them to access binary repositories, i.e. they would block a connection to retrieve the swagger.json (which you may need to build, say a javascript client for your server).
		Optionally deploy swagger-ui
		Optionally generate a java client for your server as a separate binary artifact.
			Example, when running 'mvn clean deploy' on your server code, you can configure the plugin to also create a client jar for that server, which will also be deployed to say Nexus.
		Configuration of all of the above and the exact behavior can be controlled via an annotation in your source code, which the plugin will find via annotation scanning.
	Plugin:
		Execution 1: compile phase
			Scan for configuration annotation/class: https://stackoverflow.com/q/18634850
			Generate swagger.json
		Execution 2, process-classes phase?
			if (Configuration.generateClient && semanticVersioningIndicatesNewAPI)
				generate client code from swagger.json
				mvn [inherited mvn options] client code.
</pre>

## Table of Contents  
[Releases](#releases)  
[Related projects](#related-projects)  

## Releases
Available in TODO:  

Requires **Java 8** or above.
See requirements section if you need to use this library with *Java 8*.
 

## Configuration
Example configuration:  
```
    <plugin>
        <groupId>com.github.hervian</groupId>
        <artifactId>swagger-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <configuration>
          <resourcePackages>
            <resourcePackage>dk.tdc.chub.exposure.v1</resourcePackage>
          </resourcePackages>
          <fileCustomizer>dk.tdc.chub.config.SwaggerUiCustomizer</fileCustomizer>
          <additionalDocs>
            <additionalDoc>CWIKI</additionalDoc>
          </additionalDocs>
        </configuration>
        <executions>
          <execution>
            <goals>
              <goal>generateDoc</goal> <!--Mandatory goal. Bound to Compile phase. Optional configuration: 1) Add @io.swagger.v3.oas.annotations.OpenAPIDefinition annotation anywhere in your source code to customize the generated swagger doc. 2) Add other swagger annotations to your REST methods to add detailed documentation (@Api, @ApiOperation etc). Note that both requires that you add the relevant dependency to get those annotations on the classpath if not already present. -->
              <goal>generateUi</goal> <!--Optional goal. Bound to Compile phase. -->
              <goal>generateClient</goal> <!--Optional goal. Bound to Deploy phase. TODO: Optionally add ...-->
            </goals>
          </execution>
        </executions>
      </plugin>
```

## Related projects
See pom for which projects are used under the hood (since present plugin delegates all 
the heavy lifting to other plugins).
* TODO list swagger-codegen, swagger-maven-plugins
