package io.github.dantetam.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.github.dantetam.analysis.CsvQueryResponse;
import io.github.dantetam.data.CsvAssociation;

public class FileUtils {

	public static final int LARGE_FILE_CHUNK_LINES = 1000;
	
	public static <R> CsvQueryResponse<String[], R> readLargeCsvFileAsChunks(String fileName, CsvQueryResponse<String[], R> response, CsvAssociation association) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		String line = null;
		boolean firstLineRead = false;

		List<String[]> storedData = new ArrayList<>();

		try {
			while((line = reader.readLine()) != null) {
				if (!firstLineRead) {
					firstLineRead = true;
					continue;
				}
				String[] tokens = line.split(",");
				storedData.add(tokens);
				if (storedData.size() >= LARGE_FILE_CHUNK_LINES) {
					for (String[] csvRowTokens: storedData) {
						response.feedResource(csvRowTokens, association);
					}
					storedData.clear();
				}
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return response;
	}
	
	public static List<String> readShortFile(String fileName) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		String line = null;

		List<String> storedData = new ArrayList<>();

		try {
			while((line = reader.readLine()) != null) {
				storedData.add(line);
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return storedData;
	}
	
}
