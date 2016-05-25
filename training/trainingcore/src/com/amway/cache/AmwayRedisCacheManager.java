package com.amway.cache;

import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisOperations;


public class AmwayRedisCacheManager extends RedisCacheManager implements AmwayCacheManager
{

	public AmwayRedisCacheManager(final RedisOperations redisOperations)
	{
		super(redisOperations);
	}

	@Override
	public AmwayCache getAmwayCache(final String name)
	{
		return (AmwayCache) getCache(name);
	}

	@Override
	protected AmwayRedisCache createCache(final String cacheName)
	{
		final long expiration = computeExpiration(cacheName);
		return new AmwayRedisCache(cacheName, isUsePrefix() ? getCachePrefix().prefix(cacheName) : null, getRedisOperations(),
				expiration);
	}
}