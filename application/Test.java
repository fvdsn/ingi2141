
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
        NetbltAgent Reciever = new NetbltAgent(6000,5000,(short)250,"Rec  ");
        InetAddress IP = null;
        try{
            IP = InetAddress.getByName("localhost");
        }catch(Exception e){
            e.printStackTrace();
        }
        Reciever.recieve();
        Sender.send(IP,6000,null); 
    }
}
