package de.hybris.platform.stock.impl;

import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import de.hybris.platform.cache.impl.StockCacheFacade;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;


public class ExtStockLevelDao extends DefaultStockLevelDao
{
	@Resource(name = "stockLevelCacheFacade")
	StockCacheFacade stockLevelCacheFacade;

	protected final Logger LOG = Logger.getLogger(this.getClass());

	@Override
	public StockLevelModel findStockLevel(final String productCode, final WarehouseModel warehouse)
	{
		LOG.info("----------------------findStockLevel-----------------------------");
		String key = productCode + warehouse.getCode();
		if(!stockLevelCacheFacade.hasKey(key)){
			StockLevelModel stocklevel = super.findStockLevel(productCode, warehouse);
			stockLevelCacheFacade.put(key, stocklevel);
		}
		if(stockLevelCacheFacade.get(key) == null) {
			return null;
		}
		return stockLevelCacheFacade.get(key);
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
		stockLevelCacheFacade.put(stockLevel.getProductCode() + stockLevel.getWarehouse().getCode(), stockLevel);
	}

	@Override
	public Integer reserve(final StockLevelModel stockLevel, final int amount)
	{
		LOG.info("----------------------reserve-----------------------------");
		final Integer i = super.reserve(stockLevel, amount);
		stockLevelCacheFacade.put(stockLevel.getProductCode() + stockLevel.getWarehouse().getCode(), stockLevel);
		return i;
	}

	@Override
	public Integer release(final StockLevelModel stockLevel, final int amount)
	{
		LOG.info("----------------------release-----------------------------");
		final Integer i = super.release(stockLevel, amount);
		stockLevelCacheFacade.put(stockLevel.getProductCode() + stockLevel.getWarehouse().getCode(), stockLevel);
		return i;
	}

}