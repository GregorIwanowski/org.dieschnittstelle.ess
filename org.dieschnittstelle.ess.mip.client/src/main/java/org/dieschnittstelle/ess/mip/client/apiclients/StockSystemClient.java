package org.dieschnittstelle.ess.mip.client.apiclients;

import java.util.ArrayList;
import java.util.List;

import org.dieschnittstelle.ess.mip.components.erp.api.StockSystemService;
import org.dieschnittstelle.ess.mip.components.erp.api.StockSystem;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;

public class StockSystemClient implements StockSystem {

	private StockSystemService serviceProxy;

	public StockSystemClient() throws Exception {
		// TODO: remove the comments and complete the implementation
		this.serviceProxy = ServiceProxyFactory.getInstance().getProxy(StockSystemService.class);
	}

	// TODO: uncomment the commented sections from all the following methods and remove the default return statements

	@Override
	public void addToStock(IndividualisedProductItem product, long pointOfSaleId, int units) {
		this.serviceProxy.addToStock(product.getId(),pointOfSaleId,units);
	}

	@Override
	public void removeFromStock(IndividualisedProductItem product, long pointOfSaleId,
								int units) {
		this.serviceProxy.removeFromStock(product.getId(),pointOfSaleId,units);
	}

	@Override
	public List<IndividualisedProductItem> getProductsOnStock(long pointOfSaleId) {
		return this.serviceProxy.getProductsOnStock(pointOfSaleId);
	}

	// added this "hack" for pointOfSaleId, because instructions are contradictory
	@Override
	public List<IndividualisedProductItem> getAllProductsOnStock() {
		long pointOfSaleId = 0L;
		return this.serviceProxy.getProductsOnStock(pointOfSaleId);
	}

	@Override
	public int getUnitsOnStock(IndividualisedProductItem product, long pointOfSaleId) {
//		return this.serviceProxy.getUnitsOnStock(product.getId(),pointOfSaleId);
		return 0;
	}

	@Override
	public int getTotalUnitsOnStock(IndividualisedProductItem product) {
//		return this.serviceProxy.getTotalUnitsOnStock(product.getId());
		return 0;
	}

	@Override
	public List<Long> getPointsOfSale(IndividualisedProductItem product) {
//		return this.serviceProxy.getPointsOfSale(product.getId());
		return new ArrayList<>();
	}


}
