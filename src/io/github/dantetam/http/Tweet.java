package io.github.dantetam.http;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class Tweet {

	public String topic;
	public int topicScore;
	private JsonElement jsonElement;
	
	public Tweet(String topic, int topicScore, JsonElement init) {
		this.topic = topic;
		this.topicScore = topicScore;
		jsonElement = init;
	}
	
	public int retweets() {
		return Integer.parseInt(jsonElement.getAsJsonObject().get("retweet_count").toString());
	}
	public int favorites() {
		return Integer.parseInt(jsonElement.getAsJsonObject().get("favorite_count").toString());
	}
	public int userFollowers() {
		return Integer.parseInt(jsonElement.getAsJsonObject().get("user").getAsJsonObject().get("followers_count").toString());
	}
	public int userFriends() {
		return Integer.parseInt(jsonElement.getAsJsonObject().get("user").getAsJsonObject().get("friends_count").toString());
	}
	
	public String text() {
		return jsonElement.getAsJsonObject().get("text").toString();
	}
	public String sanitizedText() {
		return TwitterRequest.sanitizeTweet(text());
	}
	
	public boolean hasPhoto() {
		JsonElement possibleMedia = jsonElement.getAsJsonObject().get("entities").getAsJsonObject().get("media");
		if (possibleMedia == null) return false;
		JsonArray allMedia = possibleMedia.getAsJsonArray();
		for (JsonElement media: allMedia) {
			System.out.println(media.getAsJsonObject().get("type").toString());
			if (media.getAsJsonObject().get("type").toString().contains("photo")) {
				return true;
			}
		}
		
		JsonElement possibleExtraMedia;
		try {
			possibleExtraMedia = jsonElement.getAsJsonObject().get("extended_entities").getAsJsonObject().get("media");
		} catch (NullPointerException e) {
			return false;
		}
		if (possibleExtraMedia == null) return false;
		JsonArray allExtraMedia = possibleExtraMedia.getAsJsonArray();
		for (JsonElement media: allExtraMedia) {
			if (media.getAsJsonObject().get("type").toString().contains("photo")) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean hasVideo() {		
		JsonElement possibleExtraMedia;
		try {
			possibleExtraMedia = jsonElement.getAsJsonObject().get("extended_entities").getAsJsonObject().get("media");
		} catch (NullPointerException e) {
			return false;
		}
		if (possibleExtraMedia == null) return false;
		JsonArray allExtraMedia = possibleExtraMedia.getAsJsonArray();
		for (JsonElement media: allExtraMedia) {
			if (media.getAsJsonObject().get("type").toString().contains("video")) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasAnimatedGif() {		
		JsonElement possibleExtraMedia;
		try {
			possibleExtraMedia = jsonElement.getAsJsonObject().get("extended_entities").getAsJsonObject().get("media");
		} catch (NullPointerException e) {
			return false;
		}
		if (possibleExtraMedia == null) return false;
		JsonArray allExtraMedia = possibleExtraMedia.getAsJsonArray();
		for (JsonElement media: allExtraMedia) {
			if (media.getAsJsonObject().get("type").toString().contains("animated_gif")) {
				return true;
			}
		}
		return false;
	}
	
	public float popularityScore() {
		float top = 2.0f * retweets() + (float) favorites();
		float bottom = userFollowers() * 0.01f + (float) userFriends();
		return top / (bottom * topicScore);
	}
	
}
