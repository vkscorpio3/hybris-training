package com.tencent.service;

import com.tencent.common.Configure;
import com.tencent.common.Signature;
import com.tencent.protocol.qrcode.UnifiedorderReqData;


/**
 * User: rizenguo Date: 2014/10/29 Time: 16:03
 */
public class UnifiedOrderService extends BaseService
{

	public UnifiedOrderService() throws IllegalAccessException, InstantiationException, ClassNotFoundException
	{
		super(Configure.UNIFIED_ORDER_API);
	}

	/**
	 * 请求支付服务
	 * 
	 * @param scanPayReqData
	 *           这个数据对象里面包含了API要求提交的各种数据字段
	 * @return API返回的数据
	 * @throws Exception
	 */
	public String request(final UnifiedorderReqData unifiedorderReqData) throws Exception
	{

		unifiedorderReqData.setFee_type("NATIVE");
		final String sign = Signature.getSign(unifiedorderReqData.toMap());
		unifiedorderReqData.setSign(sign);
		final String responseString = sendPost(unifiedorderReqData);
		return responseString;
	}
}
