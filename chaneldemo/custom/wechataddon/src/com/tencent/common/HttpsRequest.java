package com.tencent.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;

import com.tencent.service.IServiceRequest;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;


/**
 * User: rizenguo Date: 2014/10/29 Time: 14:36
 */
public class HttpsRequest implements IServiceRequest
{

	public interface ResultListener
	{


		public void onConnectionPoolTimeoutError();

	}

	private static Log log = new Log(LoggerFactory.getLogger(HttpsRequest.class));

	//表示请求器是否已经做了初始化工作
	private boolean hasInit = false;

	//连接超时时间，默认10秒
	private final int socketTimeout = 10000;

	//传输超时时间，默认30秒
	private final int connectTimeout = 30000;

	//HTTP请求器
	private DefaultHttpClient httpClient;

	public HttpsRequest() throws UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			IOException
	{
		init();
	}

	private void init() throws IOException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException,
			KeyManagementException
	{

		final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());


		httpClient = new DefaultHttpClient();
		final FileInputStream instream = new FileInputStream(new File(this.getClass().getResource("/").getPath()
				+ Configure.getCertLocalPath()));
		try
		{
			keyStore.load(instream, Configure.getCertPassword().toCharArray());
		}
		catch (final CertificateException e)
		{
			// YTODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			instream.close();
		}
		final SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore);
		socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		final Scheme sch = new Scheme("https", 443, socketFactory);
		httpClient.getConnectionManager().getSchemeRegistry().register(sch);


		final HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, connectTimeout);
		HttpConnectionParams.setSoTimeout(params, socketTimeout);

		hasInit = true;
	}

	/**
	 * 通过Https往API post xml数据
	 * 
	 * @param url
	 *           API地址
	 * @param xmlObj
	 *           要提交的XML数据对象
	 * @return API回包的实际数据
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws UnrecoverableKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */

	public String sendPost(final String url, final Object xmlObj) throws IOException, KeyStoreException,
			UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException
	{

		if (!hasInit)
		{
			init();
		}

		String result = null;

		final HttpPost httpPost = new HttpPost(url);

		//解决XStream对出现双下划线的bug
		final XStream xStreamForRequestPostData = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));

		//将要提交给API的数据对象转换成XML格式数据Post给API
		final String postDataXML = xStreamForRequestPostData.toXML(xmlObj);

		Util.log("API，POST过去的数据是：");
		Util.log(postDataXML);

		//得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别
		final StringEntity postEntity = new StringEntity(postDataXML, "UTF-8");
		httpPost.addHeader("Content-Type", "text/xml");
		httpPost.setEntity(postEntity);


		Util.log("executing request" + httpPost.getRequestLine());

		try
		{
			final HttpResponse response = httpClient.execute(httpPost);

			final HttpEntity entity = response.getEntity();

			result = EntityUtils.toString(entity, "UTF-8");

		}
		catch (final ConnectionPoolTimeoutException e)
		{
			log.e("http get throw ConnectionPoolTimeoutException(wait time out)");

		}
		catch (final ConnectTimeoutException e)
		{
			log.e("http get throw ConnectTimeoutException");

		}
		catch (final SocketTimeoutException e)
		{
			log.e("http get throw SocketTimeoutException");

		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			httpPost.abort();
		}

		return result;
	}

}
