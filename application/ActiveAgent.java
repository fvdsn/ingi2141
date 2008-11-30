
package application;

import java.net.InetAddress;
import message.*;
import message.MessageType;
import message.OpenAndResponse;

/**
 *
 * @author fred
 */
public class ActiveAgent extends PassiveAgent{

    public ActiveAgent(NetbltAgent S,MessageQueue MQ, InetAddress remoteAddress,int remotePort){
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
        this.MQ = MQ;
        this.S = S;
        this.state = "waitingforresponse";
    }
    public void run(){
        this.state = connectionSetup();
        printConnectionParameters();
        dataTransfer();
    }
    public String connectionSetup(){
        Debug.mark(S.name+":ACTIVE::beginning connection setup");
        remoteUID = S.getUID();
        OpenAndResponse Op = new OpenAndResponse(   MessageType.OPEN,
                                                    remoteUID,
                                                    S.maxBufferSize,
                                                    S.maxDataPacketSize,
                                                    S.burstRate,
                                                    S.burstSize,
                                                    S.deathTimerValue);
        int i = maxConnectionTries;
        OpenAndResponse R = null;
        while(i-- > 0){
            S.sendMessage(remoteAddress, remotePort, Op);
            Message Resp = MQ.getMessage(ConnectionTimeout);
            if(Resp != null && Resp.getType() == MessageType.RESPONSE){
                Debug.mark(S.name + ":ACTIVE::RESPONSE recieved");
                R = (OpenAndResponse)Resp;
                break;
            }
        }
        if(R != null){
            this.remoteBufferSize       = R.bufferSize;
            this.remoteBurstRate        = R.burstRate;
            this.remoteBurstSize        = R.burstSize;
            this.remoteDataPacketSize   = R.dataPacketSize;
            this.remoteDeathTimerValue  = R.deathTimerValue;
            i = maxConnectionTries;
            while(i-- > 0){
                Message Go = MQ.getMessage(remoteDeathTimerValue);
                if (Go != null && Go.getType() == MessageType.GO){
                    Debug.mark(S.name+":ACTIVE::GO recieved : connection successfull");
                    aliveTimer = new KeepAliveTimer(remoteDeathTimerValue/4.0);
                    aliveTimer.start();
                    return "connected";
                }
                Debug.mark(S.name+"Got message that wasn't a go");
            }
            Debug.mark("ACTIVE::Could not connect : GO message tryout");
            this.closeConnection();
            return "closed";
        }else{
            Debug.mark(S.name +"ACTIVE::Could not connect : Response tryout");
            this.closeConnection();
            return "closed";
        }
    }

    public String dataTransfer(){
        while(true){
            Debug.mark(S.name+"ACTIVE:: Beginning data transfer");
            Message M = MQ.getMessage(remoteDeathTimerValue);
            if(M == null){
                Debug.mark(S.name+"ACTIVE:: deathtimer expired : closing connection");
                closeConnection();
            }
            return "closed";
        }
    }
    public synchronized void sendBuffer(Buffer B){
        this.B = B;
    }
}
