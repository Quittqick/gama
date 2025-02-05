package msi.gama.headless.listener;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import msi.gama.headless.common.Globals;
import msi.gama.headless.core.GamaHeadlessException;
import msi.gaml.operators.Files; 

public class CompileEndPoint  {

//	@Override
	public void onOpen(WebSocket socket) {
//		socket.send("Hello!");
	}

//	@Override
	public void onMessage(WebSocketServer server, WebSocket socket, String message) {
		System.out.println(socket + ": String " + message);
//		socket.send(message);

	}

	public void buildFromZip(final WebSocket socket, final ByteBuffer compiledModel)
			throws IOException, GamaHeadlessException {
		FileOutputStream fos = new FileOutputStream(Globals.TEMP_PATH+"/tmp"+socket.hashCode()+".zip");
		fos.write(compiledModel.array());
		fos.close();
		System.out.println(Globals.TEMP_PATH+"/tmp"+socket);
		Files.extractFolder(null,Globals.TEMP_PATH+"/tmp"+socket.hashCode()+".zip",Globals.TEMP_PATH+"/tmp"+socket.hashCode());
//		socket.send(Globals.TEMP_PATH+"/tmp"+socket.hashCode());
//		ByteArrayInputStream bis = new ByteArrayInputStream(compiledModel.array());
//		ObjectInput in = null;
//		ExperimentJob selectedJob = null;
//		try {
//			in = new ObjectInputStream(bis);
//			Object o = in.readObject();
//			selectedJob = (ExperimentJob) o;
//		} catch (ClassNotFoundException ex) {
//			ex.printStackTrace();
//		} finally {
//			try {
//				if (in != null) {
//					in.close();
//				}
//			} catch (IOException ex) {
//				ex.printStackTrace();
//			}
//		} 

	}

//	@Override
	public void onMessage(WebSocketServer server, WebSocket socket, ByteBuffer message) {

		System.out.println(socket + ": " + message);
		try {
			buildFromZip(socket, message);
		} catch (IOException | GamaHeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}