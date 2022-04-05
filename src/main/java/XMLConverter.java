package main.java;

public class XMLConverter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(":::: Starting TTCS Middleware ::::");

		try {
			while (true) {
				Worker worker = new Worker();
				worker.execute();

				Thread.sleep(worker.getDelay());
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
