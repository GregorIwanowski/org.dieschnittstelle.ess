package org.dieschnittstelle.ess.mip.components.erp.impl;

import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;
import org.dieschnittstelle.ess.entities.erp.PointOfSale;
import org.dieschnittstelle.ess.entities.erp.StockItem;
import org.dieschnittstelle.ess.mip.components.erp.api.StockSystem;
import org.dieschnittstelle.ess.mip.components.erp.crud.api.PointOfSaleCRUD;
import org.dieschnittstelle.ess.mip.components.erp.crud.impl.StockItemCRUD;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
@Transactional
@Logged
public class StockSystemImpl implements StockSystem {

    @Inject
    private PointOfSaleCRUD posCRUD;

    @Inject
    private StockItemCRUD stockItemCRUD;

    @Override
    public void addToStock(IndividualisedProductItem product, long pointOfSaleId, int units) {

        PointOfSale pos = posCRUD.readPointOfSale(pointOfSaleId);
        StockItem stockItem = stockItemCRUD.readStockItem(product, pos);
        if (stockItem != null) {
            stockItem.setUnits(stockItem.getUnits()+units);
            stockItemCRUD.updateStockItem(stockItem);
        } else {
            stockItem = new StockItem(product, pos, units);
            stockItemCRUD.createStockItem(stockItem);
        }

    }

    @Override
    public void removeFromStock(IndividualisedProductItem product, long pointOfSaleId, int units) {
        this.addToStock(product, pointOfSaleId, -units);
    }

    @Override
    public List<IndividualisedProductItem> getProductsOnStock(long pointOfSaleId) {
        PointOfSale pos = posCRUD.readPointOfSale(pointOfSaleId);
        List<StockItem> stockItemList = stockItemCRUD.readStockItemsForPointOfSale(pos);
        List<IndividualisedProductItem> productItemList = new ArrayList<IndividualisedProductItem>();
        for (StockItem stockItem : stockItemList) {
            productItemList.add(stockItem.getProduct());
        }
        return productItemList;
    }

    @Override
    public List<IndividualisedProductItem> getAllProductsOnStock() {
        List<PointOfSale> posList = posCRUD.readAllPointsOfSale();
        Set<IndividualisedProductItem> productItemSet = new HashSet<IndividualisedProductItem>();
        for (PointOfSale pos : posList) {
            List<IndividualisedProductItem> productItemList = this.getProductsOnStock(pos.getId());
            productItemSet.addAll(productItemList);
        }
        List<IndividualisedProductItem> piList = new ArrayList<IndividualisedProductItem>(productItemSet);
        return piList;
    }

    @Override
    public int getUnitsOnStock(IndividualisedProductItem product, long pointOfSaleId) {
        return 0;
    }

    @Override
    public int getTotalUnitsOnStock(IndividualisedProductItem product) {
        return 0;
    }

    @Override
    public List<Long> getPointsOfSale(IndividualisedProductItem product) {
        return null;
    }
}
