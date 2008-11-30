package message;

import java.io.*;

/**
 * This class defines a NETBLT header. It should be extended in order to provide other features (e.g. add a new field) 
 * <p>
 * This class is also suitable to represent KEEPALIVE (type 2) and ABORT (type 5) messages as they are identical 
 * to the NETBLT header 
 * 
 * @author Laurent Vanbever - Universit� catholique de Louvain (UCL) - INGI
 * @version 1.0 - 06 october 2008
 */

public class Message implements MessageType {
	protected short checksum = (short) 0xff; // Packet checksum. Not used in this project 
	protected byte version = 0x01; // the NETBLT protocol version number. Default value.
	
	protected byte type; // the NETBLT packet type number (KEEPALIVE = 2, ABORT = 5, etc.)
	protected short length; // the total length (NETBLT header plus data, if present) of the NETBLT packet in bytes
	
	// Output stream used to write the message as a byte array (useful to build a DatagramPacket afterwards).
	protected ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
	protected DataOutputStream out = new DataOutputStream(byteStream);
	
	// Input stream used to build the message from a byte array (useful when a DatagramPacket has been received to build the corresponding Message).
	protected ByteArrayInputStream bs;
	protected DataInputStream in;
	
	/**
	 * Default constructor.
	 */
	public Message(){
		
	}//end constructor
	
	
	public Message(byte type, short length) {
		this.type = type;
		this.length = length;
	} //end constructor
	
	/*
	 * Outputs the header's content on the output stream.
	 */
	public void headerToWire() throws IOException {
		out.writeShort(checksum);
		out.writeByte(version);
		out.writeByte(type);
		out.writeShort(length);
		out.writeShort((short)0); // write padding after length field
	}
	
	/*
	 * Returns the byte array corresponding to this message
	 */
	public byte[] toWire() throws IOException {
		headerToWire();
		
		byte[] data = byteStream.toByteArray();
		return data;
	}
	
	/*
	 * Builds the message based on a received byte array
	 */
	public void fromWire(byte[] input) throws IOException {
		bs = new ByteArrayInputStream(input);
		in = new DataInputStream(bs);

		this.checksum = in.readShort();
		this.version = in.readByte();
		this.type = in.readByte();
		this.length = in.readShort();
		
		in.readShort(); // flush padding after length field
	}
	
	// Accessors methods
	
	public int getType(){
		return type;
	}
    public String getTypeToString(){
        switch (getType()){
            case MessageType.ABORT: return "ABORT";
            case MessageType.DATA: return "DATA";
            case MessageType.DONE: return "DONE";
            case MessageType.GO: return "GO";
            case MessageType.KEEPALIVE: return "KEEPALIVE";
            case MessageType.LDATA: return "LDATA";
            case MessageType.NULL_ACK: return "NULL_ACK";
            case MessageType.OK: return "OK";
            case MessageType.OPEN: return "OPEN";
            case MessageType.QUIT: return "QUIT";
            case MessageType.QUITACK: return "QUITACK";
            case MessageType.REFUSED: return "REFUSED";
            case MessageType.RESEND: return "RESEND";
            case MessageType.RESPONSE: return "RESPONSE";
            default: return "UNKNOWN";
        }
    }
	
	public void setType(byte type){
		this.type = type;
	}
	
	public void setLength(short length){
		this.length = length;
	}
	
	public int getLength(){
		return length;
	}
	
	/**
	 * Returns a String representation of <i>this</i> message.
	 */
	public String toString(){
		String toReturn = "************************\n";
		toReturn += "*   MESSAGE CONTENT    *\n";
		toReturn += "************************\n";
		toReturn +=	"Checksum: " +checksum+"\n";
		toReturn += "Version: " +version+"\n";
		toReturn += "Type: " +getTypeToString()+"\n";
		toReturn += "Length: " +length+"\n";
		return toReturn;
	}
}