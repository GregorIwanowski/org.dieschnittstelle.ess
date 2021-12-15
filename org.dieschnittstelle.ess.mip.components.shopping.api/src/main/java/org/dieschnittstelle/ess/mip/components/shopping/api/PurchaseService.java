package org.dieschnittstelle.ess.mip.components.shopping.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

// TODO: PAT1: this is the interface to be provided as a rest service if rest service access is used
@Path("/purchase")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface PurchaseService {

	@POST
	void purchaseCartAtTouchpointForCustomer(@QueryParam("shoppingCartId") long shoppingCartId, @QueryParam("touchpointId") long touchpointId, @QueryParam("customerId") long customerId) throws ShoppingException;
	
}
