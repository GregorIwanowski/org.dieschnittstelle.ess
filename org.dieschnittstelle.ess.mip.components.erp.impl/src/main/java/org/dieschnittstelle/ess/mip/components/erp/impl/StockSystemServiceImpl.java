package org.dieschnittstelle.ess.mip.components.erp.impl;

import org.dieschnittstelle.ess.entities.erp.AbstractProduct;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;
import org.dieschnittstelle.ess.mip.components.erp.api.StockSystem;
import org.dieschnittstelle.ess.mip.components.erp.api.StockSystemService;
import org.dieschnittstelle.ess.mip.components.erp.crud.api.ProductCRUD;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import javax.transaction.Transactional;
import java.util.List;

@Alternative
@Priority(Interceptor.Priority.APPLICATION+10)
@ApplicationScoped
@Logged
@Transactional
public class StockSystemServiceImpl implements StockSystemService {

    @Inject
    private StockSystem stockSystem;

    @Inject
    private ProductCRUD productCRUD;

    @Override
    public void addToStock(long productId, long pointOfSaleId, int units) {
        AbstractProduct product = productCRUD.readProduct(productId);
        stockSystem.addToStock((IndividualisedProductItem) product, pointOfSaleId, units);

    }

    @Override
    public void removeFromStock(long productId, long pointOfSaleId, int units) {
        this.addToStock(productId, pointOfSaleId, -units);
    }

    @Override
    public List<IndividualisedProductItem> getProductsOnStock(long pointOfSaleId) {
        if (pointOfSaleId == 0L) {
            return stockSystem.getAllProductsOnStock();
        }
        return stockSystem.getProductsOnStock(pointOfSaleId);
    }

    @Override
    public int getUnitsOnStock(long productId, long pointOfSaleId) {
        AbstractProduct product = productCRUD.readProduct(productId);
        if (pointOfSaleId == 0L) {
            return stockSystem.getTotalUnitsOnStock((IndividualisedProductItem) product);
        }
        return stockSystem.getUnitsOnStock((IndividualisedProductItem) product, pointOfSaleId);
    }

    @Override
    public List<Long> getPointsOfSale(long productId) {
        AbstractProduct product = productCRUD.readProduct(productId);
        List<Long> poss = stockSystem.getPointsOfSale((IndividualisedProductItem) product);
        return poss;
    }
}
