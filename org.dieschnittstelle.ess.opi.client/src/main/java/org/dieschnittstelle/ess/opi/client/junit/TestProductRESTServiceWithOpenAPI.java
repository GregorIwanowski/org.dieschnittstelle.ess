package org.dieschnittstelle.ess.opi.client.junit;

// TODO: entfernen Sie die auskommentierten Codezeilen, nachdem Sie nach Umsetzung der server-seitigen
//  Entwicklunsmassnahmen fuer OPI1 erstmalig die client-seitigen Klassen fuer den Zugriff auf die WebAPI
//  generiert haben. Falls Ihre imports automatisch aktualisiert werden, dann entfernen Sie erst die Kommentare
//  aus der Implementierung der Klasse und kommentieren Sie die imports erst danach ein.

import org.dieschnittstelle.ess.opi.client.model.Campaign;
import org.dieschnittstelle.ess.opi.client.model.IndividualisedProductItem;
import org.dieschnittstelle.ess.opi.client.model.ProductBundle;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TestProductRESTServiceWithOpenAPI {
	
	private ProductCRUDOpenAPIClient client;

	private IndividualisedProductItem PRODUCT_1 = new IndividualisedProductItem();
	private IndividualisedProductItem PRODUCT_2 = new IndividualisedProductItem();
	private Campaign CAMPAIGN = new Campaign();

	@Before
	public void prepareContext() throws Exception {
	    // prepare the client
		client = new ProductCRUDOpenAPIClient();

		// prepare the products
		PRODUCT_1.setName("Schrippe");
		PRODUCT_1.setProductType(IndividualisedProductItem.ProductTypeEnum.ROLL);
		PRODUCT_1.setExpirationAfterStocked(720);

		PRODUCT_2.setName("Kirschplunder");
		PRODUCT_2.setProductType(IndividualisedProductItem.ProductTypeEnum.PASTRY);
		PRODUCT_2.setExpirationAfterStocked(1080);

		ProductBundle b1 = new ProductBundle();
		b1.setUnits(10);
		b1.setProduct(PRODUCT_1);
		ProductBundle b2 = new ProductBundle();
		b2.setUnits(3);
		b2.setProduct(PRODUCT_2);
		CAMPAIGN.setBundles(Arrays.asList(b1,b2));
	}

	@Test
	public void crudWorks() {
		List<?> prodlistBefore;
		// read all products
		assertNotNull("product list can be read",prodlistBefore = client.readAllProducts());

		/* CREATE + READ */
		// create two products
		client.createProduct(PRODUCT_1);
		client.createProduct(PRODUCT_2);

		assertEquals("product list is appended on create",2,client.readAllProducts().size()-prodlistBefore.size());

		// read the products and check whether they are equivalent
		IndividualisedProductItem testProduct = client.readProduct(PRODUCT_1.getId());

		assertNotNull("new product can be read",testProduct);
		assertEquals("new product name is correct",PRODUCT_1.getName(),testProduct.getName());

		/* UPDATE */
		// change the local name
		PRODUCT_1.setName(PRODUCT_1.getName() + " " + PRODUCT_1.getName());
		// update the product on the server-side
		client.updateProduct(PRODUCT_1);

		// read out the product and compare the names
		testProduct = client.readProduct(PRODUCT_1.getId());
		assertEquals("product name is updated correctly",PRODUCT_1.getName(), testProduct.getName());

		/* DELETE */
		assertTrue("product can be deleted",  client.deleteProduct(PRODUCT_1.getId()));
		// given the dependencies from the generator, the following test results in a NoSuchMethodError as
		// int javax.ws.rs.core.Response.getLength() is not available - which is supposedly due to the
		// null response we receive here
//		assertNull("deleted product does not exist anymore", client.readProduct(PRODUCT_1.getId()));
		assertEquals("product list is reduced on delete",prodlistBefore.size()+1,client.readAllProducts().size());

		/* this is for internally testing that campaigns can be written and read via the web api - not part of the exercise */
		Campaign campaign = (Campaign)client.createCampaign(CAMPAIGN);
		assertNotEquals("campaign can be created",(int)campaign.getId(),0);
		assertEquals("campaign contains bundles",campaign.getBundles().size(),2);
		assertNotNull("campaign bundles contain products",campaign.getBundles().get(0).getProduct());
	}

}
