
package application;

import java.net.InetAddress;
import message.*;

/**
 *
 * @author fred
 */
public class Buffer {
    int maxSize; /*Maximum amount of bytes that can be stored in the buffer*/
    int realSize; /*real amount of bytes stored in the buffer*/
    int packetSize; /*max size of the packets*/
    int bufferNumber;
    int packetCount;
    int realPacketCount = 0;
    byte[][] data;
    boolean lastBuffer = false;
    boolean[] recieved;
    int lastSentPacket = -1;
    int recievedIndex = -1;
    InetAddress originAddress;
    int originPort;
    public Buffer(int bufferNumber,int maxSize,int packetSize){
        this.packetCount = maxSize / packetSize;
        if (packetCount * packetSize < maxSize){ packetCount++; }
        this.data       = new byte[packetCount][];
        this.maxSize    = maxSize;
        this.packetSize = packetSize;
        this.realSize   = 0;
        this.bufferNumber = bufferNumber;
        recieved = new boolean[packetCount];
        for(boolean b : recieved){
            b = false;
        }
    };
    public Buffer(int bufferNumber,int maxSize,int packetSize, byte[] data){
        this.packetCount = maxSize / packetSize;
        if (packetCount * packetSize < maxSize){ packetCount++; }
        this.data       = new byte[packetCount][];
        this.maxSize    = maxSize;
        this.packetSize = packetSize;
        this.bufferNumber = bufferNumber;
        Debug.error(maxSize < data.length,"Too much data in buffer");
        int i = 0;
        realPacketCount = 0;
        while(i < packetCount){
            realPacketCount++;
            int j = 0;
            int pcksz = packetSize;
            if (i*packetSize + pcksz > data.length){
                pcksz = data.length - i*packetSize;
            }
            if (pcksz <= 0){
                realPacketCount--;
                break;
            }
            this.data[i] = new byte[pcksz];
            while(j < pcksz){
                this.data[i][j] = data[i*packetSize + j];
                j++;
            }
            i++;
        }
    }
    public void setLastBuffer(){
        lastBuffer = true;
    }
    public void setOriginAddress(InetAddress IP, int port){
        originAddress = IP;
        originPort = port;
    }
    public InetAddress getOriginAddress(){
        return originAddress;
    }
    public int getOriginPort(){
        return originPort;
    }
    public boolean getLastBuffer(){
        return lastBuffer;
    }
    public byte[] getData(int index){
        return this.data[index];
    }
    public Data makeMessage(int index){
        byte type = MessageType.DATA;
        if(lastBuffer || index == packetCount -1 || index == realPacketCount-1){
            type = MessageType.LDATA;
        }
        Data D = new Data(  type,
                            (short)(packetSize + Data.headerLength),
                            bufferNumber,
                            (short)(recievedIndex + 1), // ? ? ?
                            (short)index,
                            lastBuffer,
                            getData(index)          );
        return D;

    }
    public void print(){
        int i = 0;
        System.out.println("BUFFER No"+bufferNumber);
        while(i < data.length){
            System.out.print("Pkt["+i+"]");
            if(data[i] == null){
                System.out.println(" _");
                i++;
                continue;
            }
            System.out.print('\t');
            int j = 0;
            while(j < data[i].length){
                System.out.print((char)data[i][j]);
                j++;
            }
            System.out.println("");
            i++;
        }
    }
    public static Buffer[] makeBuffer(int maxSize,int packetSize,byte[] data){
        Buffer[] bufferList = null;
        int bufferCount     = data.length / maxSize;
        if (bufferCount*maxSize < data.length){bufferCount++;}
        bufferList = new Buffer[bufferCount];
        int i = 0;
        while ( i < bufferCount){
            int buffLen = maxSize;
            if (i*maxSize + buffLen > data.length){
                buffLen = data.length - i*maxSize;
                Debug.error(buffLen <= 0,"The impossible just happened");
            }
            byte[] buff = new byte[buffLen];
            int j = 0;
            while (j < buffLen){
                buff[j] = data[i*maxSize + j];
                j++;
            }
            bufferList[i] = new Buffer(i,maxSize,packetSize,buff);
            i++;
        }
        return bufferList;
    }
    public static void main(String[] args){
        /*testing the class*/
        String Message  = TestData.SmallText;
        byte[] Data     = Message.getBytes();
        System.out.println(Data.length);
        Debug.showError();
        Buffer[] testBuffers = makeBuffer(30,10,Data);
        int i = 0;
        while(i < testBuffers.length){
            if(testBuffers[i] != null){
                testBuffers[i].print();
            }else{
                Debug.error("buffer null");
            }
            i++;
        }
    }
}
