import java.io.*;  
import java.net.*;  
import java.util.arraylist.*;
public class MyClient {  
public static void main(String[] args) {  
try{      
	// -- Socket Creation -- //
	Socket mySocket=new Socket("localhost",50000);  
	BufferedReader dis = new BufferedReader(new InputStreamReader(s.getInputStream()));
	DataOutputStream dout=new DataOutputStream(s.getOutputStream()); 
	
	// -- Variables -- //
	String store[] = new String[10];
	String[] storeTemp;
	int core = 0;
	int memory = 0;
	int disk = 0;
	int serverID=0; // Tells LRR which server to schedule job to
	int jobID = 0; // Tells LRR which job to schedule
	ArrayList available = new ArrayList();


	// -- Handshake -- //
	dout.write(("HELO\n").getBytes());
	dout.flush(); 
	 
	String str=(String)dis.readLine();
	System.out.println(str);
	  
	dout.write(("AUTH Adam\n").getBytes());
	dout.flush();
	
	str=dis.readLine(); 
	System.out.println(str);
	
	// -- Job Scheduler While Loop -- //
	while(!str.contains("NONE")){  // WHILE LOOP START
	
	// -- Find Server Information -- //
	dout.write(("REDY\n").getBytes());
	dout.flush();
	
	str=dis.readLine(); 
	System.out.println(str);
	
	dout.write(("GETS Avail " + core + " " + memory + " " + disk "\n").getBytes()); // Get server data
	dout.flush();
	
	str=dis.readLine(); 
	System.out.println(str);
	
	dout.write(("OK\n").getBytes());
	dout.flush();
	

	int nRecs = Integer.parseInt(String.valueOf(str.charAt(5))); // Find Number of Records (Servers)

	for (int i=0; i<nRecs; i++){
		str = dis.readLine();
		System.out.println(str);
		
        	storeTemp = str.split(" ", 9);
		coreNum = Integer.parseInt(String.valueOf(storeTemp[4])); // Find number of cores in this server type
    	
    		if(topCores <= coreNum){ // Find Server type with most cores
    			topCores=coreNum;
    			store = storeTemp;
    			numServers = Integer.parseInt(String.valueOf(store[1])); // Find number of servers of the largest type
		}
	}

	dout.write(("OK\n").getBytes());
	dout.flush();
	
	
		if(serverID>numServers) serverID=0; // Reset to Server 0 when last server has been utilised

		dout.write(("REDY\n").getBytes());
		dout.flush();
		
		str=dis.readLine(); 
		System.out.println("str print 1 = " + str);
		
		dout.write(("OK\n").getBytes());
		dout.flush();
		
		str=dis.readLine(); 
		System.out.println("str print 2 = " + str);


		if(str.split(" ")[0].equals("JOBN")){ //Schedule a job if server sends one (Don't schedule one if server sends a JCPL)
			System.out.println("Schedule Job " + jobID + " to server " + store[0] + " " + serverID );
			dout.write(("SCHD " + jobID + " " + store[0] + " " + serverID + "\n").getBytes());
			dout.flush();
	
			while(!str.contains("OK")) str=dis.readLine(); // Wait until server acknowledges schedule
			
			jobID++; // Increment to next job
			serverID++; // Increment to next server
		} //END IF
		
 	} // END WHILE
	
		// -- QUIT -- //
	dout.write(("QUIT\n").getBytes());
	dout.flush();

	dout.close();  
	mySocket.close();  
}catch(Exception e){System.out.println(e);}  
}  
} 
