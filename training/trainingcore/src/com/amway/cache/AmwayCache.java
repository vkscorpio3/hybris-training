/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package com.amway.cache;

import org.springframework.cache.Cache;


public abstract interface AmwayCache extends Cache
{
	public abstract boolean containsKey(final Object obj);
}