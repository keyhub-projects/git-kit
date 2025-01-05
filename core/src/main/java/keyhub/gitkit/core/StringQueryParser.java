package keyhub.gitkit.core;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringQueryParser {
	public static Map<String, String> extractQueries(String content) {
		Map<String, String> queries = new HashMap<>();
		// 정규식: @Select, SQL 쿼리, 반환 타입, 메서드명, 파라미터를 캡처
		String combinedPattern = "@(Select|Insert|Update|Delete)\\(\"\"\"(.*?)\"\"\"\\)\\s+(public|private|protected)?\\s*(\\S+)\\s+(\\w+)\\s*\\((.*?)\\)";
		Pattern combinedRegex = Pattern.compile(combinedPattern, Pattern.DOTALL);
		Matcher matcher = combinedRegex.matcher(content);
		while (matcher.find()) {
			String query = matcher.group(2).trim();           // SQL Query
			String returnType = matcher.group(4);            // Return type
			String methodName = matcher.group(5);            // Method name
			String parameters = matcher.group(6).trim();     // Parameters
			// Key: "리턴타입 메서드명(파라미터)"
			String key = String.format("%s %s(%s)", returnType, methodName, parameters);
			queries.put(key, query);
		}
		return queries;
	}
}
