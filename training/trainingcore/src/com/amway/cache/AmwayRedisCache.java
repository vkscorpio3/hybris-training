package com.amway.cache;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.core.RedisOperations;


public class AmwayRedisCache extends RedisCache implements AmwayCache
{

	public AmwayRedisCache(final String name, final byte[] prefix,
			final RedisOperations<? extends Object, ? extends Object> redisOperations, final long expiration)
	{
		super(name, prefix, redisOperations, expiration);
	}

	@Override
	public boolean containsKey(final Object obj)
	{
		return ((RedisOperations) getNativeCache()).hasKey(obj);
	}


}