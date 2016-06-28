//package com.razorfish.wechataddon.controllers.pages;
//
//import de.hybris.platform.commercefacades.order.OrderFacade;
//import de.hybris.platform.commercefacades.order.data.OrderData;
//import de.hybris.platform.order.OrderService;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.log4j.Logger;
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import com.tencent.common.Util;
//import com.tencent.protocol.qrcode.UnifiedorderReqData;
//import com.tencent.protocol.qrcode.UnifiedorderResData;
//import com.tencent.service.UnifiedOrderService;
//
//
///**
// * AlipayController
// */
//@Controller
//@Scope("tenant")
//@RequestMapping(value = "/wechat")
//public class WechatController
//{
//	protected static final Logger LOG = Logger.getLogger(WechatController.class);
//	private static final String ORDER_CODE_PATH_VARIABLE_PATTERN = "{orderCode:.*}";
//	private static final String AMOUNT_PATH_VARIABLE_PATTERN = "{amount:.*}";
//
//	@Resource(name = "orderService")
//	private OrderService orderService;
//
//	@Resource(name = "orderFacade")
//	private OrderFacade orderFacade;
//
//	@RequestMapping(value = "/checkorder/" + ORDER_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
//	public String checkTradeStatus(@PathVariable("orderCode") final String orderCode, final Model model,
//			final HttpServletRequest request, final HttpServletResponse response)
//	{
//		return null;
//	}
//
//
//	@RequestMapping(value = "/unifiedOrder/" + ORDER_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
//	public String doUnifiedOrder(@PathVariable("orderCode") final String orderCode, final Model model,
//			final HttpServletRequest request, final HttpServletResponse response)
//	{
//		String qrcode_url  ="http://a.hiphotos.baidu.com/baike/w%3D268%3Bg%3D0/sign=7bcb659c9745d688a302b5a29cf91a23/2934349b033b5bb571dc8c5133d3d539b600bc12.jpg";
//		try
//		{
//			final UnifiedOrderService unifiedOrderService = new UnifiedOrderService();
//			final UnifiedorderReqData unifiedorderReqData = new UnifiedorderReqData();
//			final OrderData orderData = orderFacade.getOrderDetailsForCode(orderCode);
//			unifiedorderReqData.setOut_trade_no(orderCode);
//			unifiedorderReqData.setTotal_fee(orderData.getTotalPrice().getValue().intValue());
//			final String unifiedorderResxml = unifiedOrderService.request(unifiedorderReqData);
//			final UnifiedorderResData unifiedorderResData = (UnifiedorderResData) Util.getObjectFromXML(unifiedorderResxml,
//					UnifiedorderResData.class);
//			qrcode_url = unifiedorderResData.getCode_url();
//			
//		}
//		catch (final IllegalAccessException e)
//		{
//			// YTODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch (final InstantiationException e)
//		{
//			// YTODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch (final ClassNotFoundException e)
//		{
//			// YTODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch (final Exception e)
//		{
//			// YTODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		model.addAttribute("qrcode_url", qrcode_url);
//		return "pages/wechat/unifiedorderqrcode";
//	}
//
//
//}
