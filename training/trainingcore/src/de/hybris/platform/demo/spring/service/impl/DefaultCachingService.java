package de.hybris.platform.demo.spring.service.impl;

import de.hybris.platform.demo.spring.service.CachingService;

import org.springframework.cache.annotation.Cacheable;


public class DefaultCachingService implements CachingService
{
	@Cacheable("caching")
	@Override
	public String findData(final String id) throws InterruptedException
	{
		Thread.sleep(5000);
		return id;
	}
}
