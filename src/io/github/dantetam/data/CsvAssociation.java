package io.github.dantetam.data;

import java.util.HashMap;
import java.util.Map;

/*
 	//This file handles Stella's forms (small scale such as user input) as well as general .csv content (large scale such as a sample of ACS responses)

	//TODO: Analyze databases of questionnaire answers (such as the ACS sample responses)
	//use Google Charts to get charts of response rates and proportions,
	//as well as means, std.dev., confidence intervals, etc., for certain answers like income
	//as well as simple text queries such as "average income in the SF region not counting above $250000"
*/

public class CsvAssociation {

	private Map<String, Map<String, String>> associations;
	private Map<String, String> columnsFullNames;

	public CsvAssociation() {
		associations = new HashMap<>();
		columnsFullNames = new HashMap<>();
	}

	public void initColumn(String colName) {
		associations.put(colName, new HashMap<String, String>());
	}
	
	public void setColumnFullName(String colName, String fullName) {
		columnsFullNames.put(colName, fullName);
	}
	
	public void addDataColumn(String col, String key, String value) {
		Map<String, String> colData = associations.get(col);
		colData.put(key, value);
	}
	
	public String getColumnFullName(String colName) {
		return columnsFullNames.get(colName);
	}
	
	public Map<String, String> getAssociationsForColumn(String colName) {
		return associations.get(colName);
	}

}
