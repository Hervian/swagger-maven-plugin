package com.github.hervian.swagger;

/*import com.github.hervian.swagger.generators.GenerateSwaggerDoc;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.io.InputStreamFacade;
import org.webjars.WebJarAssetLocator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;*/

//TODO extract configuration to annotation, which we scan for. See https://stackoverflow.com/a/18688328/6095334
//Alternatively see the use of @OpenAPIDefinition in the openapi swagger-maven-plugin - they must scan for it.
/*@Mojo(name = "generateSwaggerDocAndSwaggerUi", defaultPhase = LifecyclePhase.COMPILE, configurator = "include-project-dependencies",
    requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)*/
public class GenerateSwaggerDocAndSwaggerUi{
    /*extends AbstractMojo {

  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  MavenProject project;

  @Parameter
  private List<String> resourcePackages;

  @Component
  private MavenSession mavenSession;

  @Component
  private BuildPluginManager pluginManager;

 *//* @Parameter(property = "generateSwaggerUi", required = true)
  private boolean generateSwaggerUi;

  @Parameter(property = "generateClient", required = true)
  private boolean generateClient;*//*

  //TODO: Consider if anything should be done client side to support CORS
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    //Please notice that the Mojo GenerateSwaggerDocResourceSourceCode has run in a previous phase, namely generate-source. That mojo creates the jax-rs class that is able to serve A) the swagger doc produced by present mojo and B) the swagger-ui index.html (and related css and javascript files)
    for (PluginExecution pluginExecution: project.getPlugin("com.github.hervian:all-in-one-swagger-maven-plugin").getExecutions()){ //Consider using maven-replacer-plugin https://gist.github.com/4n3w/3365657
      List<String> listOfGoals = pluginExecution.getGoals();
      System.out.println("Execution id = "+pluginExecution.getId());
      System.out.println("Phase = "+pluginExecution.getPhase());
      System.out.println("goals = "+listOfGoals);
    }
    //generateSwaggerDoc(); //TODO: add Parameter: autoUpdateSwaggerDocVersionNumber
    //TODO: generateSwaggerDocJaxRsResource (create Parameter to control creation and break build if generateSwaggerDoc=true and generateSwaggerDocJaxRsResource=false and generateSwaggerUi=true)
    //TODO: add Parameter and check: failBuildIfBreakingChangesAreNotReflectedByAnIncreaseInPomVersionNumber

    *//*if (generateSwaggerUi){
      //TODO: Make it possible to conditionally exclude swagger-ui from say prod env? https://www.baeldung.com/swagger-ui-turn-off-in-production
     // copySwaggerUiFilesToBuildOutputDir();
      //TODO: generateSwaggerUiJaxRsResource
    }*//*

    *//*if (generateClient){ //TODO: Conditional on whether or not there is an increase in major version - keep record of previous releases.
      generateAndDeployClient(); //Split - deploy must be done in deploy phase ... i.e. plugin user must add that as a separate goal and the deploy logic must exist in that plugin. But the boolean conf is probably ok.
    }*//*
    //deploy: https://urldefense.com/v3/__https://stackoverflow.com/questions/40464363/deploying-multiple-files-in-single-pom__;!!GNpmbAs!UxwsbqGbXWKOIDiAtMMX9p-qVUItEKaPiHbMyyep3c5y7oIq-ipejKjMGQ24UDU$
  }

 *//* private void generateSwaggerDoc() throws MojoExecutionException {
    getLog().info("generateSwaggerDoc");
    GenerateSwaggerDoc
        .builder()
          .project(project)
          .mavenSession(mavenSession)
          .pluginManager(pluginManager)
          .resourcePackages(resourcePackages)
          .swaggerDocDir(getSwaggerDocDir())
        .build()
        .execute();
    getLog().info("swagger.json created annd written to: " + getSwaggerDocDir());
  }*//*


*/
}
