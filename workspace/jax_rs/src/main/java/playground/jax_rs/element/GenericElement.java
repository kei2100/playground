package playground.jax_rs.element;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="data")
public class GenericElement {
	
	private String message = "";
	
	public GenericElement() {
	}
	
	public GenericElement(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
