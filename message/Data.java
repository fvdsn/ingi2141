package message;

import java.io.IOException;

/**
 * Defines the <tt>DATA</tt> and <tt>LDATA</tt> packet, as they have the same format except type.
 * <p>
 * Both messages are DATA packets corresponding to a given buffer and are sent by the sending NETBLT. 
 * A LDATA packet corresponds to the last packet of a given buffer.
 * 
 * @author Laurent Vanbever - Universit� catholique de Louvain (UCL) - INGI
 * @version 1.0 - 06 october 2008
 */

public class Data extends Message {

	private int bufferNumber; // a 32 bit unique number assigned to every buffer.
	private short highConsecutiveSeq; // highest control message sequence number below which all sequence numbers received are consecutive
	private short packetNumber; // DATA packet identifier
	private boolean belongsToLastBuffer; // flag set when the buffer that this DATA packet belongs to is the last buffer in the transfer
	private byte[] payload; // DATA packet payload (i.e. one piece of the file being transferred)
	public static final int headerLength = 20;
	public Data() {
		
	} // end constructor
	
	public Data(byte type, short length, int bufferNumber, short highConsecutiveSeq, short packetNumber,
			boolean belongsToLastBuffer, byte[] payload) {
		super(type, length);
		this.bufferNumber = bufferNumber;
		this.highConsecutiveSeq = highConsecutiveSeq;
		this.packetNumber = packetNumber;
		this.belongsToLastBuffer = belongsToLastBuffer;
		this.payload = payload;
	} // end constructor
	
	public byte[] toWire() throws IOException {
		headerToWire();
		
		out.writeInt(bufferNumber);
		out.writeShort(highConsecutiveSeq);
		out.writeShort(packetNumber);
		out.writeByte(0);
		out.writeBoolean(belongsToLastBuffer);
		out.writeShort(0);
		out.write(payload);
		
		byte[] data = byteStream.toByteArray();
		return data;
	}
	
	
	public void fromWire(byte[] input) throws IOException {
		super.fromWire(input);
		
		this.bufferNumber = in.readInt();
		this.highConsecutiveSeq = in.readShort();
		this.packetNumber = in.readShort();
		in.readByte();
		this.belongsToLastBuffer = in.readBoolean();
		in.readShort();
		this.payload = new byte[this.length-20];
		in.read(this.payload, 0, this.length-20);
	}

	// Accessors methods
	public int getBufferNumber() {
		return bufferNumber;
	}

	public void setBufferNumber(int bufferNumber) {
		this.bufferNumber = bufferNumber;
	}

	public synchronized short getHighConsecutiveSeq() {
		return highConsecutiveSeq;
	}

	public synchronized void setHighConsecutiveSeq(short highConsecutiveSeq) {
		this.highConsecutiveSeq = highConsecutiveSeq;
	}

	public short getPacketNumber() {
		return packetNumber;
	}

	public void setPacketNumber(short packetNumber) {
		this.packetNumber = packetNumber;
	}

	public boolean isBelongsToLastBuffer() {
		return belongsToLastBuffer;
	}

	public void setBelongsToLastBuffer(boolean belongsToLastBuffer) {
		this.belongsToLastBuffer = belongsToLastBuffer;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	
	/**
	 * Returns a String representation of <i>this</i> message.
	 */
	public String toString() {
		String toReturn = super.toString();
		toReturn += "Buffer Number : "+this.bufferNumber+"\n";
		toReturn += "Highest consecutive ack received : "+this.highConsecutiveSeq+"\n";
		toReturn += "Packet Number : "+this.packetNumber+"\n";
		toReturn += "Belongs to last buffer ? : "+this.belongsToLastBuffer+"\n";
		return toReturn;
	}
}