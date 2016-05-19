package de.hybris.platform.cache.impl;

import de.hybris.platform.ordersplitting.model.StockLevelModel;
import org.springframework.cache.CacheManager;

import javax.annotation.Resource;

public class StockCacheFacade {

    @Resource(name = "cacheManager")
    CacheManager cacheManager;

    public void put(String key, StockLevelModel stockleve){
        if(stockleve != null) {
            cacheManager.getCache("stock_level").put(key, stockleve);
        }
        cacheManager.getCache("stock_level").put("ex_key:"+key, true);
    }

    public Boolean hasKey(String key){
        if(cacheManager.getCache("stock_level").get("ex_key:"+key) == null){
            return false;
        }else{
            return true;
        }
    }

    public StockLevelModel get(String key){
        return cacheManager.getCache("stock_level").get(key,StockLevelModel.class);
    }
}
