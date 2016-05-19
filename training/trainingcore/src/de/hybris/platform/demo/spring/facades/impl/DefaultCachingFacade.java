package de.hybris.platform.demo.spring.facades.impl;

import de.hybris.platform.demo.spring.facades.CachingFacade;
import de.hybris.platform.demo.spring.service.CachingService;

import org.springframework.beans.factory.annotation.Required;


public class DefaultCachingFacade implements CachingFacade
{

	private CachingService cachingService;

	@Override
	public String findData(final String id)
	{
		String value = "";
		try
		{
			value = getCachingService().findData(id);
		}
		catch (final Exception e)
		{

		}
		return value;
	}

	public CachingService getCachingService()
	{
		return cachingService;
	}

	@Required
	public void setCachingService(final CachingService cachingService)
	{
		this.cachingService = cachingService;
	}
}
