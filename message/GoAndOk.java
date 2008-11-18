package message;

import java.io.IOException;

/**
 * Defines the <tt>GO</tt> and <tt>OK</tt> message, as they have the same format except type.
 * <p>
 * A GO message is sent by the receiver to notify the sender that it's ready to receive data for a given buffer 
 * A OK message is also sent by the receiver to notify the sender that it has correctly received the buffer 
 * 
 * @author Laurent Vanbever - Universit� catholique de Louvain (UCL) - INGI
 * @version 1.0 - 06 october 2008
 */

public class GoAndOk extends Message {
	
	private short seq_number; // A 16 bit unique message number. 
	private int buffer_number; // a 32 bit unique number assigned to every buffer.
	
	public GoAndOk() {
		
	} // end constructor


	public GoAndOk(byte type, short length, short seq_number, int buffer_number) {
		super(type, length);
		this.seq_number = seq_number;
		this.buffer_number = buffer_number;
	} // end constructor
	
	public byte[] toWire() throws IOException {
		headerToWire();
		out.writeShort(seq_number);
		out.writeShort(0);
		out.writeInt(buffer_number);
		
		byte[] data = byteStream.toByteArray();
		return data;
	}
	
	public void fromWire(byte[] input) throws IOException {
		super.fromWire(input);
		this.seq_number = in.readShort();
		in.readShort(); // Alignment padding
		this.buffer_number = in.readInt();
	}
	
	// Accessors methods
	public short getSeq_number() {
		return seq_number;
	}

	public void setSeq_number(short seq_number) {
		this.seq_number = seq_number;
	}

	public int getBuffer_number() {
		return buffer_number;
	}

	public void setBuffer_number(int buffer_number) {
		this.buffer_number = buffer_number;
	}
	
	/**
	 * Returns a String representation of <i>this</i> message.
	 */
	public String toString() {
		String toReturn = super.toString();
		toReturn += "Sequence number: "+seq_number+"\n";
		toReturn += "Buffer number: "+buffer_number+"\n";
		return toReturn;
	}
}