package io.github.dantetam.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/*
 * A class for collecting all methods for GET and POST methods to various web APIs.
 */
public class HttpRequest {
	
	public final static void main(String[] args) {
		twitterRequest();
	}
	
	public final static void testYahooRequest() throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	String url = new String("http://autoc.finance.yahoo.com/autoc?query=Twitter&region=1&lang=en");
            HttpGet httpget = new HttpGet(url);

            System.out.println("Executing request " + httpget.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
        } finally {
            httpclient.close();
        }
    }
	
	public static void twitterRequest() {
		String encodedToken = new String("YTdLdzNkaXc2WXFKUnNtaEZ2a1dCbGphYTpmZUJoVzB2MGIybmhneHNwcG9lanpWZUNxSWtnbUROTFExaWtXV2RaNXpuVkNJOFc5Rg==");
	
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	String url = new String("https://api.twitter.com/oauth2/token");
        	HttpPost httpPost = new HttpPost(url);

        	httpPost.addHeader("Authorization", "Basic " + encodedToken);
        	httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        	httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
        	
            System.out.println("Executing request " + httpPost.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            String responseBody = httpclient.execute(httpPost, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
        } catch (Exception e) {
        	
        } finally {
            try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
	
}
