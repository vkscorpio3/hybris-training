package com.amway.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;


public class AmwayCompositeCacheManager implements AmwayCacheManager, InitializingBean
{

	private final List<CacheManager> cacheManagers = new ArrayList<CacheManager>();

	public AmwayCompositeCacheManager()
	{
	}

	public AmwayCompositeCacheManager(final CacheManager... cacheManagers)
	{
		setCacheManagers(Arrays.asList(cacheManagers));
	}


	public void setCacheManagers(final Collection<CacheManager> cacheManagers)
	{
		this.cacheManagers.addAll(cacheManagers);
	}

	public void afterPropertiesSet()
	{
	}


	@Override
	public Cache getCache(final String name)
	{
		for (final CacheManager cacheManager : this.cacheManagers)
		{
			final Cache cache = cacheManager.getCache(name);
			if (cache != null)
			{
				return cache;
			}
		}
		return null;
	}

	@Override
	public AmwayCache getAmwayCache(final String name)
	{
		for (final CacheManager cacheManager : this.cacheManagers)
		{
			if (cacheManager instanceof AmwayCacheManager)
			{
				final AmwayCache cache = ((AmwayCacheManager) cacheManager).getAmwayCache(name);
				if (cache != null)
				{
					return cache;
				}
			}
		}
		return null;
	}

	@Override
	public Collection<String> getCacheNames()
	{
		final Set<String> names = new LinkedHashSet<String>();
		for (final CacheManager manager : this.cacheManagers)
		{
			names.addAll(manager.getCacheNames());
		}
		return Collections.unmodifiableSet(names);
	}

}
