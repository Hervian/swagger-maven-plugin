package com.github.hervian.swagger.mojos;

import com.github.hervian.swagger.compilation.ClassFileCopier;
import com.github.hervian.swagger.config.PropertiesReader;
import com.github.hervian.swagger.services.SwaggerDocJaxRsResource;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Mojo(name = "generateDoc",
    requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, defaultPhase = LifecyclePhase.COMPILE, threadSafe = true)
public class GenerateDocMojo extends AbstractMojo {
    private final Logger LOGGER = LoggerFactory.getLogger(GenerateDocMojo.class);

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Component
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager pluginManager;

    @Parameter(required = true)
    private List<String> resourcePackages;

    public enum AdditionalDoc { //https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator/src/main/java/org/openapitools/codegen/languages
        NONE,
        HTML,
        HTML2,
        CWIKI //Confluence wiki docs
    }
    @Parameter(defaultValue = "HTML2")
    private List<AdditionalDoc> additionalDocs;


    @Override
    public void execute() throws MojoExecutionException {
      validatePhaseAndGoals();

      generateSwaggerDoc();
      copySwaggerDocToBuildOutputDir();
      copySwaggerDocJaxRsResourceToBuildOutputDir();

      if (additionalDocs!=null){
          for (AdditionalDoc additionalDoc: additionalDocs){
              transformSwaggerDocIntoAdditionalDoc(additionalDoc);
          }
      }
    }

    //TODO: Either copy-paste to jar/build artifacts or attach as a separate jar'ed artifact using https://maven.apache.org/plugins/maven-jar-plugin/examples/attached-jar.html
    private void transformSwaggerDocIntoAdditionalDoc(AdditionalDoc additionalDoc) throws MojoExecutionException {
        executeMojo(
            plugin(
                groupId("org.openapitools"),
                artifactId("openapi-generator-maven-plugin"),
                version("5.3.0") //TODO get values from PropertiesReader
            ),
            goal("generate"),
            configuration(
                element(name("inputSpec"), project.getBuild().getOutputDirectory()+"/swagger/swagger.json"),
                element(name("generatorName"), additionalDoc.name().toLowerCase()),
                element(name("output"), project.getBuild().getDirectory()+"/generated-sources/openapi")
            ),
            executionEnvironment(
                project,
                mavenSession,
                pluginManager
            )
        );
    }

    /**
     * Validate that
     * <ul>
     *   <li>the configured phase, if any, of present execution is either null (=default) or at least LifecyclePhase.COMPILE and at most LifecyclePhase.prepare-package
     * </ul>
     */
    private void validatePhaseAndGoals() throws MojoExecutionException {
        PropertiesReader propertiesReader = null;
        String propertiesFromPomFile = "properties-from-pom.properties";
        try {
            propertiesReader = new PropertiesReader(propertiesFromPomFile);
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to load the build info file " + propertiesFromPomFile, e);
        }
        for (PluginExecution pluginExecution: project.getPlugin(String.format("%s:%s", propertiesReader.getGroupId(), propertiesReader.getArtifactId())).getExecutions()){ //Consider using maven-replacer-plugin https://gist.github.com/4n3w/3365657
            List<String> listOfGoals = pluginExecution.getGoals();
            System.out.println(listOfGoals);
        }
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
                version("2.1.6")//TODO: get values from PropertiesReader
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

    /**
     * Finally, copy all the generated resources over to the build output folder because
     * we run after the "process-resources" phase and Maven no longer handles the copying
     * itself in these late phases. Based on this answer: https://stackoverflow.com/a/49724374/6095334
     * @throws MojoExecutionException
     */
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

    private String getSwaggerDocDir(){
        return project.getBuild().getDirectory()+"/generated-resources/swagger";
    }

    private void copySwaggerDocJaxRsResourceToBuildOutputDir() throws MojoExecutionException {
        ClassFileCopier classFileCopier = ClassFileCopier.
            builder()
            .log(getLog())
            .project(project)
            .resourcePackages(resourcePackages)
            .build();
        classFileCopier.copyResourceToBuildOutputDir(SwaggerDocJaxRsResource.class);//"/SwaggerDocJaxRsResource.java");
    }
    /*private void copySwaggerDocJaxRsResourceToBuildOutputDir() throws MojoExecutionException {
        //TODO: https://stackoverflow.com/a/2946402/6095334, https://stackoverflow.com/q/9665768/6095334, https://github.com/trung/InMemoryJavaCompiler
        // 1: load copy of source file from top level of jar and edit package name
        // 2: Compile edited code to a .class file, see https://stackoverflow.com/questions/4463440/compile-java-source-code-from-a-string

        // 4: Save to build output dir

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        //URL inputUrl = classloader.getResource("com/github/hervian/swagger/GetSwaggerUiResource.class");
        //InputStream sourceCodeStream = classloader.getResourceAsStream("/SwaggerDocJaxRsResource.java");
        InputStream sourceCodeStream = getClass().getResourceAsStream("/SwaggerDocJaxRsResource.java");
        try {
            String sourceCode = IOUtils.toString(sourceCodeStream, StandardCharsets.UTF_8.name());
            System.out.println(sourceCode);

            sourceCode = addAnnotationsIfFoundOnClassPath(sourceCode);
            String newPackage = resourcePackages.get(0);
            sourceCode = editPackage(sourceCode, newPackage);
            System.out.println(sourceCode);
            String outputDirOfSwaggerDocJaxRsResource = project.getBuild().getOutputDirectory();// + "/"+ newPackage.replace(".", "/");
            StringToFileCompiler.compile(sourceCode, outputDirOfSwaggerDocJaxRsResource);

        *//*File dest = new File(project.getBuild().getOutputDirectory());

            FileUtils.copyStreamToFile(new InputStreamFacade() {

                @Override
                public InputStream getInputStream() throws IOException
                {
                    return inputUrl.openStream();
                }
            }, dest);
        *//*
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
    }*/
}
