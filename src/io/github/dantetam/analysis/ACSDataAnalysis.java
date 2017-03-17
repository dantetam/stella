package io.github.dantetam.analysis;

import com.sun.jna.platform.FileUtils;

import io.github.dantetam.data.CsvAssociation;
import io.github.dantetam.data.CsvAssociationParser;
import io.github.dantetam.data.CsvProcessor;

public class ACSDataAnalysis {
	
	public static class CountCsvQueryResponse extends CsvQueryResponse<String[], Integer> {

		private StringQuery stringQuery;
		protected int count;
		
		public CountCsvQueryResponse(StringQuery query) {
			super();
			stringQuery = query;
			count = 0;
		}
		
		@Override
		public void feedResource(String[] input, CsvAssociation association) {
			if (stringQuery.allowed(input)) {
				
			}
		}
		
	}
	
	//TODO: Uncomment all the code in this method and test it
	public static void main(String[] args) {
		StringQuery countFirstLess100 = (String[] data) -> Integer.parseInt(data[0]) < 100;
		CountCsvQueryResponse testQuery = new CountCsvQueryResponse(countFirstLess100) {
			public Integer singleResponseComputation() {
				return count;
			}
		};
		
		String associationFileName = "data/acs-associations.txt";
		//List<String> associationFileData = FileUtils.readShortFile(associationFileName);
		//CsvAssociationParser.parseDataAssociationText(associationFileData);
		
		String fileName = new String("data/ss15pca.csv");
		//FileUtils.readLargeCsvFileAsChunks(fileName, testQuery, association);
	}
	
	public interface StringQuery {
		boolean allowed(String[] data);
	}
	
}