/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package message;
import java.io.IOException;
/**
 *
 * @author fred
 */
public class OpenAndResponse extends Message {
    public int connectionUniqueID;
    public int bufferSize;
    public short dataPacketSize;
    public short burstSize;
    public short burstRate;
    public short deathTimerValue;

    public OpenAndResponse(){}
    public OpenAndResponse(byte type, int CUID,int bufferSize, short dataPcktSz,
                short brstRate, short brstSz, short dtv){
        super(type,(short)40);
        this.connectionUniqueID= CUID;
        this.bufferSize = bufferSize;
        this.dataPacketSize = dataPcktSz;
        this.burstRate = brstRate;
        this.burstSize = brstSz;
        this.deathTimerValue = dtv;
    }
    public byte[] toWire() throws IOException{
        headerToWire();
        out.writeInt(connectionUniqueID);
        out.writeInt(bufferSize);
        out.writeShort(dataPacketSize);
        out.writeShort(burstSize);
        out.writeShort(burstRate);
        out.writeShort(deathTimerValue);
        byte [] data = byteStream.toByteArray();
        return data;
    }
    public void fromWire(byte[] input) throws IOException{
        super.fromWire(input);
        this.connectionUniqueID = in.readInt();
        this.bufferSize = in.readInt();
        this.dataPacketSize = in.readShort();
        this.burstSize = in.readShort();
        this.burstRate = in.readShort();
        this.deathTimerValue = in.readShort();

    }
    public String toString(){
        String R = super.toString();
        R += "CUID:..........."+this.connectionUniqueID+"\n";
        R += "bufferSize:....."+this.bufferSize+"\n";
        R += "dataPacketSize:."+this.dataPacketSize+"\n";
        R += "burstRate:......"+this.burstRate+"\n";
        R += "burstSize:......"+this.burstSize+"\n";
        R += "deathTimerBalue:"+this.deathTimerValue+"\n";
        return R;
    }
}
