package io.github.dantetam.http;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import io.github.dantetam.analysis.SentimentAnalysis;

public class Tweet {

	public String topic;
	public float topicScore;
	private JsonElement jsonElement;
	
	public Tweet(String topic, float topicScore, JsonElement init) {
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
	public int userNumStatuses() {
		return Integer.parseInt(jsonElement.getAsJsonObject().get("user").getAsJsonObject().get("statuses_count").toString());
	}
	public int userOtherFavorites() {
		return Integer.parseInt(jsonElement.getAsJsonObject().get("user").getAsJsonObject().get("favourites_count").toString());
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
	
	public float getSentiment() {
		return SentimentAnalysis.getSentimentSentence(sanitizedText());
	}
	
	public int[] getTimeVector() {
		String dateString = jsonElement.getAsJsonObject().get("created_at").toString();
		float time = Float.parseFloat(dateString.split(" ")[3].split(":")[0]);
		time += Float.parseFloat(dateString.split(" ")[3].split(":")[1]) / 60.0f;
		int start = 2, inc = 2;
		int slots = 12;
		int[] vector = new int[slots];
		for (int i = 0; i < slots; i++) {
			if ( 	(time > start && time <= (start + inc)) ||
					(time + 24 > start && time + 24 <= (start + inc))
					)
			{
				vector[i] = 1;
				break;
			}
			start += inc;
		}
		return vector;
	}
	
	public int[] getDayOfWeekVector() {
		String dateString = jsonElement.getAsJsonObject().get("created_at").toString();
		String dayString = dateString.split(" ")[0].replace("\"", "");
		String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
		int[] vector = new int[7];
		for (int i = 0; i < 7; i++) {
			if (dayString.equals(days[i])) {
				vector[i] = 1;
				break;
			}
		}
		return vector;
	}
	
	public String vectorToString(int[] vec) {
		if (vec.length == 0) return "";
		String result = "";
		for (int i = 0; i < vec.length; i++) {
			result += vec[i] + ",";
		}
		return result.substring(0, result.length() - 1); 
	}
	
	public float popularityScore() {
		float top = 2.0f * retweets() + (float) favorites();
		float bottom = userFollowers() * 0.01f + (float) userFriends();
		return top * 100000.0f / (bottom * topicScore);
	}
	
	public static String oldCsvCategoryString = "CATEGORY,EMOTION,"
			+ "TIME2_6,TIME6_10,TIME10_14,TIME14_18,TIME18_22,TIME22_2,"
			+ "DATE_SUN,DATE_MON,DATE_TUE,DATE_WED,DATE_THU,DATE_FRI,DATE_SAT,"
			+ "PHOTO,VIDEO,ANIMATED_GIF,"
			+ "LOG10_USER_FAV,LOG10_USER_STATUS_COUNT,"
			+ "TOPIC,TEXT,SANITIZED_TEXT,"
			+ "SCORE";
	public String oldString() {
		return 9999 + "," +
				9999 + "," +
				vectorToString(getTimeVector()) + "," +
				vectorToString(getDayOfWeekVector()) + "," + 
				(hasPhoto() ? 1 : 0) + "," +
				(hasVideo() ? 1 : 0) + "," +
				(hasAnimatedGif() ? 1 : 0) + "," + 
				(float) Math.max(0, Math.log10(userOtherFavorites())) + "," +
				(float) Math.max(0, Math.log10(userNumStatuses())) + "," +
				topic + "," +
				text() + "," + sanitizedText() + "," + popularityScore();
	}
	
	public static String csvCategoryString = "CATEGORY,SENTIMENT,"
			+ "TIME2_4,TIME4_6,TIME6_8,TIME8_10,TIME10_12,TIME12_14,TIME14_16,TIME16_18,TIME18_20,TIME20_22,TIME22_24,TIME24_2"
			+ "DATE_SUN,DATE_MON,DATE_TUE,DATE_WED,DATE_THU,DATE_FRI,DATE_SAT,"
			+ "PHOTO,VIDEO,ANIMATED_GIF,"
			+ "USER_FAV,USER_STATUS_COUNT,"
			+ "FAVORITES,RETWEETS,TOPIC_SCORE"
			+ "TOPIC,TEXT,SANITIZED_TEXT,"
			+ "SCORE";
	public String toString() {
		return 9999 + "," +
				getSentiment() + "," +
				vectorToString(getTimeVector()) + "," +
				vectorToString(getDayOfWeekVector()) + "," + 
				(hasPhoto() ? 1 : 0) + "," +
				(hasVideo() ? 1 : 0) + "," +
				(hasAnimatedGif() ? 1 : 0) + "," + 
				userOtherFavorites() + "," + userNumStatuses() + "," +
				favorites() + "," + retweets() + "," + topicScore + "," +
				topic + "," +
				text() + "," + sanitizedText() + "," + popularityScore();
	}
	
}
