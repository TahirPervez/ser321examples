package server;

import java.net.*;
import java.io.*;

import buffers.OperationProtos.Request;
import buffers.OperationProtos.Response;

/**
- server 
- multi thread
-- create thread
-- handle client in thread
-- (close connection when client disconnects)
*/
class SockBaseServer {
    public static void main (String args[]) throws Exception {

        int count = 0;
        ServerSocket    serv = null;
        Socket clientSocket = null;
        int port = 9099; // default port
        int sleepDelay = 10000; // default delay
        if (args.length != 2) {
          System.out.println("Expected arguments: <port(int)> <delay(int)>");
          System.exit(1);
		}
        
        try {
          port = Integer.parseInt(args[0]);
          sleepDelay = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
          System.out.println("[Port|sleepDelay] must be an integer");
          System.exit(2);
        }
        try {
            serv = new ServerSocket(port);
        } catch(Exception e) {
          e.printStackTrace();
          System.exit(2);
        }
        while (serv.isBound() && !serv.isClosed()) {
            System.out.println("Ready...");
			clientSocket = serv.accept();
			System.out.println("Threaded server conneceted to client-" + count);
			ThreadedSockServer myServerThread = new ThreadedSockServer(clientSocket, count++);
			myServerThread.start();
        }
    }
	static class ThreadedSockServer extends Thread {	
		InputStream in = null;
		OutputStream out = null;
		Socket conn = null;
		int id = 0;
		
		public ThreadedSockServer(Socket sock, int id) {
			this.conn = sock;
			this.id = id;
		}
		
		public void run() { 
			try {
				// goes in run
                in = conn.getInputStream();
                out = conn.getOutputStream();
				
				// Read in
				Request req = Request.parseDelimitedFrom(in);
				System.out.println(req.toString());
				
				int val = 0;
				if(req.getOperationType() == Request.OperationType.ADD) {
					// Add
					for (int num: req.getNumsList()) {
						val += num;
					}
				} else if(req.getOperationType() == Request.OperationType.SUB) {
					
				}

                
				// Create Out
				Response.Builder resBuilder = Response.newBuilder();
				resBuilder.setSuccess(true);
				resBuilder.setResult(val);
				Response res = resBuilder.build();
				
				// Send out
				res.writeDelimitedTo(out);

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
				try {
					if (out != null) {
						out.close(); // run
					}
					if (in != null) {
						in.close(); // run
					}
					if (conn != null){
						conn.close(); // either
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
		}
	}
}

