package com.amway.cache;

import org.springframework.cache.CacheManager;


public interface AmwayCacheManager extends CacheManager
{

	AmwayCache getAmwayCache(String name);

}