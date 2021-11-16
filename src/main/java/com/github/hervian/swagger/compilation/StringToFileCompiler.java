package com.github.hervian.swagger.compilation;

import com.github.hervian.swagger.services.SwaggerDocJaxRsResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Code is based on https://stackoverflow.com/a/17526282/6095334
 */
public class StringToFileCompiler {
  private static final Logger LOGGER = LoggerFactory.getLogger(StringToFileCompiler.class);

  public static boolean compile(String fileName, String code, String targetFolder) throws Exception {
    JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
    if( jc == null) throw new Exception( "Compiler unavailable");

    JavaSourceFromStringFileObject jsfs = new JavaSourceFromStringFileObject(fileName, code);

    Iterable<? extends JavaFileObject> fileObjects = Arrays.asList( jsfs);

    List<String> options = new ArrayList<String>();
    options.add("-d");
    options.add( targetFolder);
    options.add( "-classpath");
    URLClassLoader urlClassLoader =
        (URLClassLoader)Thread.currentThread().getContextClassLoader();
    StringBuilder sb = new StringBuilder();
    for (URL url : urlClassLoader.getURLs()) {
      sb.append(url.getFile()).append(File.pathSeparator);
    }
    sb.append( targetFolder);
    options.add(sb.toString());

    StringWriter output = new StringWriter();
    boolean success = jc.getTask( output, null, null, options, null, fileObjects).call();
    if( success) {
      LOGGER.info( "Class has been successfully compiled");
      return success;
    } else {
      throw new Exception( "Compilation failed :" + output);
    }
  }

}
