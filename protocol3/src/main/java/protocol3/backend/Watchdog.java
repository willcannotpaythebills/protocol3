package protocol3.backend;

import java.util.Set;

public class Watchdog implements Runnable {
	private static Thread _thread;
	private static int timer = 5;

	public static void start() {
		if (_thread == null) {
			_thread = new Thread(new Watchdog());
			_thread.start();
		}
	}

	public static void reset() {
		timer = 5;
	}

	@Override
	public void run() {
		System.out.println("[protocol3] Watchdog thread started");
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				// ignore interrupted exceptions
			}
			if (timer == 0) {
				printStackTrace();
				timer = 10;
			} else {
				--timer;
			}
		}
	}

	private static void printStackTrace() {
		// Get all threads in Java.
		Set<Thread> threads = Thread.getAllStackTraces().keySet();

		for (Thread thread : threads) {
			// Print the thread name and current state of thread.
			System.out.println("Thread Name:" + thread.getName());
			System.out.println("Thread State:" + thread.getState());

			// Get the stack trace for the thread and print it.
			StackTraceElement[] stackTraceElements = thread.getStackTrace();
			for (StackTraceElement stackTraceElement : stackTraceElements) {
				System.out.println("\t" + stackTraceElement);
			}
			System.out.println("\n");
		}
	}
}
