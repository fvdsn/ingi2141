
package application;
import java.util.Hashtable;
import message.*;

/**
 *
 * @author fred
 */
public class MessageQueue {
    Hashtable <Integer,Message> queue = new Hashtable();
    int first;
    int last;
    int count = 0;
    public MessageQueue(){
        first = 0;
        last = 0;
    }
    synchronized void addMessage(Message M){
        count++;
        last++;
        queue.put(last, M);
        if(count == 1){
            first = last;
        }
        this.notifyAll();
    }
    synchronized Message getMessage(long timeout){
        if(timeout == 0){
            while(count == 0){
                try{
                    this.wait();
                }catch(InterruptedException e){
                    return null;
                }
            }
        }else{
            long startTime = System.currentTimeMillis();
            while(count == 0){
                long currentTime = System.currentTimeMillis();
                if(currentTime > startTime + timeout){
                    return null;
                }
                try{
                    this.wait(timeout - (currentTime - startTime));
                }catch(InterruptedException e){
                    return null;
                }
            }

        }
        Message FM = queue.remove(first);
        first++;
        count--;
        return FM;
    }
    public static void main(String[] args){
        //test unit for this class
        MessageQueue MQ = new MessageQueue();
        System.out.println("waiting 5 sec");
        MQ.getMessage(5000);
        System.out.println("stop");
        System.out.println("getting message from queue");
        Message M = new Message();
        MQ.addMessage(M);
        System.out.println(MQ.getMessage(0).toString());
        MQ.getMessage(0);
        System.out.println("should never print");
    }
}
