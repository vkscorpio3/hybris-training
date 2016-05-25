package de.hybris.platform.stock.impl;

import de.hybris.platform.cache.impl.StockCacheFacade;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.amway.cache.AmwayCache;
import com.amway.cache.AmwayCacheManager;


public class ExtStockLevelDao extends DefaultStockLevelDao
{
	@Resource(name = "stockLevelCacheFacade")
	StockCacheFacade stockLevelCacheFacade;


	@Resource(name = "cacheManager")
	AmwayCacheManager cacheManager;

	protected final Logger LOG = Logger.getLogger(this.getClass());

	@Override
	public StockLevelModel findStockLevel(final String productCode, final WarehouseModel warehouse)
	{

		final String key = productCode + warehouse.getCode();
		final AmwayCache cache = cacheManager.getAmwayCache("stock_level");
		if (!cache.containsKey(key))
		{
			final StockLevelModel stocklevel = super.findStockLevel(productCode, warehouse);
			cache.put(key, stocklevel);
			if (null == stocklevel)
			{
				LOG.info("findStockLevel put " + key + ": null");
			}
			else
			{
				LOG.info("findStockLevel put " + key + ": " + stocklevel.getAvailable() + "#" + stocklevel.getReserved());
			}
			return stocklevel;
		}
		else
		{
			final StockLevelModel stock = cache.get(key, StockLevelModel.class);
			if (null == stock)
			{
				return null;
			}
			LOG.info("findStockLevel get " + key + ": " + stock.getAvailable() + "#" + stock.getReserved());
			return stock;
		}

	}

	@Override
	public Collection<StockLevelModel> findStockLevels(final String productCode, final Collection<WarehouseModel> warehouses)
	{
		final Iterator<WarehouseModel> var = warehouses.iterator();
		final ArrayList<StockLevelModel> result = new ArrayList<StockLevelModel>();
		while (var.hasNext())
		{
			final WarehouseModel house = var.next();
			final StockLevelModel stocklevelObj = this.findStockLevel(productCode, house);
			if (stocklevelObj != null)
			{
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
		final Integer i = super.reserve(stockLevel, amount);
		final AmwayCache cache = cacheManager.getAmwayCache("stock_level");
		final String key = stockLevel.getProductCode() + stockLevel.getWarehouse().getCode();
		LOG.info("findStockLevel reserve " + key + ": " + stockLevel.getAvailable() + "#" + stockLevel.getReserved());
		cache.put(stockLevel.getProductCode() + stockLevel.getWarehouse().getCode(), stockLevel);
		return i;
	}

	@Override
	public Integer release(final StockLevelModel stockLevel, final int amount)
	{
		final Integer i = super.release(stockLevel, amount);
		final AmwayCache cache = cacheManager.getAmwayCache("stock_level");
		final String key = stockLevel.getProductCode() + stockLevel.getWarehouse().getCode();
		LOG.info("findStockLevel release " + key + ": " + stockLevel.getAvailable() + "#" + stockLevel.getReserved());
		cache.put(stockLevel.getProductCode() + stockLevel.getWarehouse().getCode(), stockLevel);
		return i;
	}

}