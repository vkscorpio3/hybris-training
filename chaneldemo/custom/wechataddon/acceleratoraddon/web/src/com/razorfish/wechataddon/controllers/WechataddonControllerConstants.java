/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package com.razorfish.wechataddon.controllers;

/**
 */
public interface WechataddonControllerConstants
{
	String ADDON_PREFIX = "addon:/wechataddon/";

	/**
	 * Class with view name constants
	 */
	interface Views
	{

		interface Pages
		{
			String UNIFIED_ORDER_QRCODE_PAGE = ADDON_PREFIX + "pages/wechat/unifiedorderqrcode";
		}

		interface Fragments
		{


		}
	}
}
