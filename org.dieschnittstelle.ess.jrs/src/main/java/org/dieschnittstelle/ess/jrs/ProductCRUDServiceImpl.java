package org.dieschnittstelle.ess.jrs;

import org.dieschnittstelle.ess.entities.GenericCRUDExecutor;
import org.dieschnittstelle.ess.entities.crm.StationaryTouchpoint;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import java.util.List;

/*
 * TODO JRS2: implementieren Sie hier die im Interface deklarierten Methoden
 */

public class ProductCRUDServiceImpl implements IProductCRUDService {

	private GenericCRUDExecutor<IndividualisedProductItem> productCRUD;

	public ProductCRUDServiceImpl(@Context ServletContext servletContext) {
		this.productCRUD = (GenericCRUDExecutor<IndividualisedProductItem>) servletContext.getAttribute("productCRUD");
	}

	@Override
	public IndividualisedProductItem createProduct(IndividualisedProductItem prod) {
		return this.productCRUD.createObject(prod);
	}

	@Override
	public List<IndividualisedProductItem> readAllProducts() {
		return this.productCRUD.readAllObjects();
	}

	@Override
	public IndividualisedProductItem updateProduct(long id, IndividualisedProductItem update) {
		return this.productCRUD.updateObject(update);
	}

	@Override
	public boolean deleteProduct(long id) {
		return this.productCRUD.deleteObject(id);
	}

	@Override
	public IndividualisedProductItem readProduct(long id) {
		return this.productCRUD.readObject(id);
	}
	
}
