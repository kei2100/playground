package playground.jax_rs.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

import playground.jax_rs.element.GenericElement;

@Component
@Path("/pathtest/{a}")
public class PathTestResource {
	
	@Path("/test/{b}")
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public GenericElement get(
			@PathParam("a") String a,
			@PathParam("b") String b) {
		
		return new GenericElement(String.format("a=%s,b=%s", a, b));
	}
}
