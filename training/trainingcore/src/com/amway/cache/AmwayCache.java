/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package com.amway.cache;

import org.springframework.cache.Cache;


public interface AmwayCache extends Cache
{
	public abstract boolean containsKey(final Object obj);

	public abstract void overload(final Object key,AmwayCache.OverloadCallback overloadCallback);

	public abstract class OverloadCallback<T>{
		public abstract T callbackFun(T overloadObj);
	}
}