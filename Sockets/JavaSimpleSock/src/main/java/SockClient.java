import java.net.*;
import java.io.*;
import java.util.Scanner;
import org.json.*;

/**
 * A class to demonstrate a simple client-server connection using sockets.
 * Ser321 Foundations of Distributed Software Systems
 * see http://pooh.poly.asu.edu/Ser321
 *
 * @author Tim Lindquist Tim.Lindquist@asu.edu
 * Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version April 2020
 * @modified-by David Clements <dacleme1@asu.edu> September 2020
 * @modified-by Kevin Moore <klmoor21@asu.edu> February 2021
 */
class SockClient {
  public static void main(String args[]) {
    Socket sock = null;
    String host = "localhost";
    String message = "";
    Integer number = 0;
    Scanner scanner = new Scanner(System.in);

    try {
      // open the connection
      sock = new Socket(host, 8888); // connect to host and socket on port 8888
      // get output channel
      OutputStream out = sock.getOutputStream();
      // create an object output writer (Java only)
      ObjectOutputStream os = new ObjectOutputStream(out);
      ObjectInputStream in = new ObjectInputStream(sock.getInputStream());

      serverConnection : while (true) {
		  System.out.println("Enter 1 to enter a String, 2 to enter a Number, or 0 to Exit.");
		  int choice = scanner.nextInt();
		  scanner.nextLine();
		  switch(choice) {
			  case 1:
				System.out.print("Please enter a String to send to the Server (enter \"exit\" to quit\"): ");
				message = scanner.nextLine();
				os.writeObject("{'type':'message', 'value':" + message + "'}'");
				break;
			  case 2:
			    System.out.print("Please enter a Number to send to the Server (enter 0 to quit\"): ");
				number = scanner.nextInt();
				scanner.nextLine();
				os.writeObject("{'type':'message', 'value':" + number + "'}'");
				break;
			  case 0:
				System.out.println("EXIT");
				os.writeObject("{'type':'exit'}");
				break serverConnection;
		  }
		  
        String response = (String) in.readObject();
		JSONObject json = new JSONObject(response);
		if(json.getBoolean("ok")) {
			if(json.getString("type").equals("message")) {
				System.out.println("Here is your return : " + json.getString("value"));
			} else if(json.getString("type").eqsuals("message")) {
				System.out.println("Here is your return : " + json.getInt("value"));
			} else if (json.getString("type").equals("exit")) {
				System.out.println("BYE");
			}
		} else {
			System.out.println("WRONG");
		}
	  }


/* 
        //if sever is exiting, exit as well
        if (i.equalsIgnoreCase("exiting")) {
          System.out.println(i);
          break;
        }

        System.out.println(i);
      } */

      //close resources
      scanner.close();
      os.close();
      in.close();
      sock.close();
    } catch(ConnectException e){
      System.out.println("Connection Error");
    }catch (Exception e) {
      e.printStackTrace();
    }
  }
}