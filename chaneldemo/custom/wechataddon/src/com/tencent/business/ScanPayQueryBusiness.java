package com.tencent.business;

import static java.lang.Thread.sleep;

import org.slf4j.LoggerFactory;

import com.tencent.common.Log;
import com.tencent.common.Util;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryReqData;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryResData;
import com.tencent.protocol.qrcode.UnifiedorderReqData;
import com.tencent.service.ScanPayQueryService;


/**
 * User: rizenguo Date: 2014/12/1 Time: 17:05
 */
public class ScanPayQueryBusiness implements Runnable
{
	UnifiedorderReqData reqData;
	ResultListener resultListener;

	public ScanPayQueryBusiness(final UnifiedorderReqData reqData, final ResultListener resultListener)
			throws IllegalAccessException, ClassNotFoundException, InstantiationException
	{
		scanPayQueryService = new ScanPayQueryService();
		this.reqData = reqData;
		this.resultListener = resultListener;
	}

	public interface ResultListener
	{
		//支付失败
		void onFail(UnifiedorderReqData scanPayResData);

		//支付成功
		void onSuccess(UnifiedorderReqData scanPayResData);
	}

	//打log用
	private static Log log = new Log(LoggerFactory.getLogger(ScanPayQueryBusiness.class));

	//每次调用订单查询API时的等待时间，因为当出现支付失败的时候，如果马上发起查询不一定就能查到结果，所以这里建议先等待一定时间再发起查询

	private int waitingTimeBeforePayQueryServiceInvoked = 10000;

	//循环调用订单查询API的次数
	private int payQueryLoopInvokedCount = 100;

	private ScanPayQueryService scanPayQueryService;


	public void run()
	{
		try
		{
			final String outTradeNo = reqData.getOut_trade_no();

			if (doPayQueryLoop(payQueryLoopInvokedCount, outTradeNo))
			{
				resultListener.onSuccess(reqData);
			}
			else
			{
				resultListener.onFail(reqData);
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	private boolean doOnePayQuery(final String outTradeNo) throws Exception
	{

		sleep(waitingTimeBeforePayQueryServiceInvoked);//等待一定时间再进行查询，避免状态还没来得及被更新

		String payQueryServiceResponseString;

		final ScanPayQueryReqData scanPayQueryReqData = new ScanPayQueryReqData("", outTradeNo);
		payQueryServiceResponseString = scanPayQueryService.request(scanPayQueryReqData);

		//将从API返回的XML数据映射到Java对象
		final ScanPayQueryResData scanPayQueryResData = (ScanPayQueryResData) Util.getObjectFromXML(payQueryServiceResponseString,
				ScanPayQueryResData.class);
		if (scanPayQueryResData == null || scanPayQueryResData.getReturn_code() == null)
		{
			log.i("支付订单查询请求逻辑错误，请仔细检测传过去的每一个参数是否合法");
			return false;
		}

		if (scanPayQueryResData.getReturn_code().equals("FAIL"))
		{
			//注意：一般这里返回FAIL是出现系统级参数错误，请检测Post给API的数据是否规范合法
			log.i("支付订单查询API系统返回失败，失败信息为：" + scanPayQueryResData.getReturn_msg());
			return false;
		}
		else
		{
			if (scanPayQueryResData.getResult_code().equals("SUCCESS"))
			{//业务层成功
				if (scanPayQueryResData.getTrade_state().equals("SUCCESS"))
				{
					//表示查单结果为“支付成功”
					log.i("查询到订单支付成功");
					return true;
				}
				else
				{
					//支付不成功
					log.i("查询到订单支付不成功");
					return false;
				}
			}
			else
			{
				log.i("查询出错，错误码：" + scanPayQueryResData.getErr_code() + "     错误信息：" + scanPayQueryResData.getErr_code_des());
				return false;
			}
		}
	}

	/**
	 * 由于有的时候是因为服务延时，所以需要商户每隔一段时间（建议5秒）后再进行查询操作，多试几次（建议3次）
	 * 
	 * @param loopCount
	 *           循环次数，至少一次
	 * @param outTradeNo
	 *           商户系统内部的订单号,32个字符内可包含字母, [确保在商户系统唯一]
	 * @return 该订单是否支付成功
	 * @throws InterruptedException
	 */
	private boolean doPayQueryLoop(int loopCount, final String outTradeNo) throws Exception
	{
		//至少查询一次
		if (loopCount == 0)
		{
			loopCount = 1;
		}
		//进行循环查询
		for (int i = 0; i < loopCount; i++)
		{
			if (doOnePayQuery(outTradeNo))
			{
				return true;
			}
		}
		return false;
	}


	public void setWaitingTimeBeforePayQueryServiceInvoked(final int duration)
	{
		waitingTimeBeforePayQueryServiceInvoked = duration;
	}


	public void setPayQueryLoopInvokedCount(final int count)
	{
		payQueryLoopInvokedCount = count;
	}

	public void setScanPayQueryService(final ScanPayQueryService service)
	{
		scanPayQueryService = service;
	}


}
