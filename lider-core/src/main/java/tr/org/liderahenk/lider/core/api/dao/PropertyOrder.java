package tr.org.liderahenk.lider.core.api.dao;

import tr.org.liderahenk.lider.core.api.enums.OrderType;

/**
 * Provides (ascending or descending) ordering on properties.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.plugin.IPluginDbService
 *
 */
public class PropertyOrder {

	final private String propertyName;
	final private OrderType orderType;

	public PropertyOrder(String propertyName, OrderType orderType) {
		super();
		this.propertyName = propertyName;
		this.orderType = orderType;
	}

	public PropertyOrder(String propertyName) {
		super();
		this.propertyName = propertyName;
		this.orderType = OrderType.ASC;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public OrderType getOrderType() {
		return orderType;
	}
}
