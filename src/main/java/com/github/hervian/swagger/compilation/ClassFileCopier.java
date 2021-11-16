package com.github.hervian.swagger.compilation;

import com.github.hervian.swagger.services.SwaggerDocJaxRsResource;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Builder
@Data
public class ClassFileCopier {
  private Log log;
  private List<String> resourcePackages;
  private MavenProject project;

  public void copyResourceToBuildOutputDir(Class<?> jaxRsResourceClass) throws MojoExecutionException {
    //TODO: https://stackoverflow.com/a/2946402/6095334, https://stackoverflow.com/q/9665768/6095334, https://github.com/trung/InMemoryJavaCompiler
    // 1: load copy of source file from top level of jar and edit package name
    // 2: Compile edited code to a .class file, see https://stackoverflow.com/questions/4463440/compile-java-source-code-from-a-string

    // 4: Save to build output dir
    String resource = String.format("/%s.java", jaxRsResourceClass.getSimpleName());//SwaggerDocJaxRsResource.class.getSimpleName()
    log.info("copying file: " + resource);
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    //URL inputUrl = classloader.getResource("com/github/hervian/swagger/GetSwaggerUiResource.class");
    //InputStream sourceCodeStream = classloader.getResourceAsStream("/SwaggerDocJaxRsResource.java");
    InputStream sourceCodeStream = getClass().getResourceAsStream(resource);
    try {
      String sourceCode = IOUtils.toString(sourceCodeStream, StandardCharsets.UTF_8.name());

      sourceCode = addAnnotationsIfFoundOnClassPath(sourceCode);
      String newPackage = resourcePackages.get(0);
      sourceCode = editPackage(sourceCode, newPackage);
      String outputDirOfSwaggerDocJaxRsResource = project.getBuild().getOutputDirectory();// + "/"+ newPackage.replace(".", "/");
      StringToFileCompiler.compile(jaxRsResourceClass.getSimpleName(), sourceCode, outputDirOfSwaggerDocJaxRsResource);

        /*File dest = new File(project.getBuild().getOutputDirectory());

            FileUtils.copyStreamToFile(new InputStreamFacade() {

                @Override
                public InputStream getInputStream() throws IOException
                {
                    return inputUrl.openStream();
                }
            }, dest);
        */
    } catch (Exception e) {
      getLog().error(e);
      throw new MojoExecutionException("Unable to copy generated resources to build output folder", e);
    }
  }

  private String editPackage(String sourceCode, String newPackage) {
    String oldPackage = SwaggerDocJaxRsResource.class.getPackage().getName();
    return sourceCode.replace(oldPackage, newPackage);
  }

  private String addAnnotationsIfFoundOnClassPath(String content) {
    boolean addApiOperationAnnotation = isOnClassPath("io.swagger.annotations.ApiOperation");
    boolean addConditionalOnPropertyAnnotation = isOnClassPath("org.springframework.boot.autoconfigure.condition.ConditionalOnProperty");
    boolean addGeneratedAnnotation = isOnClassPath("javax.annotation.Generated");

    content = addOrRemoveAnnotation(addApiOperationAnnotation, content, "ApiOperation");
    content = addOrRemoveAnnotation(addConditionalOnPropertyAnnotation, content, "ConditionalOnProperty");
    content = addOrRemoveAnnotation(addGeneratedAnnotation, content, "Generated");

    return content;
  }

  private boolean isOnClassPath(String fqcn) {
    try {
      ClassLoader classloader = Thread.currentThread().getContextClassLoader();
      Class.forName(fqcn, false, classloader);
      return true;
    } catch(ClassNotFoundException e) {
      return false;
    }
  }

  private String addOrRemoveAnnotation(boolean addAnnotation, String content, String simpelName) {
    if (addAnnotation) {
      content = content.replaceAll(String.format("//%s:", simpelName), "");
    } else {
      Matcher matcher = Pattern.compile(String.format(".*%s.*", simpelName), Pattern.MULTILINE).matcher(content);
      content = matcher.replaceAll("");
    }
    return content;
  }

}
