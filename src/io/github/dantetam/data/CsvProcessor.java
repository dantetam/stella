package io.github.dantetam.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.ejml.ops.ReadCsv;

public class CsvProcessor {

	public static void readCsv(String fileName) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
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
				if (storedData.size() >= 1000) {
					System.out.println(storedData.size());
					storedData.clear();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		/*if (args.length < 2) {
			args = new String[]{"/data/acs-sample-test.csv"};
		}*/
		
		/*File folder = new File("data");
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println("File " + listOfFiles[i].getName());
			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}*/

		//String fileName = new String("data/acs-sample-test.csv");
		String fileName = new String("data/ss15pca.csv");
		readCsv(fileName);
	}

}
