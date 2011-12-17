package ca.cutterslade.securerest.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/bundle/list")
public class BundleListResource {

  @GET
  @Produces("text/plain")
  public String getBundleList() {
    return "list";
  }
}
