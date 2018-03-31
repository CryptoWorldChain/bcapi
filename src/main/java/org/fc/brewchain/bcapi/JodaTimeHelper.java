package org.fc.brewchain.bcapi;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class JodaTimeHelper {
	static DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd'Z'HHmmss");

	public static String current() {
		return fmt.print(System.currentTimeMillis());
	}
	
	public static String format(long time) {
		return fmt.print(time);
	}
	
	public static void main(String[] args) {
		System.out.println(current());
	}
	
}
