package com.razorfish.wechataddon.daos.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.Collections;

import com.razorfish.wechataddon.daos.WechatQueryDao;



public class DefaultWechatQueryDao<M extends ItemModel> extends DefaultGenericDao<M> implements WechatQueryDao<M>
{
	private final String typeCode;

	public DefaultWechatQueryDao(final String typeCode)
	{
		super(typeCode);
		this.typeCode = typeCode;
	}


	public M findById(final String codeField, final Object value)
	{
		final StringBuilder builder = createQueryString();
		builder.append("WHERE {c:").append(codeField).append("} ").append("= ?codeField ");
		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString(), Collections.singletonMap("codeField", value));

		return getFlexibleSearchService().searchUnique(query);
	}

	private StringBuilder createQueryString()
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("SELECT {c:").append("pk").append("} ");
		builder.append("FROM {").append(this.typeCode).append(" AS c} ");
		return builder;
	}
}
