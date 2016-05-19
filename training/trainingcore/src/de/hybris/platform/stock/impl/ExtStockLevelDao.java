package de.hybris.platform.stock.impl;

import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;


public class ExtStockLevelDao extends DefaultStockLevelDao
{
	@Resource(name = "cacheManager")
	CacheManager cacheManager;

	protected final Logger LOG = Logger.getLogger(this.getClass());

	@Cacheable(value = "stockCache", key = "#productCode+#warehouse.getCode()")
	@Override
	public StockLevelModel findStockLevel(final String productCode, final WarehouseModel warehouse)
	{
		LOG.info("----------------------findStockLevel-----------------------------");
		String key = productCode + warehouse.getCode();
		if(cacheManager.getCache("stockCache").get(key,StockLevelModel.class) == null){
			StockLevelModel stocklevel = super.findStockLevel(productCode, warehouse);
			return stocklevel;
		}
		return cacheManager.getCache("stockCache").get(key,StockLevelModel.class);
	}

	@Override
	public Collection<StockLevelModel> findStockLevels(String productCode, Collection<WarehouseModel> warehouses) {
		Iterator var = warehouses.iterator();
		ArrayList<StockLevelModel> result = new ArrayList();
		while(var.hasNext()) {
			WarehouseModel house = (WarehouseModel)var.next();
			StockLevelModel stocklevelObj = this.findStockLevel(productCode,house);
			if(stocklevelObj != null){
				result.add(stocklevelObj);
			}
		}
		return result;
	}

	@Override
	public void updateActualAmount(final StockLevelModel stockLevel, final int actualAmount)
	{
		LOG.info("----------------------updateActualAmount-----------------------------");
		super.updateActualAmount(stockLevel, actualAmount);
		cacheManager.getCache("stockCache").put(stockLevel.getProductCode() + stockLevel.getWarehouse().getCode(), stockLevel);
	}

	@Override
	public Integer reserve(final StockLevelModel stockLevel, final int amount)
	{
		LOG.info("----------------------reserve-----------------------------");
		final Integer i = super.reserve(stockLevel, amount);
		cacheManager.getCache("stockCache").put(stockLevel.getProductCode() + stockLevel.getWarehouse().getCode(), stockLevel);
		return i;
	}

	@Override
	public Integer release(final StockLevelModel stockLevel, final int amount)
	{
		LOG.info("----------------------release-----------------------------");
		final Integer i = super.release(stockLevel, amount);
		cacheManager.getCache("stockCache").put(stockLevel.getProductCode() + stockLevel.getWarehouse().getCode(), stockLevel);
		return i;
	}

}