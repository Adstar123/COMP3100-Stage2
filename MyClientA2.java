import java.io.*;  
import java.net.*;  

public class MyClientA2 {  

	public static void main(String[] args) {  
		try{      
			Socket mySocket = new Socket("localhost",50000);  
			BufferedReader dout = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
			DataOutputStream dis=new DataOutputStream(mySocket.getOutputStream()); 
			
			// Handshake process
			initiateHandshake(dis, dout);
			
			// Process jobs
			processJobs(dis, dout);

			// Close the socket connection
			mySocket.close();
		} 
		catch (Exception e) {
			// Print any exceptions that occur to the console
			System.out.println(e);
		}
	}

	private static void initiateHandshake(DataOutputStream dis, BufferedReader dout) throws IOException {
		String str = ""; // response from the server

		dis.write(("HELO\n").getBytes());
		dis.flush();

		str = dout.readLine();

		dis.write(("AUTH Adam\n").getBytes());
		dis.flush();

		str = dout.readLine();
	}

	private static void processJobs(DataOutputStream dis, BufferedReader dout) throws IOException {
		String communicationStr = ""; 
		String[] dataResult; 

		while(true){
			dis.write(("REDY\n").getBytes()); 
			dis.flush();

			communicationStr = dout.readLine();

			dataResult = communicationStr.split("\\ ", 0); 

			if(communicationStr.equals("NONE")){
				dis.write(("QUIT\n").getBytes());
				dis.close();
				break;
			}
			
			if(dataResult[0].equals("JOBN")){
				handleJob(dis, dout, communicationStr, dataResult);
			}
		}
	}

	private static void handleJob(DataOutputStream dis, BufferedReader dout, String jobDetails, String[] dataResult) throws IOException {
		String communicationStr = "";
		String server = "";

		dis.write(("GETS Avail " + dataResult[4] + " " + dataResult[5] + " " + dataResult[6] + "\n").getBytes());
		communicationStr = dout.readLine();
		
		dataResult = communicationStr.split("\\ ", 0);

		if(Integer.parseInt(dataResult[1]) == 0){ 
			dis.write(("OK\n").getBytes());
			dis.flush();
			communicationStr = dout.readLine();

			dataResult = jobDetails.split("\\ ", 0);
			
			dis.write(("GETS Capable " + dataResult[4] + " " + dataResult[5] + " " + dataResult[6] + "\n").getBytes());
			communicationStr = dout.readLine();

			dataResult = communicationStr.split("\\ ", 0); 
		}

		dis.write(("OK\n").getBytes());
		dis.flush();

		server = getFirstServer(dout, communicationStr, dataResult[1]);
		
		dis.write(("OK\n").getBytes());
		dis.flush();
		
		communicationStr = dout.readLine();

		String[] jobResult = jobDetails.split("\\ ", 0);
		dataResult = server.split("\\ ", 0);

		dis.write(("SCHD " + jobResult[2] + " " + dataResult[0] + " " + dataResult[1] + "\n").getBytes());
		communicationStr = dout.readLine();
	}

	private static String getFirstServer(BufferedReader dout, String str, String numOfServers) throws IOException {
		int serverCount = Integer.parseInt(numOfServers);
		String firstServer = "";

		for(int i = 0; i < serverCount; i++){
			if(i == 0){
				str = dout.readLine();
				firstServer = str; 
				i++;
			}
			if(serverCount != 1){
				str = dout.readLine();
			}
		}
		return firstServer;
	}
}
