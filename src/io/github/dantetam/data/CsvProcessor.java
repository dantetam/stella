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

/*
 * A class for reading in a large CSV file in a stream of chunks
 * where each chunk is processed at a time.
 */
public class CsvProcessor {

	public static void readCsv(String fileName) {
		
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
