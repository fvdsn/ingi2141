
package application;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Random;
import message.*;

/**
 *
 * @author fred
 */
public class NetbltAgent extends Thread {
    Hashtable<String,MessageQueue> queue;   //Matches IP&Port to MessageQueues
    Hashtable<String,PassiveAgent> agent;
    private InetAddress localAddress;
    private int localPort;
    private boolean alive = false;
    private DatagramSocket localSocket;
    public final int maxPacketSize = 1500;
    public final int maxBufferSize;
    public final short maxDataPacketSize;
    public short burstRate = 10;
    public short burstSize = 10;
    public short deathTimerValue = 5;
    private Random uidgenerator;
    public String name;
    public BufferQueue BQ = new BufferQueue();
    
    
    public NetbltAgent(int localPort, int maxBufferSize, short maxDataPacketSize,String name){
        queue = new Hashtable();
        agent = new Hashtable();
        this.localPort = localPort;
        this.maxBufferSize = maxBufferSize;
        this.maxDataPacketSize = maxDataPacketSize;
        this.name = name;
        uidgenerator = new Random();
        try{
            localAddress = InetAddress.getByName("localhost");
            localSocket = new DatagramSocket(this.localPort,localAddress);
        }catch(Exception e){ e.printStackTrace(); }

    }
    public void run(){
        this.alive = true;
        byte[] packetBuffer = new byte[maxPacketSize];
        while(alive){
            Debug.mark(name +"::listening for packets");
            Debug.mark(name +" +->at address "+localAddress.toString()+" and port "+localPort);
            DatagramPacket incoming = new DatagramPacket(packetBuffer,maxPacketSize);
            
            try{
                localSocket.receive(incoming);
            }catch(IOException e){e.printStackTrace();}
            byte[] packetData = incoming.getData();

            int clientPort = incoming.getPort();
            InetAddress clientAddress = incoming.getAddress();
            
            
              
            Message M = new Message();
            try{ M.fromWire(packetBuffer); }catch(IOException e){e.printStackTrace();}
            Debug.mark(name +"::recieved a "+M.getTypeToString() +" message");
            processMessage(clientAddress,clientPort,M,packetBuffer);
            

        }
    }
    public void sendMessage(InetAddress remoteAddress, int remotePort, Message M){
        Debug.mark(name +"::sending a "+M.getTypeToString()+" message");
        Debug.mark(name +" +-> at address "+remoteAddress.toString()+" and port "+remotePort);
        try {
            byte[] packetData = M.toWire();
            Debug.mark(name +" +-> messagesize: "+packetData.length);
            //Debug.mark(name + M.toString());
            DatagramPacket outgoing = new DatagramPacket(packetData,
                                                         packetData.length,
                                                         remoteAddress,
                                                         remotePort);
            localSocket.send(outgoing);
        } catch (IOException ex) {ex.printStackTrace();}
        //Debug.mark(name +"::message sent");
    }
    public String hashAddress(InetAddress Address,int Port){
        /* returns a string uniquely identifying the address and port of a host*/
        return ""+Address.hashCode()+Port;
    }
    private void processMessage(InetAddress remoteAddress, int remotePort, Message M,byte[] data){
        /*What to do when a Message has been recieved*/
        Debug.mark(name +"::dispatching a "+M.getTypeToString()+" message");

        MessageQueue MQ = queue.get(hashAddress(remoteAddress,remotePort));
        if(MQ == null){ // First time we get a message from this client
            switch(M.getType()){
                case MessageType.OPEN:{ //creating a message queue and a server thread
                    Debug.mark(name +"::creating new connection");
                    MQ = new MessageQueue();
                    queue.put(hashAddress(remoteAddress,remotePort),MQ);
                    Thread ST = new PassiveAgent(this,MQ,remoteAddress,remotePort);
                    ST.start();
                    break;
                }
                default:{   // if it's not an open, we ignore it. TODO -> check appropriate response
                    Debug.mark(name +"::ignoring message");
                    Debug.mark(name +" +-> from remoteAddress :"+remoteAddress+"remotePort:"+remotePort);
                    Debug.mark(queue.toString());
                    //Debug.mark(M.toString());
                    return;
                }
            }
        }
        try{
            //Debug.mark(name +"::dispatching message");
            switch (M.getType()) {
                case MessageType.ABORT:
                case MessageType.KEEPALIVE: {
                    MQ.addMessage(M);
                    break;
                }
                case MessageType.OPEN:
                case MessageType.RESPONSE:{
                    //Debug.mark(name +"::dispatching open or response message");
                    M = new OpenAndResponse();
                    M.fromWire(data);
                    MQ.addMessage(M);
                    break;
                }
                case MessageType.DATA: {
                    M = new Data();
                    M.fromWire(data);
                    MQ.addMessage(M);
                    break;
                }

                
                case MessageType.GO:
                case MessageType.OK:{
                    M = new GoAndOk();
                    M.fromWire(data);
                    //Debug.mark(name +"::dispatching go or ok message");
                    MQ.addMessage(M);
                    break;
                }
                case MessageType.DONE: {
                }
                case MessageType.LDATA: {
                }
                case MessageType.NULL_ACK: {
                }
                

                case MessageType.QUIT: {
                }
                case MessageType.QUITACK: {
                }
                case MessageType.REFUSED: {
                }
                case MessageType.RESEND: {
                }

                default: {
                    Debug.error(name+"::ERROR::unknown message type");
                }

            } //switch(type)
        }catch(IOException e){e.printStackTrace();}
    }
    public int connect(InetAddress remoteAddress, int remotePort){
        MessageQueue MQ = new MessageQueue();
        queue.put(hashAddress(remoteAddress,remotePort), MQ);
        PassiveAgent CT = new ActiveAgent(this,MQ,remoteAddress,remotePort);
        CT.start();
        if(this.alive == false){
            this.start();
        }
        agent.put(hashAddress(remoteAddress,remotePort),CT);
        
        while(true){    /*waiting for connection*/
            CT = agent.get(hashAddress(remoteAddress,remotePort));
            if(CT == null){
                Debug.error("Could not connect");
                return -1;
            }else if(CT.getRemoteBufferSize() > 0){
                return CT.getRemoteBufferSize();
            }else{
                try{
                    sleep(50);
                }catch(Exception e){
                    Debug.error("Connection interrupted");
                    return -1;
                }
            }
        }
    }
    public synchronized void send(InetAddress remoteAddress,int remotePort,Buffer data){
        /* initializes the Agent for sending data. This operation doesn't currently
         * block, but it should TODO
         */
        MessageQueue MQ = new MessageQueue();
        queue.put(hashAddress(remoteAddress,remotePort), MQ);
        Thread CT = new ActiveAgent(this,MQ,remoteAddress,remotePort);
        CT.start();
        if (this.alive == false){
            this.start();
        }
    }
    public Buffer recieve(long timeout){
        if(this.alive == false){
            this.start();
        }
        return BQ.getBuffer(timeout);
    }
    public synchronized void disconnect(InetAddress remoteAddress,int remotePort){
        PassiveAgent A = agent.get(hashAddress(remoteAddress,remotePort));
        if(A != null){
            A.closeConnection();
        }
    }
    public synchronized void closeConnection(InetAddress remoteAddress,int remotePort){
        queue.remove(hashAddress(remoteAddress,remotePort));
        agent.remove(hashAddress(remoteAddress,remotePort));
        Debug.mark(name+":: closing connection to ip "+remoteAddress.toString()+" at port "+remotePort);
    }
    public int getUID(){
        /* returns an unique id for connection id*/
        return uidgenerator.nextInt();
    }
    public void setDTV(int DTV){
        deathTimerValue = (short)DTV;
    }
    public void setBurstRate(int br){
        burstRate = (short)br;
    }
    public void setBurstSize(int bs){
        burstSize = (short)bs;
    }
}
