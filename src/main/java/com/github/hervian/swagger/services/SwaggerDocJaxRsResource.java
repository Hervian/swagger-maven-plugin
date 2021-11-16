package com.github.hervian.swagger.services;

//import org.springframework.stereotype.Component;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;
import io.swagger.annotations.ApiOperation; //TODO: add dependencies to plugin to get type safety in GenerateDocMojo
//ConditionalOnProperty:import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

//ConditionalOnProperty:@ConditionalOnProperty(value = "swagger.ui.disabled", havingValue = "false", matchIfMissing = true) //Give Spring users an option to exclude swagger-ui from, say deployments to prod environment by setting swagger.ui.disabled=true in application-prod
//@Component
@Path("/doc") //TODO Insert configurable base path? If done, remember to update GenerateSwaggerDocMojo which has a hard coded reference to 'doc' when specifying the url of the swagger.json in the index.html
public class SwaggerDocJaxRsResource {

  @Context
  UriInfo uriInfo;

  @ApiOperation(value = "hidden", hidden = true)
  @GET
  @Path("swagger/swagger.json")
  public InputStream getSwaggerDoc(){
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    String path = uriInfo.getPath().substring(uriInfo.getPath().indexOf("/")+1);
    InputStream resource = classloader.getResourceAsStream("swagger/swagger.json");
    System.out.println("swagger.json resource!=null: "+Boolean.valueOf(resource!=null));
    return resource;
  }

}
