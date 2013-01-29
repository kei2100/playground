package playground.jax_rs.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Controller;

import playground.jax_rs.element.GenericElement;

@Controller
@Path("/hashcode")
public class HashCodeResource {
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public GenericElement getHashCode() {
		return new GenericElement(String.valueOf(this.hashCode()));
	}
}
