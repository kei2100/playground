package playground.jax_rs.resource;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import playground.jax_rs.element.GenericElement;

@Controller
@Path("/test")
public class TestResource {

	@Autowired
	ApplicationContext context;
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public GenericElement getTestMessage() {
		return 
			new GenericElement(context != null ? "context is not null" : "context is null");
	}
	
	@POST
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public GenericElement postTestMessage(
			@QueryParam("message") String message) {
		
		return 
			new GenericElement(message);
	}
}
