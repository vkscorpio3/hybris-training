package com.amway.cache;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheElement;
import org.springframework.data.redis.cache.RedisCacheKey;
import org.springframework.data.redis.connection.DecoratedRedisConnection;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;
import org.springframework.data.redis.serializer.RedisSerializer;


public class AmwayRedisCache extends RedisCache implements AmwayCache
{
	private final byte[] keyPrefix;
	private long defaultExpiration = 0L;
	private final AmwayRedisCache.RedisCacheMetadata cacheMetadata;

	public AmwayRedisCache(final String name, final byte[] prefix,
			final RedisOperations<? extends Object, ? extends Object> redisOperations, final long expiration)
	{
		super(name, prefix, redisOperations, expiration);
		this.cacheMetadata = new AmwayRedisCache.RedisCacheMetadata(name, prefix);
		this.keyPrefix = prefix;
		this.defaultExpiration = expiration;
	}

	public void overload(final Object key, AmwayRedisCache.OverloadCallback overloadCallback){
		RedisSerializer keySerializer = ((RedisOperations) getNativeCache()).getKeySerializer();
		RedisCacheKey cacheKey = (new RedisCacheKey(key)).usePrefix(this.cacheMetadata.getKeyPrefix()).withKeySerializer(keySerializer);
		RedisCacheElement element = new RedisCacheElement(cacheKey, (Object)null);
		RedisSerializer valueRedisSerializer = ((RedisOperations) getNativeCache()).getValueSerializer();
		((RedisOperations) getNativeCache()).execute(new AmwayRedisCache.AbstractRedisCacheCallback(this.cacheMetadata,element,valueRedisSerializer){
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException{
				this.waitForLock(connection);
				this.lock(connection);
				byte[] value = connection.get(element.getKeyBytes());
				Object valueObj = this.getValueSerializer() != null?this.getValueSerializer().deserialize(value):value;
				valueObj = overloadCallback.callbackFun(valueObj);
				if(!AmwayRedisCache.isClusterConnection(connection)) {
					connection.multi();
				}
				value = valueObj == null?new byte[0]:this.getValueSerializer().serialize(valueObj);
				connection.set(element.getKeyBytes(), value);
				if(!AmwayRedisCache.isClusterConnection(connection)) {
					connection.exec();
				}
				this.unlock(connection);
				return Boolean.TRUE;
			}
		});
	}

	private static boolean isClusterConnection(RedisConnection connection) {
		while(connection instanceof DecoratedRedisConnection) {
			connection = ((DecoratedRedisConnection)connection).getDelegate();
		}

		return connection instanceof RedisClusterConnection;
	}

	@Override
	public boolean containsKey(final Object key) {
		RedisSerializer keySerializer = ((RedisOperations) getNativeCache()).getKeySerializer();
		return this.containsKey((new RedisCacheKey(key)).usePrefix(this.cacheMetadata.getKeyPrefix()).withKeySerializer(keySerializer));
	}

	public boolean containsKey(RedisCacheKey cacheKey) {
		Assert.notNull(cacheKey, "CacheKey must not be null!");
		RedisCacheElement element = new RedisCacheElement(cacheKey, (Object)null);
		return (Boolean)((RedisOperations) getNativeCache()).execute(new RedisCallback() {
			public Boolean doInRedis(RedisConnection connection) {
				return connection.exists(element.getKeyBytes());
			}
		});
	}

	abstract static class AbstractRedisCacheCallback<T> implements RedisCallback<T> {
		private long WAIT_FOR_LOCK_TIMEOUT = 300L;
		private final AmwayRedisCache.RedisCacheMetadata cacheMetadata;
		private final RedisCacheElement element;
		private final RedisSerializer valueSerializer;

		public AbstractRedisCacheCallback(AmwayRedisCache.RedisCacheMetadata metadata,RedisCacheElement element,RedisSerializer valueSerializer) {
			this.cacheMetadata = metadata;
			this.element = element;
			this.valueSerializer = valueSerializer;
		}

		public RedisSerializer getValueSerializer(){
			return this.valueSerializer;
		}

		public abstract T doInRedis(RedisConnection var2) throws DataAccessException;

		protected boolean waitForLock(RedisConnection connection) {
			boolean foundLock = false;

			boolean retry;
			do {
				retry = false;
				if(connection.exists(this.cacheMetadata.getCacheLockKey()).booleanValue()) {
					foundLock = true;

					try {
						Thread.sleep(this.WAIT_FOR_LOCK_TIMEOUT);
					} catch (InterruptedException var5) {
						Thread.currentThread().interrupt();
					}

					retry = true;
				}
			} while(retry);

			return foundLock;
		}

		protected void lock(RedisConnection connection) {
			this.waitForLock(connection);
			connection.set(this.cacheMetadata.getCacheLockKey(), "locked".getBytes());
		}

		protected void unlock(RedisConnection connection) {
			connection.del(new byte[][]{this.cacheMetadata.getCacheLockKey()});
		}
	}

	static class RedisCacheMetadata {
		private final String cacheName;
		private final byte[] keyPrefix;
		private final byte[] setOfKnownKeys;
		private final byte[] cacheLockName;
		private long defaultExpiration = 0L;

		public RedisCacheMetadata(String cacheName, byte[] keyPrefix) {
			Assert.hasText(cacheName, "CacheName must not be null or empty!");
			this.cacheName = cacheName;
			this.keyPrefix = keyPrefix;
			StringRedisSerializer stringSerializer = new StringRedisSerializer();
			this.setOfKnownKeys = this.usesKeyPrefix()?new byte[0]:stringSerializer.serialize(cacheName + "~keys");
			this.cacheLockName = stringSerializer.serialize(cacheName + "~lock");
		}

		public boolean usesKeyPrefix() {
			return this.keyPrefix != null && this.keyPrefix.length > 0;
		}

		public byte[] getKeyPrefix() {
			return this.keyPrefix;
		}

		public byte[] getSetOfKnownKeysKey() {
			return this.setOfKnownKeys;
		}

		public byte[] getCacheLockKey() {
			return this.cacheLockName;
		}

		public String getCacheName() {
			return this.cacheName;
		}

		public void setDefaultExpiration(long seconds) {
			this.defaultExpiration = seconds;
		}

		public long getDefaultExpiration() {
			return this.defaultExpiration;
		}
	}

}