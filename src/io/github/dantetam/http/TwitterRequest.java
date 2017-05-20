package io.github.dantetam.http;

import java.awt.print.Printable;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.sun.jna.platform.win32.OaIdl.VARDESC;

import edu.stanford.nlp.util.StringParsingTask;
import io.github.dantetam.data.JsonProcessor;
import io.github.dantetam.parser.ParseGrammarResult;
import io.github.dantetam.parser.StellaDependencyParser;
import io.github.dantetam.parser.StellaTreeAssociation;
import io.github.dantetam.parser.StellaWordAssociationGraph;

public class TwitterRequest {

	private String accessToken;
	
	public TwitterRequest() {
		accessToken = HttpRequest.twitterRequestBearerToken().replace("\"", "");
	}
	
	/*
	 * Get an array of the currently leading topics on Twitter through the GET trends/place endpoint.
	 */
	public String[] twitterGlobalTrending() {
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	String url = new String("https://api.twitter.com/1.1/trends/place.json?id=1");
        	HttpGet httpGet = new HttpGet(url);
        	httpGet.setHeader("Authorization", "Bearer " + accessToken);
                	
            ResponseHandler<String> responseHandler = new HttpResponseHandler();
            String responseBody = httpclient.execute(httpGet, responseHandler);

            JsonElement element = JsonProcessor.parseStringToObject(responseBody);            
            JsonArray trends = element.getAsJsonArray().get(0).getAsJsonObject().get("trends").getAsJsonArray();
           
            String[] results = new String[trends.size()];
            for (int i = 0; i < trends.size(); i++) {
            	JsonElement trend = trends.get(i);
            	//System.out.println(trend);
            	results[i] = trend.getAsJsonObject().get("name").toString();
            }
            
            //System.out.println(trends);
            return results;
        } catch (Exception e) {
        	e.printStackTrace();
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
	
	/*
	 * Get tweets from Twitter for a certain topic through the Twitter API GET search/tweets endpoint.
	 */
	public String[] twitterSearchTopic(String topic) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	String url = new String("https://api.twitter.com/1.1/search/tweets.json?");
        	url += "q=" + topic;
        	url += "&lang=en";
        	url += "&count=100";
        	System.out.println(url);
        	HttpGet httpGet = new HttpGet(url);
        	httpGet.setHeader("Authorization", "Bearer " + accessToken);
        	
            ResponseHandler<String> responseHandler = new HttpResponseHandler();
            String responseBody = httpclient.execute(httpGet, responseHandler);

            JsonArray tweets = JsonProcessor.parseStringToObject(responseBody).getAsJsonObject().get("statuses").getAsJsonArray();   
            String[] results = new String[tweets.size()];
            for (int i = 0; i < tweets.size(); i++) {
            	JsonElement tweet = tweets.get(i);
            	System.out.println("-------");
            	System.out.println(tweet);
            	System.out.println(tweet.getAsJsonObject().get("created_at").toString());
            	System.out.println(tweet.getAsJsonObject().get("id").toString());
            	System.out.println(tweet.getAsJsonObject().get("text").toString());
            	System.out.println(tweet.getAsJsonObject().get("user").toString());
            	results[i] = tweet.getAsJsonObject().get("text").toString();
            }
            
            return results;
            //System.out.println(trends);
            
        } catch (Exception e) {
        	e.printStackTrace();
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
	
	private static String sanitizeTweet(String text) {
		text = text.replaceAll("\n", "").replaceAll("\r", "").replaceAll("\"", "");
		//text = text.replaceAll("\\p{P}", "");
		String[] tokens = text.split(" ");
		String result = "";
		for (int i = 0; i < tokens.length - 1; i++) { //Remove last token which is often truncated
			String token = tokens[i];
			if (token.contains("#")) {
				token = token.substring(1);
			}
			if (!token.equals("RT") && !token.contains("@") && !token.contains("&") && !token.contains("...") && token.trim().length() > 0) {
				result += token.replaceAll("[^A-Za-z0-9 ]", "") + " ";
			}
		}
		return result;
	}
	
	public List<String> collatePopularTopicTweets(int numTopics, int tweetsPerTopic) {
		List<String> result = new ArrayList<>();
		String[] topics = twitterGlobalTrending();
		
		for (int topicIndex = 0; topicIndex < topics.length; topicIndex++) {
			if (topicIndex >= topics.length) break;
			String topic = topics[topicIndex];
			topic = topic.replace("\"", "").replace("#", "").replace(" ", "_");
		}
		
		String[][] tweetsOrganized = new String[numTopics][tweetsPerTopic];
		for (int topicIndex = 0; topicIndex < numTopics; topicIndex++) {
			if (topicIndex >= topics.length) break;
			String topic = topics[topicIndex];
			topic = topic.replace("\"", "").replace("#", "").replace(" ", "_").replace("?", "");
			String[] tweets = twitterSearchTopic(topic);
			int num = Math.min(tweetsPerTopic, tweets.length);
			for (int i = 0; i < num; i++) {
				tweetsOrganized[topicIndex][i] = tweets[i];
			}
		}
		
		for (int i = 0; i < tweetsOrganized.length; i++) {
			for (int j = 0; j < tweetsOrganized[0].length; j++) {
				String tweet = tweetsOrganized[i][j];
				if (tweet != null) {
					result.add(tweet);
				}
			}
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		List<String> popularTweets = new TwitterRequest().collatePopularTopicTweets(5,5);
		for (String tweet: popularTweets) {
			System.out.println(tweet);
		}
		
		if (5 > 3) return;
		
		TwitterRequest twitterRequest = new TwitterRequest();
		String[] topics = twitterRequest.twitterGlobalTrending();
		
		for (int topicIndex = 0; topicIndex < topics.length; topicIndex++) {
			if (topicIndex >= topics.length) break;
			String topic = topics[topicIndex];
			topic = topic.replace("\"", "").replace("#", "").replace(" ", "_");
			
			System.out.println(topics[topicIndex]);
		}
		
		for (int topicIndex = 0; topicIndex < 10; topicIndex++) {
			if (topicIndex >= topics.length) break;
			String topic = topics[topicIndex];
			topic = topic.replace("\"", "").replace("#", "").replace(" ", "_").replace("?", "");
			
			String[] tweets = twitterRequest.twitterSearchTopic(topic);
			
			StellaTreeAssociation stellaTreeAssociation = new StellaTreeAssociation();
			
			//String tweet = tweets[0];
			//String tweet = "I can always tell when movies use fake dinosaurs";
			for (String tweet: tweets) {
				//System.out.println(tweet);
				tweet = sanitizeTweet(tweet);
				//System.out.println(tweet);
				List<ParseGrammarResult> parseGrammarResults = StellaDependencyParser.parseGrammarStructure(tweet);
				for (ParseGrammarResult psg: parseGrammarResults) {
					StellaWordAssociationGraph graph = stellaTreeAssociation.processText(psg);
					double[][] matrix = StellaWordAssociationGraph.getFullAssociationMatrix(psg.taggedWords, graph);
					Map<String[], Double> sortedCorr = StellaWordAssociationGraph.matrixFindBestCorrelation(psg.taggedWords, matrix);
					int i = 0;
					for (Map.Entry<String[], Double> entry : sortedCorr.entrySet()) {
						String word1 = entry.getKey()[0], word2 = entry.getKey()[1];
						//if ()
						if (word1 == "``" || word2 == "``") continue;
						System.out.println(word1 + ", " + word2 + ": " + entry.getValue());
						i++;
						if (i == 30) break;
					}
				}
			}
		}
	}
	
}
