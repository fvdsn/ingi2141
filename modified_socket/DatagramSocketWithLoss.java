package modified_socket;

import java.io.*;
import java.net.*;

/**
 * Defines a modified socket that introduces losses in the network.
 * 
 * The drop probability is customizable by changing the drop_probability
 * through the constructor.
 * 
 * @author Laurent Vanbever - Universite catholique de Louvain (UCL) - INGI
 * @version 1.0 - 28 sept. 08
 */

public class DatagramSocketWithLoss extends DatagramSocket {
	private double drop_probability; // drop probability

	/** 
	 * Constructs an unreliable datagram socket and binds it to any available port on the local host machine.
	 * 
	 * @param drop_probability The associated drop probability
	 * 
	 * @throws SocketException if the socket could not be opened, or the socket could not bind to the specified local port. 
	 */
	public DatagramSocketWithLoss(double drop_probability) throws SocketException {
		super(); 
		this.drop_probability = drop_probability;
	}	

	/** 
	 * Creates an unbound and unreliable datagram socket with the specified DatagramSocketImpl
	 * 
	 * @param impl an instance of a DatagramSocketImpl  the subclass wishes to use on the DatagramSocket
	 * @param drop_probability The associated drop probability 
	 * 
	 */
	public DatagramSocketWithLoss(DatagramSocketImpl impl, double drop_probability) {
		super(impl);
		this.drop_probability = drop_probability;
	}

	/** 
	 * Creates an unbound and unreliable datagram socket with the specified DatagramSocketImpl
	 * 
	 * @param impl an instance of a DatagramSocketImpl  the subclass wishes to use on the DatagramSocket
	 * @param drop_probability The associated drop probability 
	 * 
	 * @throws SocketException if the socket could not be opened, or the socket could not bind to the specified local port. 
	 */	
	public DatagramSocketWithLoss(int port, double drop_probability) throws SocketException {
		super(port);
		this.drop_probability = drop_probability;
	}

	/**
	 * Creates an unreliable datagram socket, bound to the specified local address. The local port must be between 0 and 65535 inclusive. If the IP address is 0.0.0.0, the socket will be bound to the wildcard address, an IP address chosen by the kernel.
	 * <p> If there is a security manager, its checkListen method is first called with the port argument as its argument to ensure the operation is allowed. This could result in a SecurityException. </p>
	 * 
	 * @param port local port to use
	 * @param laddr local address to bind
	 * @param drop_probability associated drop probability
	 * @throws SocketException if the socket could not be opened, or the socket could not bind to the specified local port. 
	 */
	public DatagramSocketWithLoss(int port, InetAddress laddr, double drop_probability) throws SocketException {
		super(port, laddr);
		this.drop_probability = drop_probability;
	}

	/**
	 * Creates an unreliable datagram socket, bound to the specified local socket address.
	 * <p> If, if the address is null, creates an unbound socket.</p>
	 * <p> If there is a security manager, its checkListen method is first called with the port from the socket address as its argument to ensure the operation is allowed. This could result in a SecurityException.</p>
	 * 
	 * @param binaddr local socket address to bind, or null for an unbound socket.
	 * @param drop_probability associated drop probability
	 * 
	 * @throws SocketException if the socket could not be opened, or the socket could not bind to the specified local port. 
	 */
	public DatagramSocketWithLoss(SocketAddress bindaddr, double drop_probability) throws SocketException {
		super(bindaddr);
		this.drop_probability = drop_probability;
	}

	/** Sends a DatagramPacket over the network in an unreliable way. 
	 * 
	 * @param p  a datagram that needs to be sent over the network
	 * 
	 * @throws IOException if an I/O error occurs
	 */

	public void send(DatagramPacket p) throws IOException {
		double random = Math.random();
		if(drop_probability <= random) {
			super.send(p);
		} else {
			// DEBUG:
			// System.out.println("Throwing away this datagram...");
		}
	}
}