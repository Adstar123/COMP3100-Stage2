import java.io.*;  
import java.net.*;  
public class MyClient {  
public static void main(String[] args) {  
try{      
	// -- Socket Creation -- //
	Socket mySocket=new Socket("localhost",50000);  
	BufferedReader dis = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
	DataOutputStream dout=new DataOutputStream(mySocket.getOutputStream()); 
	
	// -- Variables -- //
	String store[] = new String[10];
	String[] storeTemp;
	int core = 0;
	int memory = 0;
	int disk = 0;
	int jobID = 0; // Tells RR which job to schedule
	boolean unavailable = false;


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
	
	String jobSpecs[] = new String[10];
	
	if(unavailable==true){
		dout.write(("DEQJ GQ\n").getBytes());
		dout.flush();
		unavailable=false;
	}
	
	// -- Find Server Information -- //
	dout.write(("REDY\n").getBytes());
	dout.flush();
	
	str=dis.readLine(); 
	System.out.println(str);
	jobSpecs=str.split(" ", 7);
		
	core = Integer.parseInt(String.valueOf(jobSpecs[4]));
	memory = Integer.parseInt(String.valueOf(jobSpecs[5]));
	disk = Integer.parseInt(String.valueOf(jobSpecs[6]));
	
	dout.write(("GETS Avail " + core + " " + memory + " " + disk + "\n").getBytes()); // Get server data
	dout.flush();
	
	str=dis.readLine(); 
	System.out.println("String split = " + str.split(" ")[1]);
	
	// if(str.split(" ")[1].equals("0")) unavailable = true;

	dout.write(("OK\n").getBytes());
	dout.flush();
	

	int nRecs = Integer.parseInt(String.valueOf(str.charAt(5))); // Find Number of Records (Servers)

	for (int i=0; i<nRecs; i++){
		str = dis.readLine();
		System.out.println(str);
		
		storeTemp = str.split(" ", 9);
    	
    		if(i==0){ // Save first available server
    			store = storeTemp;
		}
	}

	dout.write(("OK\n").getBytes());
	dout.flush();

		dout.write(("REDY\n").getBytes());
		dout.flush();
		
		str=dis.readLine(); 
		System.out.println("str print 1 = " + str);
		
		dout.write(("OK\n").getBytes());
		dout.flush();
		
		str=dis.readLine(); 
		System.out.println("str print 2 = " + str);


		if(str.split(" ")[0].equals("JOBN") && unavailable==false){ //Schedule a job if server sends one (Don't schedule one if server sends a JCPL)
			System.out.println("Schedule Job " + jobID + " to server " + store[0] + " " + store[1]);
			dout.write(("SCHD " + jobID + " " + store[0] + " " + store[1] + "\n").getBytes());
			dout.flush();
	
			while(!str.contains("OK")) str=dis.readLine(); // Wait until server acknowledges schedule
			
			jobID++; // Increment to next job
		} //END IF
	
		if(unavailable == true){
			dout.write(("ENGJ GQ\n").getBytes());
			dout.flush();
		}
	
 	} // END WHILE
	
		// -- QUIT -- //
	dout.write(("QUIT\n").getBytes());
	dout.flush();

	dout.close();  
	mySocket.close();  
}catch(Exception e){System.out.println(e);}  
}  
}