package com.hf.module.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;






import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import com.hf.module.ModuleConfig;
import com.hf.module.ModuleException;


public class HttpProxy {
	private static HttpProxy instance = new HttpProxy();
	
	public static int HTTP_TIMEOUT = 5000;
	
	private String uServiceUrl = "";
	private String uSslServiceUrl = "";

	private HttpProxy() {
		super();
	}

	public synchronized static HttpProxy getInstance() {
		return instance;
	}

	public void setServiceUrl(String uServiceUrl){
		this.uServiceUrl = uServiceUrl;
		
	}
	
	public void setSslServiceUrl(String uSslServiceUrl){
		this.uSslServiceUrl = uSslServiceUrl;
	}
	
	public void init(){
		try {
			trustAllHttpsCertificates();
			HttpsURLConnection.setDefaultHostnameVerifier(hv);  
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public String mycallHttpPost(String req) throws Exception {
		URL postUrl = new URL(this.uServiceUrl);
		HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
		return postIt(req, connection);
	}

	public String callHttpsPost(String req) throws ModuleException {
		return callHttpsPost(req, this.uSslServiceUrl);
	}

	public String callHttpsPost(String req, String url) throws ModuleException {
//        try {
//        	URL postUrl = new URL(url);
//			HttpsURLConnection connection = (HttpsURLConnection) postUrl.openConnection();
//			return postIt(req, connection);
//        } catch (MalformedURLException me) {
//        	throw new ModuleException("HTTP POST error." + me.getMessage());
//        } catch (IOException ioe) {
//        	throw new ModuleException("HTTP POST error." + ioe.getMessage());
//        } 
		  try {
	        	
				return reqByHttpPost(req);
	        } catch (MalformedURLException me) {
	        	throw new ModuleException("HTTP POST error." + me.getMessage());
	        } catch (IOException ioe) {
	        	throw new ModuleException("HTTP POST error." + ioe.getMessage());
	        } catch (Exception e) {
				// TODO Auto-generated catch block
	        	throw new ModuleException("HTTP POST error." + e.getMessage());
			} 
	}
	
	
	private String postIt(String req, HttpURLConnection connection) throws ProtocolException, IOException, UnsupportedEncodingException {
	    String rsp ;
	    System.out.println(req);
//	    req = URLEncoder.encode(req,"UTF-8");
	    connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		connection.setUseCaches(false);
		connection.setInstanceFollowRedirects(true);
		connection.setRequestProperty("Content-Type", "application/x-javascript->json");
		connection.setRequestProperty("Content-Encoding", "UTF-8");
		connection.setConnectTimeout(HTTP_TIMEOUT);
		connection.setReadTimeout(HTTP_TIMEOUT);
		/*
		 * 	HttpEntity requestHttpEntity = new UrlEncodedFormEntity(paramsNameValuePairs, HTTP.UTF_8);
			HttpPost httpPost = new HttpPost(baseUrl);
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			httpPost.setEntity(requestHttpEntity);
			HttpClient httpClient = new DefaultHttpClient();	*/
		connection.connect();
		DataOutputStream out = new DataOutputStream(connection.getOutputStream());

		String content = req;
//		out.writeBytes(content);
		out.write(req.getBytes("UTF-8"));
		out.flush();
		out.close();

		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
		String line = null;
		StringBuffer sb = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}

		rsp = sb.toString();
		System.out.println(rsp);
	    return rsp;
    }

	private static HostnameVerifier hv = new HostnameVerifier() {
		public boolean verify(String urlHostName, SSLSession session) {
			System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
			return true;
		}
	};

	private static void trustAllHttpsCertificates() throws Exception {
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new EasyTrustManager();
		trustAllCerts[0] = tm;
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	private static class EasyTrustManager implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		@SuppressWarnings("unused")
        public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
			return true;
		}

		@SuppressWarnings("unused")
		public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
		        throws java.security.cert.CertificateException {
			return;
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
		        throws java.security.cert.CertificateException {
			return;
		}
	}


	public static void main(String args[]) throws Exception {
	    HttpProxy.getInstance().setServiceUrl("http://115.29.164.59/usvc/");
	    HttpProxy.getInstance().setSslServiceUrl("https://115.29.164.59/usvc/");
	    HttpProxy.getInstance().init();
		
		String req1 = "{'PL':{'userName':'demo1','password':'demo','mac':'123456789012'},'CID':10011}";
		String rsp1 = HttpProxy.getInstance().callHttpsPost(req1);
		System.out.println(rsp1);

		String req2 = "{'PL':{'userName':'demo1','password':'demo','mac':'123456789012'},'CID':10011}";
		String rsp2 = HttpProxy.getInstance().callHttpsPost(req2);
		System.out.println(rsp2);
		
	}
	
	
	public synchronized String reqByHttpPost(String req) throws Exception {
		String rsp = "";
		System.out.println(req);
		URL postUrl = new URL("http://115.29.164.59/usvc/");
		HttpURLConnection connection = (HttpURLConnection) postUrl
				.openConnection();

		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		connection.setUseCaches(false);
		connection.setInstanceFollowRedirects(true);
		connection.setRequestProperty("Content-Type", "application/x-javascript->json");
		connection.setRequestProperty("Content-Encoding", "utf-8");
		connection.setConnectTimeout(HTTP_TIMEOUT);
		connection.setReadTimeout(HTTP_TIMEOUT);
		connection.connect();
		DataOutputStream out = new DataOutputStream(
				connection.getOutputStream());

		String content = req;
//		out.writeBytes(content);
		out.write(content.getBytes("UTF-8"));
		out.flush();
		out.close();

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream(), "utf-8"));
		String line = null;
		StringBuffer sb = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		rsp = sb.toString();
		System.out.println(rsp);
		return rsp;
	}
}
