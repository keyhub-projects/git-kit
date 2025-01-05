package keyhub.sample.common;

public class TraceUtil {
	private static final ThreadLocal<String> USER_ID = new ThreadLocal<>();
	private static final ThreadLocal<String> GUID = new ThreadLocal<>();

	public static String traceUserId() {
		return USER_ID.get();
	}

	public static void traceUserId(String adminId) {
		USER_ID.set(adminId);
	}

	public static String guid() {
		return GUID.get();
	}

	public static void guid(String guid) {
		GUID.set(guid);
	}
}
