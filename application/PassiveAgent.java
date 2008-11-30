
package application;

import java.net.InetAddress;
import message.*;
import timer.*;

/**
 *
 * @author fred
 */
public class PassiveAgent extends Thread{
    MessageQueue MQ;
    NetbltAgent S;              //The "Main Server" recieving and sending packets.
    Buffer B;
    int maxBufferSize;
    String state;               //"closed","waitingforopen",etc...

    int maxConnectionTries = 4;
    int ConnectionTimeout = 5;   //sec

    // Properties of this agent.

    // properties of the agent where messages come from, and messages are sent
    InetAddress remoteAddress;
    int remotePort;
    int remoteUID;
    int remoteBufferSize;
    short remoteDataPacketSize;
    short remoteBurstRate;
    short remoteBurstSize;
    short remoteDeathTimerValue;

    KeepAliveTimer aliveTimer = null;

    protected class KeepAliveTimer extends Timer {
        public KeepAliveTimer(double seconds){
            initialize((int)(seconds*1000),-1);
        }
        public void timeout() {
            Message M = new Message(MessageType.KEEPALIVE,(short)10);
            S.sendMessage(remoteAddress, remotePort, M);
        }
        public void terminate() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    public PassiveAgent(){

    }
    public PassiveAgent(NetbltAgent S,MessageQueue MQ, InetAddress remoteAddress, int remotePort){
        this.MQ = MQ;
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
        this.S = S;
        this.remoteUID = 0;
        state = "init";
    }
    public void run(){
        while(!state.equals("closed")){
            Message M = MQ.getMessage(remoteDeathTimerValue);
            if(M == null){
                Debug.mark(S.name+":PASSIVE:: death timer expired, closing connection");
                this.closeConnection();
                return;
            }
            Debug.mark(S.name+"PASSIVE :: recieved a "+M.getTypeToString()+" message");
            int Mtype = M.getType();
            handleMessage(M,Mtype);
        }
    }
    public void handleMessage(Message M, int Mtype){
        if (Mtype == MessageType.OPEN){
            if(state.equals("init")){
                makeResponse((OpenAndResponse)M);
                printConnectionParameters();

            }
        }
    }
    public void makeResponse(OpenAndResponse Open){
        if(remoteUID == 0){
            remoteUID = Open.connectionUniqueID;
        }else if( this.remoteUID != Open.connectionUniqueID){
            S.sendMessage(  this.remoteAddress,
                            this.remotePort,
                            new Message(MessageType.ABORT,(short)10)    );
            return;
        }
         if (Open.bufferSize > S.maxBufferSize){
            this.remoteBufferSize = S.maxBufferSize;
        }else{
            this.remoteBufferSize = Open.bufferSize;
        }
        if (Open.burstRate > S.burstRate){
            this.remoteBurstRate = S.burstRate;
        }else{
            this.remoteBurstRate = Open.burstRate;
        }
        if(Open.burstSize > S.burstSize){
            this.remoteBurstSize = S.burstSize;
        }else{
            this.remoteBurstSize = Open.burstSize;
        }
        if(Open.dataPacketSize > S.maxDataPacketSize){
            this.remoteDataPacketSize = S.maxDataPacketSize;
        }else{
            this.remoteDataPacketSize = Open.dataPacketSize;
        }
        if(Open.deathTimerValue > S.deathTimerValue){
            this.remoteDeathTimerValue = S.deathTimerValue;
        }else{
            this.remoteDeathTimerValue = Open.deathTimerValue;
        }
        OpenAndResponse R = new OpenAndResponse(MessageType.RESPONSE,
                                                remoteUID,
                                                remoteBufferSize,
                                                remoteDataPacketSize,
                                                remoteBurstRate,
                                                remoteBurstSize,
                                                remoteDeathTimerValue);
        S.sendMessage(remoteAddress,remotePort,R);
        if(B != null){
            B = new Buffer(0,remoteBufferSize,remoteDataPacketSize);
        }
        S.sendMessage(  remoteAddress,
                        remotePort,
                        new Message(MessageType.GO,(short)10)   );
        aliveTimer = new KeepAliveTimer(remoteDeathTimerValue/4.0);
        aliveTimer.start();
        this.state = "connected";
    }
    public void printConnectionParameters(){
        Debug.mark(S.name + "PRINTING CONNECTION PARAMETERS ------------");
        Debug.mark("remoteBufferSize: "+this.remoteBufferSize);
        Debug.mark("remoteBurstRate "+this.remoteBurstRate);
        Debug.mark("remoteBurstSize "+this.remoteBurstSize);
        Debug.mark("remoteDataPacketSize "+this.remoteDataPacketSize);
        Debug.mark("remoteDeathTimerV "+this.remoteDeathTimerValue);
        Debug.mark("connectionUID "+this.remoteUID);
    }
    public String connectionSetup(){
        return null;
    }
    public void closeConnection(){
        Debug.mark(S.name+"closing message handling agent");
        S.closeConnection(remoteAddress, remotePort);
        if(aliveTimer !=null){ aliveTimer.stopTimer(); }
        this.state = "closed";
        this.stop();
    }
    int getRemoteBufferSize(){
        if(this.state.equals("connected")){
            return remoteBufferSize;
        }else{
            return -1;
        }
    }
    public synchronized void sendBuffer(Buffer B){
        
    }
}
