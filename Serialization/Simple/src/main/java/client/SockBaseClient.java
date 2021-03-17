package client;

import java.net.*;
import java.io.*;

import buffers.OperationProtos.Request;
import buffers.OperationProtos.Response;

class SockBaseClient {
     

     public static void main (String args[]) throws Exception {
        Socket serverSock = null;
        OutputStream out = null;
        InputStream in = null;
        int i1=0, i2=0;
        int port = 9099; // default port

        if (args.length != 3) {
            System.out.println("Expected arguments: <host(String)> <port(int)> <data(json file)>");
            System.exit(1);
        }
        String host = args[0];
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] must be integer");
            System.exit(2);
        }
        String filename = args[2];
       

        try {
            // connect to the server
            serverSock = new Socket(host, port);

            // write to the server
            out = serverSock.getOutputStream();

            // read from the server
            in = serverSock.getInputStream();
			
			// Build Request
			Request.Builder reqBuilder = Request.newBuilder();
			reqBuilder.setOperationType(Request.OperationType.ADD);
			reqBuilder.addNums(1);
			reqBuilder.addNums(2);
			reqBuilder.addNums(3);
			reqBuilder.addNums(4);
			Request req = reqBuilder.build(); 
			
			// Send Out
			req.writeDelimitedTo(out);
			
			// Read In
			Response res = Response.parseDelimitedFrom(in);
			System.out.println("Result is: " + res.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null)   in.close();
            if (out != null)  out.close();
            if (serverSock != null) serverSock.close();
        }
    }
}

