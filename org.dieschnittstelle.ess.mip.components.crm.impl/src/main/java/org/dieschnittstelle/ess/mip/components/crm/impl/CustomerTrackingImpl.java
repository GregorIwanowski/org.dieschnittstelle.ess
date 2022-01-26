package org.dieschnittstelle.ess.mip.components.crm.impl;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

import org.dieschnittstelle.ess.mip.components.crm.api.CustomerTracking;
import org.dieschnittstelle.ess.entities.crm.ShoppingCartItem;
import org.dieschnittstelle.ess.entities.crm.CustomerTransaction;
import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.mip.components.crm.crud.impl.CustomerTransactionCRUD;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

/**
 * allows read/write access to a customer's shopping history
 */
@Alternative
@Priority(Interceptor.Priority.APPLICATION+10)
@Logged
@ApplicationScoped
public class CustomerTrackingImpl implements CustomerTracking {

	protected static Logger logger = org.apache.logging.log4j.LogManager
			.getLogger(CustomerTrackingImpl.class);

	/**
	 * we use the local interface to the CustomerTransactionCRUD
	 */
	@Inject
	private CustomerTransactionCRUD customerTransactionCRUD;

	public CustomerTrackingImpl() {
		logger.info("<constructor>: " + this);
	}

	public void createTransaction(CustomerTransaction transaction) {
		// in case of using the RESTful shopping cart implementation, product bundles will have been persisted and will
		// be passed with a non-default id. In order to allow a unified treatment, we will keep the respective OneToMany
		// relations to ShoppingCartItem and will reset their ids before creating the transaction
		for (ShoppingCartItem item : transaction.getItems()) {
			item.setId(0);
		}
		
		customerTransactionCRUD.createTransaction(transaction);
	}

	@Override
	public List<CustomerTransaction> readTransactions(long touchpointId,long customerId) {
		if (customerId == 0 && touchpointId == 0) {
			return customerTransactionCRUD.readAllTransactions();
		}
		else if (customerId != 0 && touchpointId != 0) {
			return customerTransactionCRUD.readAllTransactionsForTouchpointAndCustomer(touchpointId,customerId);
		}
		else if (customerId != 0) {
			return customerTransactionCRUD.readAllTransactionsForCustomer(customerId);
		}
		else {
			return customerTransactionCRUD.readAllTransactionsForTouchpoint(touchpointId);
		}
	}

	@PostConstruct
	public void initialise() {
		logger.info("@PostConstruct: customerTransactionCRUD is: " + customerTransactionCRUD);
	}

	@PreDestroy
	public void finalise() {
		logger.info("@PreDestroy");
	}

}
