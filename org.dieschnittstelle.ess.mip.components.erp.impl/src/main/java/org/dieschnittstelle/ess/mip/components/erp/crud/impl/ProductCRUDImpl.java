package org.dieschnittstelle.ess.mip.components.erp.crud.impl;

import org.dieschnittstelle.ess.entities.erp.AbstractProduct;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;
import org.dieschnittstelle.ess.mip.components.erp.crud.api.ProductCRUD;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
@Transactional
@Logged
public class ProductCRUDImpl implements ProductCRUD {

    @Inject
    @EntityManagerProvider.ERPDataAccessor
    private EntityManager em;

    @Override
    public AbstractProduct createProduct(AbstractProduct prod) {
        em.persist(prod);
        return prod;
    }

    @Override
    public List<AbstractProduct> readAllProducts() {
        IndividualisedProductItem item = new IndividualisedProductItem();
        item.setName("My Product");
        return Arrays.asList(item);
    }

    @Override
    public AbstractProduct updateProduct(AbstractProduct update) {
        return em.merge(update);
    }

    @Override
    public AbstractProduct readProduct(long productID) {
        return em.find(AbstractProduct.class, productID);
    }

    @Override
    public boolean deleteProduct(long productID) {
        AbstractProduct prod = em.find(AbstractProduct.class, productID);
        try {
            em.remove(prod);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
