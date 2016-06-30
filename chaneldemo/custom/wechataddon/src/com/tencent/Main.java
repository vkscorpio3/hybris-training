package com.tencent;

import com.razorfish.wechataddon.service.OrderUpdateResultListener;
import com.tencent.business.ScanPayQueryBusiness;
import com.tencent.protocol.qrcode.UnifiedorderReqData;
import com.tencent.service.UnifiedOrderService;

import de.hybris.platform.commercefacades.order.data.OrderData;


public class Main
{

	public static void main(final String[] args)
	{

		try
		{
			final UnifiedorderReqData unifiedorderReqData = new UnifiedorderReqData();
			unifiedorderReqData.setOut_trade_no("S002-00253002");
			unifiedorderReqData.setTotal_fee(1);
			unifiedorderReqData.setBody("zz");
			unifiedorderReqData.setSpbill_create_ip("10.112.0.16");
			final ScanPayQueryBusiness scanPayQueryBusiness = new ScanPayQueryBusiness(unifiedorderReqData,
					new OrderUpdateResultListener(){
				
			});
			scanPayQueryBusiness.run();
//			final UnifiedOrderService unifiedOrderService = new UnifiedOrderService();
//			final UnifiedorderReqData unifiedorderReqData = new UnifiedorderReqData();
//			unifiedorderReqData.setOut_trade_no("S002-00253002");
//			unifiedorderReqData.setTotal_fee(1);
//			unifiedorderReqData.setAppid("wxd678efh567hg6787");
//			unifiedorderReqData.setMch_id("1230000109");
//			final String x = unifiedOrderService.request(unifiedorderReqData);
//			System.out.println(x);
			//--------------------------------------------------------------------
			//温馨提示，第一次使用该SDK时请到com.tencent.common.Configure类里面进行配置
			//--------------------------------------------------------------------



			//--------------------------------------------------------------------
			//PART One:基础组件测试
			//--------------------------------------------------------------------

			//1）https请求可用性测试
			//HTTPSPostRquestWithCert.test();

			//2）测试项目用到的XStream组件，本项目利用这个组件将Java对象转换成XML数据Post给API
			//XStreamTest.test();


			//--------------------------------------------------------------------
			//PART Two:基础服务测试
			//--------------------------------------------------------------------

			//1）测试被扫支付API
			//			PayServiceTest.test();

			//2）测试被扫订单查询API
			//PayQueryServiceTest.test();

			//3）测试撤销API
			//温馨提示，测试支付API成功扣到钱之后，可以通过调用PayQueryServiceTest.test()，将支付成功返回的transaction_id和out_trade_no数据贴进去，完成撤销工作，把钱退回来 ^_^v
			//ReverseServiceTest.test();

			//4）测试退款申请API
			//RefundServiceTest.test();

			//5）测试退款查询API
			//RefundQueryServiceTest.test();

			//6）测试对账单API
			//DownloadBillServiceTest.test();


			//本地通过xml进行API数据模拟的时候，先按需手动修改xml各个节点的值，然后通过以下方法对这个新的xml数据进行签名得到一串合法的签名，最后把这串签名放到这个xml里面的sign字段里，这样进行模拟的时候就可以通过签名验证了
			// Util.log(Signature.getSignFromResponseString(Util.getLocalXMLString("/test/com/tencent/business/refundqueryserviceresponsedata/refundquerysuccess2.xml")));

			//Util.log(new Date().getTime());
			//Util.log(System.currentTimeMillis());

		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}

	}
}
