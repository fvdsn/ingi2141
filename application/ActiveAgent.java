
package application;

import java.net.InetAddress;
import message.MessageType;
import message.OpenAndResponse;

/**
 *
 * @author fred
 */
public class ActiveAgent extends PassiveAgent{
    
    public ActiveAgent(NetbltAgent S,MessageQueue MQ, InetAddress remoteAddress,int remotePort,byte[]data){
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
        this.MQ = MQ;
        this.S = S;
        this.state = "waitingforresponse";
    }
    public void run(){
        connectionSetup();
        dataTransfer();
    }
    public String connectionSetup(){
        Debug.mark(S.name+":ACTIVE::beginning connection setup");
        int uid = S.getUID();
        OpenAndResponse Op = new OpenAndResponse(   MessageType.OPEN,
                                                    uid,
                                                    S.maxBufferSize,
                                                    S.maxDataPacketSize,
                                                    S.burstRate,
                                                    S.burstSize,
                                                    S.deathTimerValue);
        S.sendMessage(remoteAddress, remotePort, Op);
        Debug.mark(S.name+":ACTIVE::ending connection setup");

        return "closed";
    }
    public String dataTransfer(){return "closed";}
}
