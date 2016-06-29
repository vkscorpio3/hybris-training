package com.razorfish.wechataddon.service;

import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import com.razorfish.wechataddon.daos.WechatQueryDao;
import com.tencent.business.ScanPayQueryBusiness.ResultListener;
import com.tencent.protocol.qrcode.UnifiedorderReqData;


/**
 * 
 */

/**
 * @author bingluo
 * 
 */
public class OrderUpdateResultListener implements ResultListener
{
	@Resource(name = "wechatOrderQueryDao")
	WechatQueryDao<OrderModel> wechatOrderQueryDao;

	@Resource
	ModelService modelService;

	@Override
	public void onFail(final UnifiedorderReqData scanPayResData)
	{

	}

	@Override
	public void onSuccess(final UnifiedorderReqData scanPayResData)
	{
		final OrderModel order = wechatOrderQueryDao.findById("code", scanPayResData.getOut_trade_no());
		if (!PaymentStatus.PAID.equals(order.getPaymentStatus()))
		{
			order.setPaymentStatus(PaymentStatus.PAID);
			modelService.save(order);
		}
	}

}
