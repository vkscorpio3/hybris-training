/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/

package com.amway.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;


public abstract class AbstractAmwayCacheManager extends AbstractCacheManager implements AmwayCacheManager
{

	public AmwayCache getAmwayCache(final String name)
	{
		final Cache c = super.getCache(name);
		if (c instanceof AmwayCache)
		{
			return (AmwayCache) c;
		}
		else
		{
			return null;
		}
	}

}
