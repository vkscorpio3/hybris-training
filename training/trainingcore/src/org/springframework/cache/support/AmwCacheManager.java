package org.springframework.cache.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;


public class AmwCacheManager implements CacheManager, InitializingBean
{
	private final List cacheManagers;
	private boolean fallbackToNoOpCache;

	public AmwCacheManager()
	{
		cacheManagers = new ArrayList();
		fallbackToNoOpCache = false;
	}

	public AmwCacheManager(final CacheManager cacheManagers[])
	{
		this.cacheManagers = new ArrayList();
		fallbackToNoOpCache = false;
		setCacheManagers(Arrays.asList(cacheManagers));
	}


	public void setCacheManagers(final Collection cacheManagers)
	{
		this.cacheManagers.addAll(cacheManagers);
	}

	public void setFallbackToNoOpCache(final boolean fallbackToNoOpCache)
	{
		this.fallbackToNoOpCache = fallbackToNoOpCache;
	}

	@Override
	public void afterPropertiesSet()
	{
		if (fallbackToNoOpCache)
		{
			cacheManagers.add(new NoOpCacheManager());
		}
	}

	@Override
	public Cache getCache(final String name)
	{
		for (final Iterator iterator = cacheManagers.iterator(); iterator.hasNext();)
		{
			final CacheManager cacheManager = (CacheManager) iterator.next();
			if (cacheManager instanceof HazelcastCacheManager)
			{
				final HazelcastInstance hazelcastInstance = ((HazelcastCacheManager) cacheManager).getHazelcastInstance();
			}
			final Cache cache = cacheManager.getCache(name);
			if (cache != null)
			{
				return cache;
			}
		}

		return null;
	}

	@Override
	public Collection getCacheNames()
	{
		final Set names = new LinkedHashSet();
		CacheManager manager;
		for (final Iterator iterator = cacheManagers.iterator(); iterator.hasNext(); names.addAll(manager.getCacheNames()))
		{
			manager = (CacheManager) iterator.next();
		}

		return Collections.unmodifiableSet(names);
	}
}