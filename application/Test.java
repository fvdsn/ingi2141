
package application;

import java.net.*;

/**
 *
 * @author fred
 */
public class Test {
    public static void main(String[] args){
        Debug.showMark();
        Debug.showError();
        Debug.showWarning();
        NetbltAgent Sender = new NetbltAgent(8000,10000,(short)1000,"S   ");
        Sender.setBurstRate(15);
        Sender.setBurstSize(8);
        NetbltAgent Reciever = new NetbltAgent(6000,5542,(short)250,"Rec  ");
        InetAddress IP = null;
        try{
            IP = InetAddress.getByName("localhost");
        }catch(Exception e){
            e.printStackTrace();
        }
        Reciever.recieve(1);
        Debug.mark("COUCOU1");
        Debug.mark("ATTENTIONPLEASE " + Sender.connect(IP,6000));
        Sender.disconnect(IP, 6000);

    }
}
