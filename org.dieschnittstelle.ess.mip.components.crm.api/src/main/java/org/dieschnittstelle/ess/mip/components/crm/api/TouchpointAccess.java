package org.dieschnittstelle.ess.mip.components.crm.api;

import java.util.List;

import org.dieschnittstelle.ess.entities.crm.AbstractTouchpoint;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@RegisterRestClient
@Path("/touchpoints")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@WebService
public interface TouchpointAccess {

	@POST
	public AbstractTouchpoint createTouchpointAndPointOfSale(AbstractTouchpoint touchpoint) throws CrmException;

	@GET
	public List<AbstractTouchpoint> readAllTouchpoints();

	@GET
	@Path("/{id}")
	public AbstractTouchpoint readTouchpoint(@PathParam("id") long id);
	
}
