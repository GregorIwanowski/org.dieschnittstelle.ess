package org.dieschnittstelle.ess.mip.components.shopping.impl;

import org.dieschnittstelle.ess.mip.components.shopping.api.PurchaseService;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;

@RequestScoped
@Transactional
@Logged
public class PurchaseServiceImpl implements PurchaseService {
}
