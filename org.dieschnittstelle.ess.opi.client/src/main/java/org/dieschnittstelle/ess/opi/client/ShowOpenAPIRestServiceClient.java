package org.dieschnittstelle.ess.opi.client;

// TODO: entfernen Sie die auskommentierten Codezeilen. Falls Ihre imports automatisch aktualisiert
//  werden, dann entfernen Sie erst die Kommentare aus der Implementierung der Klasse und kommentieren
//  Sie die imports erst danach ein.

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.logging.log4j.Logger;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.dieschnittstelle.ess.opi.client.api.DefaultApi;
import org.dieschnittstelle.ess.opi.client.model.Address;
import org.dieschnittstelle.ess.opi.client.model.StationaryTouchpoint;

import java.util.ArrayList;
import java.util.List;

public class ShowOpenAPIRestServiceClient {

	protected static Logger logger = org.apache.logging.log4j.LogManager
			.getLogger(ShowOpenAPIRestServiceClient.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// create the service proxy, using a jackson provider instance that is configured not
		// to fail on unknown properties (most importantly, the @class property)
		JacksonJsonProvider provider = new JacksonJsonProvider();
		provider.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

		List providers = new ArrayList();
		providers.add(provider);

		DefaultApi serviceProxy = JAXRSClientFactory.create("http://localhost:8080", DefaultApi.class, providers);

		List<StationaryTouchpoint> existingTouchpoints = serviceProxy.readAllTouchpoints();
		logger.info("read " + existingTouchpoints.size() + " touchpoints: " + toSinglelineString(existingTouchpoints));

		// if we have more than 0 touchpoints, delete the first one
		if (existingTouchpoints.size() > 0) {
			serviceProxy.deleteTouchpoint(existingTouchpoints.get(0).getId());
			logger.info("after deletion, read " + serviceProxy.readAllTouchpoints().size() + " touchpoints");
		}

		// create a new touchpoint - note that all attributes with primitive types need to be set to a default value
		// as the generated code uses the wrapper types, which will be passed as null values otherwise, causing
		// server-side trouble
		StationaryTouchpoint newTouchpoint = new StationaryTouchpoint();
		Address address = new Address();
		address.setCity("Berlin");
		address.setStreet("Luxemburger Str.");
		address.setZipCode("13353");
		address.setHouseNr("10");
		address.setGeoLat(0);
		address.setGeoLong(0);
		address.setId(0);
		newTouchpoint.setErpPointOfSaleId(0);
		newTouchpoint.setAddress(address);
		newTouchpoint.setName("BHT OpenAPI Touchpoint");
		newTouchpoint.setId(0);

		// create
		StationaryTouchpoint tpp = serviceProxy.createTouchpoint(newTouchpoint);
		logger.info("created: " + toSinglelineString(tpp));

		// read the created one
		StationaryTouchpoint resp = serviceProxy.readTouchpoint(tpp.getId());
		logger.info("read created: " + toSinglelineString(resp));

	}

	public static String toSinglelineString(Object obj) {
		String trimmed = String.valueOf(obj).replaceAll("\n","");
		while (trimmed.indexOf("  ") != -1) {
			trimmed = trimmed.replaceAll("  "," ");
		}
		return trimmed;
	}

}
