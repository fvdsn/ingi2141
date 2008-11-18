package message;

import java.io.IOException;

/**
 * Defines a <tt>NULL-ACK</tt> message.
 * <p>
 * This message is sent by sender to notify the receiver that it has received control messages 
 * when it has no more data to send.
 * 
 * @author Laurent Vanbever - Université catholique de Louvain (UCL) - INGI
 * @version 1.0 - 06 october 2008
 */

public class NullAck extends Message {
	
	private short highest_ack; // High consecutive sequence number received by the sender
	
	public NullAck() {
		
	} // end constructor
	
	public NullAck(byte type, short length, short highest_ack) {
		super(type, length);
		this.highest_ack = highest_ack;
	} // end constructor
	
	public byte[] toWire() throws IOException {
		headerToWire();
		out.writeShort(highest_ack);
		out.writeShort(0); // write padding after 'High Consecutive Seq Num Rcvd' field
		byte[] data = byteStream.toByteArray();
		return data;
	}
	
	public void fromWire(byte[] input) throws IOException {
		super.fromWire(input);
		this.highest_ack = in.readShort();
		in.readShort(); // flush alignment padding after 'High Consecutive Seq Num Rcvd' field
	}

	// Accessors methods	
	public short getHighestAck() {
		return highest_ack;
	}

	public void setHighestAck(short seq_number) {
		this.highest_ack = seq_number;
	}
	
	/**
	 * Returns a String representation of <i>this</i> message.
	 */	
	public String toString() {
		String toReturn = super.toString();
		toReturn += "Highest sequence number received: "+highest_ack+"\n";
		return toReturn;
	}
}
