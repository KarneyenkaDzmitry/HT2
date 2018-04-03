package util;

class DBConfigs {
	protected String dbHost = "localhost";
	protected String dbPort = "3306";
	protected String dbUser = "user=root";
	protected String dbPass = "password=1qw23er4";
	protected String dbName = "phonebook";
	protected String configString = "jdbc:mysql://"+dbHost+"/"+dbName+"?" +dbUser +"&"+dbPass+""
			+ "&useUnicode=true&characterEncoding=UTF-8&characterSetResults=utf8&connectionCollation=utf8_general_ci";
}
