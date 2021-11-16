package com.github.hervian.swagger.generators;

/*import lombok.Builder;
import lombok.Data;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

@Builder
@Data*/
public class GenerateSwaggerDoc {

  /*private MavenProject project;
  private MavenSession mavenSession;
  private BuildPluginManager pluginManager;
  private List<String> resourcePackages;
  private String swaggerDocDir;

  public void execute() throws MojoExecutionException {
    generateSwaggerDoc();
    copySwaggerDocToBuildOutputDir();
  }

  private void generateSwaggerDoc() throws MojoExecutionException {
    MojoExecutor.Element[] resourcePackagesAsElements = new MojoExecutor.Element[resourcePackages.size()];
    for (int i=0; i<resourcePackages.size(); i++){
      resourcePackagesAsElements[i]=(element(name("resourcePackage"), resourcePackages.get(i)));
    }

    executeMojo(
        plugin(
            groupId("io.openapitools.swagger"),
            artifactId("swagger-maven-plugin"),//https://github.com/openapi-tools/swagger-maven-plugin
            version("2.1.6")
        ),
        goal("generate"),
        configuration(
            element(name("outputDirectory"), getSwaggerDocDir()),
            element(name("outputFilename"), "swagger"),
            element(name("outputFormats"), "JSON"),
            element(name("prettyPrint"), "true"),
            element(name("resourcePackages"), resourcePackagesAsElements)
        ),
        executionEnvironment(
            project,
            mavenSession,
            pluginManager
        ));
  }

  *//**
   * Finally, copy all the generated resources over to the build output folder because
   * we run after the "process-resources" phase and Maven no longer handles the copying
   * itself in these late phases. Based on this answer: https://stackoverflow.com/a/49724374/6095334
   * @throws MojoExecutionException
   *//*
  private void copySwaggerDocToBuildOutputDir() throws MojoExecutionException {
    try {
      File swaggerFile = new File(getSwaggerDocDir()+"/swagger.json");
      File swaggerFileDestination = new File(project.getBuild().getOutputDirectory()+"/swagger/swagger.json");
      System.out.println("swaggerFileDestination.getAbsolutePath(): "+swaggerFileDestination.getAbsolutePath());

      FileUtils.copyFile(swaggerFile, swaggerFileDestination);
    }
    catch (IOException e) {
      throw new MojoExecutionException("Unable to copy generated resources to build output folder", e);
    }
  }
*/
}
