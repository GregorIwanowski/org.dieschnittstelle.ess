package org.dieschnittstelle.ess.mip.components.shopping.impl;

import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.entities.crm.AbstractTouchpoint;
import org.dieschnittstelle.ess.entities.crm.Customer;
import org.dieschnittstelle.ess.entities.crm.CustomerTransaction;
import org.dieschnittstelle.ess.entities.crm.ShoppingCartItem;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;
import org.dieschnittstelle.ess.entities.erp.Campaign;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;
import org.dieschnittstelle.ess.entities.erp.ProductBundle;
import org.dieschnittstelle.ess.mip.components.crm.api.CampaignTracking;
import org.dieschnittstelle.ess.mip.components.crm.api.CustomerTracking;
import org.dieschnittstelle.ess.mip.components.crm.api.TouchpointAccess;
import org.dieschnittstelle.ess.mip.components.crm.crud.api.CustomerCRUD;
import org.dieschnittstelle.ess.mip.components.erp.api.StockSystem;
import org.dieschnittstelle.ess.mip.components.erp.api.StockSystemService;
import org.dieschnittstelle.ess.mip.components.erp.crud.api.ProductCRUD;
import org.dieschnittstelle.ess.mip.components.shopping.api.PurchaseService;
import org.dieschnittstelle.ess.mip.components.shopping.api.ShoppingException;
import org.dieschnittstelle.ess.mip.components.shopping.cart.api.ShoppingCart;
import org.dieschnittstelle.ess.mip.components.shopping.cart.api.ShoppingCartService;
import org.dieschnittstelle.ess.mip.components.shopping.cart.impl.ShoppingCartEntity;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@RequestScoped
@Transactional
@Logged
public class PurchaseServiceImpl implements PurchaseService {

    protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(PurchaseServiceImpl.class);

    /*
     * the three beans that are used
     */
    private ShoppingCart shoppingCart;

    @Inject
    private CustomerTracking customerTracking;

    @Inject
    private CampaignTracking campaignTracking;

    @Inject
    private ProductCRUD productCRUD;

    @Inject
    private StockSystemService stockSystemService;

    /**
     * the customer
     */
    private Customer customer;

    /**
     * the touchpoint
     */
    private AbstractTouchpoint touchpoint;

//    public ShoppingSession() {
//        logger.info("<constructor>");
//        try {
//            this.campaignTracking = new CampaignTrackingClient();
//            this.customerTracking = new CustomerTrackingClient();
//            this.shoppingCart = new ShoppingCartClient();
//        } catch (Exception e) {
//            throw new RuntimeException("initialise() failed: " + e, e);
//        }
//    }

    public void setTouchpoint(AbstractTouchpoint touchpoint) {
        this.touchpoint = touchpoint;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void addProduct(AbstractProduct product, int units) {
        this.shoppingCart.addItem(new ShoppingCartItem(product.getId(), units, product instanceof Campaign));
    }

    /*
     * verify whether campaigns are still valid
     */
    public void verifyCampaigns() throws ShoppingException {
        if (this.customer == null || this.touchpoint == null) {
            throw new RuntimeException("cannot verify campaigns! No touchpoint has been set!");
        }

        for (ShoppingCartItem item : this.shoppingCart.getItems()) {
            if (item.isCampaign()) {
                int availableCampaigns = this.campaignTracking.existsValidCampaignExecutionAtTouchpoint(
                        item.getErpProductId(), this.touchpoint);
                logger.info("got available campaigns for product " + item.getErpProductId() + ": "
                        + availableCampaigns);
                // we check whether we have sufficient campaign items available
                if (availableCampaigns < item.getUnits()) {
                    throw new ShoppingException("verifyCampaigns() failed for productBundle " + item
                            + " at touchpoint " + this.touchpoint + "! Need " + item.getUnits()
                            + " instances of campaign, but only got: " + availableCampaigns);
                }
            }
        }
    }

    public void purchase()  throws ShoppingException {
        logger.info("purchase()");

        if (this.customer == null || this.touchpoint == null) {
            throw new RuntimeException(
                    "cannot commit shopping session! Either customer or touchpoint has not been set: " + this.customer
                            + "/" + this.touchpoint);
        }

        // verify the campaigns
        verifyCampaigns();

        // remove the products from stock
        checkAndRemoveProductsFromStock();

        // then we add a new customer transaction for the current purchase
        // TODO PAT1: once this functionality has been moved to the server side components, make sure
        //  that the ShoppingCartItem instances will be cloned/copied by constructing new items before
        //  using them for creating the CustomerTransaction object.
        List<ShoppingCartItem> products = this.shoppingCart.getItems();
        CustomerTransaction transaction = new CustomerTransaction(this.customer, this.touchpoint, products);
        transaction.setCompleted(true);
        customerTracking.createTransaction(transaction);

        logger.info("purchase(): done.\n");
    }

    /*
     * TODO PAT2: complete the method implementation in your server-side component for shopping / purchasing
     */
    private void checkAndRemoveProductsFromStock() throws ShoppingException {
        logger.info("checkAndRemoveProductsFromStock");

        for (ShoppingCartItem item : this.shoppingCart.getItems()) {

            // TODO: ermitteln Sie das AbstractProduct für das gegebene ShoppingCartItem. Nutzen Sie dafür dessen erpProductId und die ProductCRUD bean
            AbstractProduct product = productCRUD.readProduct(item.getErpProductId());

            long posId = this.touchpoint.getErpPointOfSaleId();

            if (item.isCampaign()) {
                this.campaignTracking.purchaseCampaignAtTouchpoint(item.getErpProductId(), this.touchpoint,
                        item.getUnits());
                // TODO: wenn Sie eine Kampagne haben, muessen Sie hier
                // 1) ueber die ProductBundle Objekte auf dem Campaign Objekt iterieren, und
                // 2) fuer jedes ProductBundle das betreffende Produkt in der auf dem Bundle angegebenen Anzahl, multipliziert mit dem Wert von
                // item.getUnits() aus dem Warenkorb,
                // - hinsichtlich Verfuegbarkeit ueberpruefen, und
                // - falls verfuegbar, aus dem Warenlager entfernen - nutzen Sie dafür die StockSystem bean
                // (Anm.: item.getUnits() gibt Ihnen Auskunft darüber, wie oft ein Produkt, im vorliegenden Fall eine Kampagne, im
                // Warenkorb liegt)
                for (ProductBundle prodBundle : ((Campaign) product).getBundles()) {
                    long prodId = ((IndividualisedProductItem) prodBundle.getProduct()).getId();
                    int prodAmount = prodBundle.getUnits() * item.getUnits();
                    int onStock = stockSystemService.getUnitsOnStock(prodId, posId);
                    if (onStock >= prodAmount) {
                        stockSystemService.removeFromStock(prodId, posId, prodAmount);
                    } else {
                        throw new ShoppingException("checkAndRemoveProductsFromStock() failed for productBundle " + prodBundle
                                + " at touchpoint " + this.touchpoint + "! Tried to remove " + prodAmount
                                + " products from stock, but only got: " + onStock);
                    }
                }

            } else {
                // TODO: andernfalls (wenn keine Kampagne vorliegt) muessen Sie
                // 1) das Produkt in der in item.getUnits() angegebenen Anzahl hinsichtlich Verfuegbarkeit ueberpruefen und
                // 2) das Produkt, falls verfuegbar, in der entsprechenden Anzahl aus dem Warenlager entfernen
                long prodId = ((IndividualisedProductItem) product).getId();
                int prodAmount = item.getUnits();
                int onStock = stockSystemService.getUnitsOnStock(prodId, posId);
                if (onStock >= prodAmount) {
                    stockSystemService.removeFromStock(prodId, posId, prodAmount);
                } else {
                    throw new ShoppingException("checkAndRemoveProductsFromStock() failed for IndividualisedProductItem " + item
                            + " at touchpoint " + this.touchpoint + "! Tried to remove " + prodAmount
                            + " products from stock, but only got: " + onStock);
                }
            }
        }
    }

    /*
     *  bis hier handelt es sich um eine Uebertragung der client-seitigen ShoppingSession auf die Serverseite
     */

    @Inject
    private TouchpointAccess touchpointAccess;

    @Inject
    private CustomerCRUD customerCRUD;

    @Inject
    private ShoppingCartService shoppingCartService;

    @Override
    public void purchaseCartAtTouchpointForCustomer(long shoppingCartId, long touchpointId, long customerId) throws ShoppingException {
        this.touchpoint = this.touchpointAccess.readTouchpoint(touchpointId);
        this.customer = this.customerCRUD.readCustomer(customerId);

        this.shoppingCart = new ShoppingCartEntity();
        this.shoppingCartService.getItems(shoppingCartId).forEach(item -> {
            this.shoppingCart.addItem(new ShoppingCartItem(item.getErpProductId(), item.getUnits(), item.isCampaign()));
        });

        this.purchase();
    }
}
