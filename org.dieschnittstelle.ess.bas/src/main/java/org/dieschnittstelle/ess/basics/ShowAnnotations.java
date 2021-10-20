package org.dieschnittstelle.ess.basics;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.basics.annotations.AnnotatedStockItemBuilder;
import org.dieschnittstelle.ess.basics.annotations.StockItemProxyImpl;
import org.dieschnittstelle.ess.basics.reflection.ReflectedStockItemBuilder;

import java.lang.reflect.Field;

import static org.dieschnittstelle.ess.utils.Utils.*;

public class ShowAnnotations {

	protected static Logger logger = LogManager
			.getLogger(ShowAnnotations.class);

	public static void main(String[] args) {
		// we initialise the collection
		StockItemCollection collection = new StockItemCollection(
				"stockitems_annotations.xml", new AnnotatedStockItemBuilder());
		// we load the contents into the collection
		collection.load();

		for (IStockItem consumable : collection.getStockItems()) {
			showAttributes(((StockItemProxyImpl)consumable).getProxiedObject());
		}

		// we initialise a consumer
		Consumer consumer = new Consumer();
		// ... and let them consume
		consumer.doShopping(collection.getStockItems());
	}

	/*
	 * TODO BAS2
	 */
	private static void showAttributes(Object instance) {
		show("class is: " + instance.getClass());

		// TODO BAS2: create a string representation of instance by iterating
		//  over the object's attributes / fields as provided by its class
		//  and reading out the attribute values. The string representation
		//  will then be built from the field names and field values.
		//  Note that only read-access to fields via getters or direct access
		//  is required here.

		Class klass = instance.getClass();
		String representation = "{" + klass.getSimpleName();
		try {
			for (Field f : klass.getDeclaredFields()) {
				f.setAccessible(true);
				representation += " " + f.getName() + ":" + f.get(instance) + ",";
			}

			representation = representation.substring(0, representation.length() - 1) + "}";
			logger.info(representation + "\n");

		} catch (IllegalAccessException e) {
			logger.error("got IllegalAccessException: " + e, e);
			throw new RuntimeException(e);
		}

		// TODO BAS3: if the new @DisplayAs annotation is present on a field,
		//  the string representation will not use the field's name, but the name
		//  specified in the the annotation. Regardless of @DisplayAs being present
		//  or not, the field's value will be included in the string representation.
	}

}
