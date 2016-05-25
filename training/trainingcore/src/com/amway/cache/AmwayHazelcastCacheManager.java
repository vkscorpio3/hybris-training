package com.amway.cache;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.cache.Cache;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.spring.cache.HazelcastCache;


public class AmwayHazelcastCacheManager implements AmwayCacheManager
{

	private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<String, Cache>();
	private HazelcastInstance hazelcastInstance;

	public AmwayHazelcastCacheManager()
	{
	}

	public AmwayHazelcastCacheManager(final HazelcastInstance hazelcastInstance)
	{
		this.hazelcastInstance = hazelcastInstance;
	}

	@Override
	public Cache getCache(final String name)
	{
		Cache cache = caches.get(name);
		if (cache == null)
		{
			final IMap<Object, Object> map = hazelcastInstance.getMap(name);
			cache = new HazelcastCache(map);
			final Cache currentCache = caches.putIfAbsent(name, cache);
			if (currentCache != null)
			{
				cache = currentCache;
			}
		}
		return cache;
	}

	public AmwayCache getAmwayCache(final String name)
	{
		AmwayCache cache = (AmwayCache) caches.get(name);
		if (cache == null)
		{
			final IMap<Object, Object> map = hazelcastInstance.getMap(name);
			cache = new AmwayHazelcastCache(map);
			final AmwayCache currentCache = (AmwayCache) caches.putIfAbsent(name, cache);
			if (currentCache != null)
			{
				cache = currentCache;
			}
		}
		return cache;
	}

	@Override
	public Collection<String> getCacheNames()
	{
		final Set<String> cacheNames = new HashSet<String>();
		final Collection<DistributedObject> distributedObjects = hazelcastInstance.getDistributedObjects();
		for (final DistributedObject distributedObject : distributedObjects)
		{
			if (distributedObject instanceof IMap)
			{
				final IMap<?, ?> map = (IMap) distributedObject;
				cacheNames.add(map.getName());
			}
		}
		return cacheNames;
	}

	public void setHazelcastInstance(final HazelcastInstance hazelcastInstance)
	{
		this.hazelcastInstance = hazelcastInstance;
	}

	public HazelcastInstance getHazelcastInstance()
	{
		return hazelcastInstance;
	}
}
