package de.hybris.platform.demo.spring.service;

public interface CachingService
{
	public String findData(String id) throws InterruptedException;
}
