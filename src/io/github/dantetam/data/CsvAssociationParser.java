package io.github.dantetam.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.util.StringUtils;

public class CsvAssociationParser {

	//This file handles Stella's forms (small scale such as user input) as well as general .csv content (large scale such as a sample of ACS responses)

	//TODO: Analyze databases of questionnaire answers (such as the ACS sample responses)
	//use Google Charts to get charts of response rates and proportions,
	//as well as means, std.dev., confidence intervals, etc., for certain answers like income
	//as well as simple text queries such as "average income in the SF region not counting above $250000"

	public static CsvAssociation parseDataAssociationText(List<String> lines) {
		CsvAssociation result = new CsvAssociation(); 
		
		String currentColumn = "", currentColumnFullName = "";
		boolean lastLineAbbr = false;
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (line.isEmpty()) continue;
			String[] tokens = line.split(" ");
			if (lastLineAbbr) {
				lastLineAbbr = false;
				currentColumnFullName = line.trim();
				result.setColumnFullName(currentColumn, currentColumnFullName);
				continue;
			}
			if (tokens.length == 2) {
				if (StringUtils.isAlphanumeric(tokens[0]) && tokens[0].equals(tokens[0].toUpperCase())) {
					currentColumn = tokens[0];
					lastLineAbbr = true;
					result.initColumn(currentColumn);
					continue;
				}
			}
			//The line is a key-value pair association for the current column
			tokens = line.split(" .");
			String key = tokens[0].trim();
			String value = tokens[1];
			result.addDataColumn(currentColumn, key, value);
		}

		return result;
	}

	//csvRow is of the format "DATA_1,DATA_2,...,DATA_N" directly as numbers from the CSV file
	//columns contains ["COL1", "COL2", ...]
	//associations is the file parsed in parseDataAssociationText(<associations file>)
	//One example is this file is the ACS manual found here:
	//http://www2.census.gov/programs-surveys/acs/tech_docs/pums/data_dict/PUMS_Data_Dictionary_2011-2015.txt
	public Map<String, String> convertRowToTextData(String csvRow, String[] columnsList, CsvAssociation associations) {
		String[] tokens = csvRow.split(",");
		Map<String, String> result = new HashMap<>();
		if (tokens.length == columnsList.length) {
			for (int i = 0; i < tokens.length; i++) {
				String fullColString = associations.getColumnFullName(columnsList[i]);
				Map<String, String> numToStringMap = associations.getAssociationsForColumn(columnsList[i]);
				String dataNumberToText = numToStringMap.get(tokens[i]);
				result.put(fullColString, dataNumberToText);
			}
		}
		return result;
	}


	/*public String convertTextToRow(commaText, columnsList, associations) {

	}*/
	
	
}
