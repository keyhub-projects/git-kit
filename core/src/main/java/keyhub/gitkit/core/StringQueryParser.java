/*
 * MIT License
 *
 * Copyright (c) 2024 KH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
