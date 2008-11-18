package message;

/**
 * Defines the different types of messages that could be exchanged between two NETBLT entities
 * @author Laurent Vanbever - Université catholique de Louvain (UCL) - INGI
 * @version 1.0 - 06 october 2008
 */
public interface MessageType {
	/**
	 * <tt>OPEN</tt> message.
	 */
	public final static byte OPEN = 0;
	
	/**
	 * <tt>RESPONSE</tt> message.
	 */
	public final static byte RESPONSE = 1;
	
	/**
	 * <tt>KEEPALIVE</tt> message.
	 */
	public final static byte KEEPALIVE = 2;
	
	/**
	 * <tt>QUIT</tt> message.
	 */
	public final static byte QUIT = 3;
	
	/**
	 * <tt>QUITACK</tt> message.
	 */
	public final static byte QUITACK = 4;
	
	/**
	 * <tt>ABORT</tt> message.
	 */
	public final static byte ABORT = 5;
	
	/**
	 * <tt>DATA</tt> message.
	 */
	public final static byte DATA = 6;
	
	/**
	 * <tt>LDATA</tt> message.
	 */
	public final static byte LDATA = 7;
	
	/**
	 * <tt>NULL-ACK</tt> message.
	 */
	public final static byte NULL_ACK = 8;
	
	/**
	 * <tt>REFUSED</tt> message.
	 */
	public final static byte REFUSED = 9;
	
	/**
	 * <tt>DONE</tt> message.
	 */
	public final static byte DONE = 10;
	
	/**
	 * <tt>GO</tt> message.
	 */
	public final static byte GO = 11;
	
	/**
	 * <tt>OK</tt> message.
	 */
	public final static byte OK = 12;
	
	/**
	 * <tt>RESEND</tt> message.
	 */
	public final static byte RESEND = 13;
}//end interface