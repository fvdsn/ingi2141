
package application;

import java.net.InetAddress;
import message.*;

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

    // Properties of this agent.
    InetAddress localAddress;
    int localPort;
    int localUID;
    int localBufferSize;
    short localDataPacketSize;
    short localBurstRate;
    short localBurstSize;
    short localDeathTimerValue;

    // properties of the agent where messages come from, and messages are sent
    InetAddress remoteAddress;
    int remotePort;
    int remoteUID;
    int remoteBufferSize;
    short remoteDataPacketSize;
    short remoteBurstRate;
    short remoteBurstSize;
    short remoteDeathTimerValue;

    public PassiveAgent(){

    }
    public PassiveAgent(NetbltAgent S,MessageQueue MQ, InetAddress remoteAddress, int remotePort){
        this.MQ = MQ;
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
        this.localBufferSize = S.maxBufferSize;
        this.S = S;
        state = "waitingforopen";
    }
    public void run(){
        while(!state.equals("closed")){
            Message M = MQ.getMessage(0);
            
            Debug.mark(S.name+"PASSIVE :: recieved a message");
            Debug.error(M == null,"Null message");

            int Mtype = M.getType();
            handleMessage(M,Mtype);
        }
    }
    public void handleMessage(Message M, int Mtype){
        Debug.mark(S.name+"PASSIVE :: handling the message");
        if (Mtype == MessageType.OPEN){
            //this.S.sendMessage(remoteAddress,remotePort,)
            Debug.mark(M.toString());
        }
    }
    public String connectionSetup(){
        return null;
    }
}
