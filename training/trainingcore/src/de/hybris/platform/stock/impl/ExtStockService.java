package de.hybris.platform.stock.impl;

import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.cache.impl.StockCacheFacade;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;



public class ExtStockService extends DefaultStockService
{
	@Resource(name = "stockLevelCacheFacade")
	StockCacheFacade stockLevelCacheFacade;

	protected final Logger LOG = Logger.getLogger(this.getClass());

	@CachePut(value = "stockCache", key = "#product.getCode()+#warehouse.getCode()")
	@Override
	protected StockLevelModel createStockLevel(final ProductModel product, final WarehouseModel warehouse, final int available,
			final int overSelling, final int reserved, final InStockStatus status, final int maxStockLevelHistoryCount,
			final boolean treatNegativeAsZero)
	{
		LOG.info("----------------------createStockLevel-----------------------------");
		StockLevelModel stockLevel = super.createStockLevel(product, warehouse, available, overSelling, reserved, status, maxStockLevelHistoryCount,treatNegativeAsZero);
		stockLevelCacheFacade.put(product.getCode() + warehouse.getCode(), stockLevel);
		return stockLevel;
	}
}