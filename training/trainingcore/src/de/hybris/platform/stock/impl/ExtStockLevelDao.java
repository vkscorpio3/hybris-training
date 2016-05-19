package de.hybris.platform.stock.impl;

import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;

import org.apache.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;


public class ExtStockLevelDao extends DefaultStockLevelDao
{

	protected final Logger LOG = Logger.getLogger(this.getClass());

	@Cacheable(value = "stockCache", key = "#product.getCode()+#warehouse.getCode()")
	@Override
	public StockLevelModel findStockLevel(final String productCode, final WarehouseModel warehouse)
	{
		LOG.info("----------------------findStockLevel-----------------------------");
		return super.findStockLevel(productCode, warehouse);
	}

}