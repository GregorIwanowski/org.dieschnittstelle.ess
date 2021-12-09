package org.dieschnittstelle.ess.mip.components.erp.crud.impl;

import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;
import org.dieschnittstelle.ess.entities.erp.PointOfSale;
import org.dieschnittstelle.ess.entities.erp.StockItem;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@Transactional
@Logged
public class StockItemCRUDImpl implements StockItemCRUD {

    @Inject
    @EntityManagerProvider.ERPDataAccessor
    private EntityManager em;

    @Override
    public StockItem createStockItem(StockItem item) {
        em.persist(item);
        return item;
    }

    @Override
    public StockItem readStockItem(IndividualisedProductItem prod, PointOfSale pos) {
        Query query = em.createQuery("SELECT si FROM StockItem si WHERE si.product.id = " + prod.getId() + " AND si.pos.id = " + pos.getId());
        List<StockItem> stockItems = query.getResultList();
        if (stockItems.size() > 0) {
            return stockItems.get(0);
        }
        return null;
    }

    @Override
    public StockItem updateStockItem(StockItem item) {
        return em.merge(item);
    }

    @Override
    public List<StockItem> readAllStockItems() {
        return null;
    }

    @Override
    public List<StockItem> readStockItemsForProduct(IndividualisedProductItem prod) {
        Query query = em.createQuery("SELECT si FROM StockItem si WHERE si.product.id = " + prod.getId());
        List<StockItem> stockItems = query.getResultList();
        return stockItems;
    }

    @Override
    public List<StockItem> readStockItemsForPointOfSale(PointOfSale pos) {
        Query query = em.createQuery("SELECT si FROM StockItem si WHERE si.pos.id = " + pos.getId());
        List<StockItem> stockItems = query.getResultList();
        return stockItems;
    }
}
