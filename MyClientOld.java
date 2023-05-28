import java.io.*;  
import java.net.*;  

public class MyClient {  
	public static void main(String[] args) {  
		try{      
			
			// -- Initialize empty string variables for communication and server details 
			String serverResponse = "";
    		String selectedServer = "";
			// Socket Creation  
			Socket schedulerSocket=new Socket("localhost",50000);   
			BufferedReader dout = new BufferedReader(new InputStreamReader(schedulerSocket.getInputStream()));
			DataOutputStream dis=new DataOutputStream(schedulerSocket.getOutputStream()); 
	

			// Starts the handshake
			dis.write(("HELO\n").getBytes()); 
   		 	dis.flush();
    		serverResponse = (String) dout.readLine();
			dis.write(("AUTH Adam\n").getBytes());
			dis.flush();
    		serverResponse = (String) dout.readLine();
			String responseSegments[]; // this is used for storing the split segments of serverresponse
	
	
			while(true){
				// Initialize empty strings to hold job and number of servers 
				String jobDetails = "";
				String serverCount = "";
				dis.write(("REDY\n").getBytes()); // this indicates that the client is ready for a job
				dis.flush();
				// Read the server's response to the "REDY" message
				serverResponse = dout.readLine();
				responseSegments = serverResponse.split("\\ ", 0); // this splits the received string into parts
				if(serverResponse.equals("NONE")){
					// when no more jobs are available the below code closes the socket and ends the connection
					dis.write(("QUIT\n").getBytes()); // the QUIT command notifies the server that it is closing the connection
					dis.close(); 
					schedulerSocket.close();
					break;
				}
		
				// The below code checks if a job notification has been received 
				if(responseSegments[0].equals("JOBN")){
					// Store the details of the new job
					jobDetails = serverResponse;
					// The following fetches servers that can handle the job
					dis.write(("GETS Avail " + responseSegments[4] + " " + responseSegments[5] + " " + responseSegments[6] + "\n").getBytes());
					serverResponse = dout.readLine();
					// Split the server's response into individual segments and store them in the Dataresult array
					responseSegments = serverResponse.split("\\ ", 0);
					// The following code does GETS capable if there are no servers available.
					if(Integer.parseInt(responseSegments[1]) == 0){  
						dis.write(("OK\n").getBytes());
						dis.flush();
						serverResponse = dout.readLine();
						responseSegments = jobDetails.split("\\ ", 0);
						dis.write(("GETS Capable " + responseSegments[4] + " " + responseSegments[5] + " " + responseSegments[6] + "\n").getBytes());
						serverResponse = dout.readLine();
						responseSegments = serverResponse.split("\\ ", 0); // Data line for numservers
					}

				// Store the number of servers available or capable to process the job	
				serverCount = responseSegments[1];
				dis.write(("OK\n").getBytes());
				dis.flush();
				// This chooses the first available server to handle the job
				selectedServer = getFirstServer(dout, serverResponse, responseSegments[1]);
				dis.write(("OK\n").getBytes());
				dis.flush();
				serverResponse = dout.readLine();
				String jobSegments[] = jobDetails.split("\\ ", 0);
				responseSegments = selectedServer.split("\\ ", 0);
				// Scheduling the job to the selected server
				dis.write(("SCHD " + jobSegments[2] + " " + responseSegments[0] + " " + responseSegments[1] + "\n").getBytes());
				serverResponse = dout.readLine();
				} 
			}


} 
	catch (Exception e) {
		// Print any exceptions that occur to the console
		System.out.println(e);
	}
}

public static String getFirstServer(BufferedReader dout, String serverResponse, String serverCount) throws IOException { 
	// This is the method to retrieve the first available server
	int numOfServers = Integer.parseInt(serverCount);

	String availableServer = "";
	for(int i = 0; i < numOfServers; i++){
		// Read the details of the first server
		if(i == 0){
			serverResponse = (String) dout.readLine(); 
			availableServer = serverResponse; // Storing the first server details
			i++;
		}
		if(numOfServers != 1){
			serverResponse = (String) dout.readLine(); // This reads through the remaining servers that are sent through
		}
	}
	return availableServer;
	}
}