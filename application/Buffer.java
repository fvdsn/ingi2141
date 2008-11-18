
package application;

/**
 *
 * @author fred
 */
public class Buffer {
    int maxSize; /*Maximum amount of bytes that can be stored in the buffer*/
    int realSize; /*real amount of bytes stored in the buffer*/
    int packetSize; /*max size of the packets*/
    int bufferNumber;
    byte[][] data;
    public Buffer(int bufferNumber,int maxSize,int packetSize){
        int packetCount = maxSize / packetSize;
        if (packetCount * packetSize < maxSize){ packetCount++; }
        this.data       = new byte[packetCount][];
        this.maxSize    = maxSize;
        this.packetSize = packetSize;
        this.realSize   = 0;
        this.bufferNumber = bufferNumber;
    };
    public Buffer(int bufferNumber,int maxSize,int packetSize, byte[] data){
        int packetCount = maxSize / packetSize;
        if (packetCount * packetSize < maxSize){ packetCount++; }
        this.data       = new byte[packetCount][];
        this.maxSize    = maxSize;
        this.packetSize = packetSize;
        this.bufferNumber = bufferNumber;
        Debug.error(maxSize < data.length,"Too much data in buffer");
        int i = 0;
        while(i < packetCount){
            int j = 0;
            int pcksz = packetSize;
            if (i*packetSize + pcksz > data.length){
                pcksz = data.length - i*packetSize;
            }
            if (pcksz <= 0){
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
    public byte[] getData(int index){
        return this.data[index];
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
