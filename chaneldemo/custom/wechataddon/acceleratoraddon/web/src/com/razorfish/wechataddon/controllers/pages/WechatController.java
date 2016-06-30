package com.razorfish.wechataddon.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.OrderService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.razorfish.wechataddon.controllers.WechataddonControllerConstants;
import com.razorfish.wechataddon.daos.WechatQueryDao;
import com.razorfish.wechataddon.service.OrderUpdateResultListener;
import com.tencent.business.ScanPayQueryBusiness;
import com.tencent.common.Util;
import com.tencent.protocol.qrcode.UnifiedorderReqData;
import com.tencent.protocol.qrcode.UnifiedorderResData;
import com.tencent.service.UnifiedOrderService;


/**
 * AlipayController
 */
@Controller
@RequestMapping(value = "/wechat")
public class WechatController extends AbstractPageController
{
	protected static final Logger LOG = Logger.getLogger(WechatController.class);
	private static final String ORDER_CODE_PATH_VARIABLE_PATTERN = "{orderCode:.*}";

	@Resource(name = "orderService")
	private OrderService orderService;

	@Resource(name = "orderFacade")
	private OrderFacade orderFacade;

	@Resource(name = "wechatOrderQueryDao")
	WechatQueryDao<OrderModel> wechatOrderQueryDao;

	@Resource
	OrderUpdateResultListener orderUpdateResultListener;

	@RequestMapping(value = "/checkorder/" + ORDER_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	public String checkTradeStatus(@PathVariable("orderCode") final String orderCode, final Model model,
			final HttpServletRequest request, final HttpServletResponse response)
	{
		final OrderModel order = wechatOrderQueryDao.findById("code", orderCode);
		if (PaymentStatus.PAID.equals(order.getPaymentStatus()))
		{
			return "1";
		}
		return "0";
	}


	@RequestMapping(value = "/unifiedOrder/" + ORDER_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	public String doUnifiedOrder(@PathVariable("orderCode") final String orderCode, final Model model,
			final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException
	{
		String qrcode_url = "http://a.hiphotos.baidu.com/baike/w%3D268%3Bg%3D0/sign=7bcb659c9745d688a302b5a29cf91a23/2934349b033b5bb571dc8c5133d3d539b600bc12.jpg";
		try
		{
			final UnifiedOrderService unifiedOrderService = new UnifiedOrderService();
			final UnifiedorderReqData unifiedorderReqData = new UnifiedorderReqData();
			final OrderData orderData = orderFacade.getOrderDetailsForCode(orderCode);
			unifiedorderReqData.setOut_trade_no(orderCode);
			unifiedorderReqData.setTotal_fee(orderData.getTotalPrice().getValue().intValue());
			unifiedorderReqData.setBody("zz");
			unifiedorderReqData.setSpbill_create_ip(request.getLocalAddr());
			final String unifiedorderResxml = unifiedOrderService.request(unifiedorderReqData);
			LOG.info(unifiedorderResxml);
			final UnifiedorderResData unifiedorderResData = (UnifiedorderResData) Util.getObjectFromXML(unifiedorderResxml,
					UnifiedorderResData.class);
			if (unifiedorderResData.getReturn_code().equals("SUCCESS") && unifiedorderResData.getResult_code().equals("SUCCESS"))
			{
				qrcode_url = unifiedorderResData.getCode_url();
				if (!StringUtils.isEmpty(qrcode_url))
				{
					final ScanPayQueryBusiness scanPayQueryBusiness = new ScanPayQueryBusiness(unifiedorderReqData,
							orderUpdateResultListener);
					new Thread(scanPayQueryBusiness).start();
				}
			}
		}
		catch (final IllegalAccessException e)
		{
			// YTODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (final InstantiationException e)
		{
			// YTODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (final ClassNotFoundException e)
		{
			// YTODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (final Exception e)
		{
			// YTODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("ordercode", orderCode);
		model.addAttribute("qrcode_url", qrcode_url);
		storeCmsPageInModel(model, getContentPageForLabelOrId(null));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(null));
		return WechataddonControllerConstants.Views.Pages.UNIFIED_ORDER_QRCODE_PAGE;
	}
}
