package com.client.certificate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.spongycastle.jce.provider.BouncyCastleProvider;

import android.content.Context;
import android.util.Log;

public class AndroidHttpClientCertificate 
{ 
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	public static final int HTTP_TIMEOUT = 30 * 1000; // milliseconds 
	
	private static SSLSocketFactory SSLSocketFactory(Context context) 
	{
		try
		{
			KeyStore keystore = KeyStore.getInstance("BKS"); // BKS - PKCS12
			//InputStream in = context.getResources().openRawResource(R.raw.vincent1331085321077);

			//Read file in Internal Storage
			FileInputStream in = null;
			try
			{
				in = context.openFileInput("keystore.bks");
			}
			catch (FileNotFoundException e)
			{
				Log.e("client", "Cert error: " + e.getMessage());
			}

			// load client certificate
			try
			{
				keystore.load(in, "password".toCharArray());
			}
			finally
			{
				if(in != null)
				{
					in.close();
				}
				else
				{
					Log.e("client", "File input stream is NULL");
				}
			}
			Log.i("client", "Loaded client certifcates: " + keystore.size());

			Enumeration<String> aliases = keystore.aliases();
			while (aliases.hasMoreElements())
			{
				String alias = (String) aliases.nextElement();

				Certificate cert = keystore.getCertificate(alias);
				Log.i("client", "Cert alias: " + alias + " \nCert Type: " + cert.getType() + "\nPublic key:\n" + cert.getPublicKey().toString() + "Public key Algorithm: " + cert.getPublicKey().getAlgorithm());
			}
			// initialize key manager factory with the read client certificate
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keystore, "password".toCharArray());
			
			KeyManager[] managers = keyManagerFactory.getKeyManagers();
			Log.d("client", managers[0].getClass().getName()); 
			return new AndroidSSLSocketFactory(managers);
		} catch (Exception e) {
			Log.e("client", "SSL error:" + e.getMessage());
			throw new AssertionError(e);
		}
	}
		
	public static HttpClient getHttpClient(Context context) 
	{ 
		try
		{ 		
			HttpParams params = new BasicHttpParams(); 
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1); 
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8); 
			
			SchemeRegistry registry = new SchemeRegistry(); 
			registry.register(new Scheme("https", SSLSocketFactory(context), 8443));		

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry); 
			return new DefaultHttpClient(ccm, params); 
		} 
		catch (Exception e) 
		{ 
			Log.e("client", "getHttpClient error: " + e.getMessage());
			return new DefaultHttpClient(); 
		} 
	}
	
	public static String executeHttpPost(Context context, String url, List<NameValuePair> nameValuePairs) throws Exception 
	{ 
		BufferedReader in = null; 
		
		try 
		{ 
			HttpClient client = getHttpClient(context);             
			HttpPost request = new HttpPost(url); 
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairs); 
			request.setEntity(formEntity); 
			HttpResponse response = client.execute(request); 
			Log.d("client", "Params: " + client.getParams().toString());
			
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent())); 
			StringBuffer sb = new StringBuffer(""); 
			String line = ""; 
			String NL = System.getProperty("line.separator"); 
			
			while ((line = in.readLine()) != null) 
			{ 
				sb.append(line + NL); 
			} 
			in.close();           
			
			String result = sb.toString(); 
			return result; 
		} 
		finally 
		{ 
			if (in != null)
			{ 
				try { 
					in.close(); 
				} catch (IOException e) { 
					e.printStackTrace(); 
				} 
			} 
		} 
	}
	
	public static String executeHttpGet(Context context, String url) throws Exception
	{ 
		BufferedReader in = null; 

		try { 
			HttpClient client = getHttpClient(context); 
			HttpGet request = new HttpGet(); 
			request.setURI(new URI(url)); 
			HttpResponse response = client.execute(request); 
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent())); 
			StringBuffer sb = new StringBuffer(""); 
			String line = ""; 
			String NL = System.getProperty("line.separator"); 

			while ((line = in.readLine()) != null) { 
				sb.append(line + NL); 
			} 
			in.close();        
			String result = sb.toString(); 
			return result;  
		}
		finally 
		{ 
			if (in != null) 
			{ 
				try { 
					in.close(); 
				} catch (IOException e) { 
					e.printStackTrace(); 
				} 
			} 
		} 
	}    
}  