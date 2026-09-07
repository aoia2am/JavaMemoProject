package javamemoproject;

public class ConsoleUtil {

	public static void showDivider() {
		System.out.println();
		System.out.println("----------------");
		System.out.println();
	}

	public static void showLocation(String location) {
		showDivider();
		System.out.println("現在地：" + location);
		System.out.println();
	}
}
