package org.dieschnittstelle.ess.mip.components.shopping.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

// TODO: PAT1: this is the interface to be provided as a rest service if rest service access is used
@Path("/purchase")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface PurchaseService {

	@POST
	void purchaseCartAtTouchpointForCustomer(long shoppingCartId, long touchpointId, long customerId) throws ShoppingException;
	
}
