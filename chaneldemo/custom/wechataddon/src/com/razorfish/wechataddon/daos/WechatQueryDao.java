package com.razorfish.wechataddon.daos;

import de.hybris.platform.core.model.ItemModel;



public abstract interface WechatQueryDao<M extends ItemModel>
{
	public abstract M findById(String paramString, Object paramObject);
}
