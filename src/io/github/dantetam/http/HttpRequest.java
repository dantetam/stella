package io.github.dantetam.http;

import java.io.Console;
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

import com.google.gson.JsonElement;

import io.github.dantetam.data.JsonProcessor;

/*
 * A class for collecting all methods for GET and POST methods to various web APIs.
 */
public class HttpRequest {
	
	public final static void main(String[] args) {
		String accessToken = twitterRequestBearerToken();
		System.out.println(accessToken);
	}
	
	public final static void testYahooRequest() throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	String url = new String("http://autoc.finance.yahoo.com/autoc?query=Twitter&region=1&lang=en");
            HttpGet httpget = new HttpGet(url);
            ResponseHandler<String> responseHandler = new HttpResponseHandler();
            
            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println(responseBody);
        } finally {
            httpclient.close();
        }
    }
	
	public static String twitterRequestBearerToken() {
		String encodedToken = new String("YTdLdzNkaXc2WXFKUnNtaEZ2a1dCbGphYTpmZUJoVzB2MGIybmhneHNwcG9lanpWZUNxSWtnbUROTFExaWtXV2RaNXpuVkNJOFc5Rg==");
	
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	String url = new String("https://api.twitter.com/oauth2/token");
        	HttpPost httpPost = new HttpPost(url);
        	httpPost.addHeader("Authorization", "Basic " + encodedToken);
        	httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        	httpPost.setEntity(new StringEntity("grant_type=client_credentials"));

            ResponseHandler<String> responseHandler = new HttpResponseHandler();
            String responseBody = httpclient.execute(httpPost, responseHandler);
            JsonElement element = JsonProcessor.parseStringToObject(responseBody);
            
            String accessToken = element.getAsJsonObject().get("access_token").toString();
            return accessToken;
        } catch (Exception e) {
        	
        } finally {
            try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return null;
	}
	
}
