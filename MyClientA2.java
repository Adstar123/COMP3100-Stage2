import java.io.*;  
import java.net.*;  

public class MyClientA2 {  

	public static void main(String[] args) {  
		try{      
			// Creating a socket connection to the server using localhost and port number 50000
			Socket mySocket = new Socket("localhost",50000);  
			// Creating an input stream reader to read data from the server
			BufferedReader dout = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
			// Creating an output stream to send data to the server
			DataOutputStream dis = new DataOutputStream(mySocket.getOutputStream()); 
			
			// Initiate the handshake with the server
			initiateHandshake(dis, dout);
			
			// Process jobs sent by the server
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

		// Send "HELO" message to server
		dis.write(("HELO\n").getBytes());
		dis.flush();

		// Read server's response
		str = dout.readLine();

		// Send "AUTH Adam" message to server
		dis.write(("AUTH Adam\n").getBytes());
		dis.flush();

		str = dout.readLine();
	}

	private static void processJobs(DataOutputStream dis, BufferedReader dout) throws IOException {
		String communicationStr = ""; 
		String[] dataResult; 

		while(true){
			// Request a new job from server
			dis.write(("REDY\n").getBytes()); 
			dis.flush();

			communicationStr = dout.readLine();

			// Split the server's response into an array of strings
			dataResult = communicationStr.split("\\ ", 0); 

			// If server's response is "NONE", there are no more jobs, so break the loop
			if(communicationStr.equals("NONE")){
				dis.write(("QUIT\n").getBytes());
				dis.close();
				break;
			}
			
			// If the server has sent a job, handle it
			if(dataResult[0].equals("JOBN")){
				handleJob(dis, dout, communicationStr, dataResult);
			}
		}
	}

	private static void handleJob(DataOutputStream dis, BufferedReader dout, String jobDetails, String[] dataResult) throws IOException {
		String communicationStr = "";
		String server = "";

		// Ask server for available servers that are capable of executing the job
		dis.write(("GETS Avail " + dataResult[4] + " " + dataResult[5] + " " + dataResult[6] + "\n").getBytes());
		communicationStr = dout.readLine();
		
		dataResult = communicationStr.split("\\ ", 0);

		// If no capable servers are available, ask the server for servers that are capable of executing the job
		if(Integer.parseInt(dataResult[1]) == 0){ 
			dis.write(("OK\n").getBytes());
			dis.flush();
			communicationStr = dout.readLine();

			dataResult = jobDetails.split("\\ ", 0);
			
			// Confirm that the server's response has been received
			dis.write(("GETS Capable " + dataResult[4] + " " + dataResult[5] + " " + dataResult[6] + "\n").getBytes());
			communicationStr = dout.readLine();

			dataResult = communicationStr.split("\\ ", 0); 
		}

		dis.write(("OK\n").getBytes());
		dis.flush();

		// Get the first server from the list of servers sent by the server
		server = getFirstServer(dout, communicationStr, dataResult[1]);
		
		// Confirm that the server's response has been received
		dis.write(("OK\n").getBytes());
		dis.flush();
		
		communicationStr = dout.readLine();

		// Schedule the job to the selected server
		String[] jobResult = jobDetails.split("\\ ", 0);
		dataResult = server.split("\\ ", 0);

		dis.write(("SCHD " + jobResult[2] + " " + dataResult[0] + " " + dataResult[1] + "\n").getBytes());
		communicationStr = dout.readLine();
	}

	private static String getFirstServer(BufferedReader dout, String str, String numOfServers) throws IOException {
		int serverCount = Integer.parseInt(numOfServers);
		String firstServer = "";

		// Loop through the list of servers sent by the server
		for(int i = 0; i < serverCount; i++){
			if(i == 0){
				str = dout.readLine();
				firstServer = str; 
				i++;
			}
			// If there's more than one server in the list, continue reading the server's responses
			if(serverCount != 1){
				str = dout.readLine();
			}
		}
		// Return the first server from the list
		return firstServer;
	}
}
