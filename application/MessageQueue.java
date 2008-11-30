
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
        timeout = timeout * 1000;
        
        if(timeout == 0){
            Debug.mark("no timeout");
            while(count == 0){
                try{
                    this.wait();
                }catch(InterruptedException e){
                    return null;
                }
            }
        }else{
            Debug.mark("timeout of : "+timeout+" msec");
            long startTime = System.currentTimeMillis();
            while(count == 0){
                long currentTime = System.currentTimeMillis();
                long timeout_time = timeout - (currentTime - startTime);
                if(timeout_time <= 0){
                    Debug.mark("timed out");
                    return null;
                }
                try{
                    
                    Debug.mark("timeout_time: "+timeout_time);
                    this.wait(timeout_time);
                }catch(InterruptedException e){
                    Debug.mark("interrupted");
                    return null;
                }
                Debug.mark("coucou");
            }

        }
        Message FM = queue.remove(first);
        first++;
        count--;
        Debug.mark("returning");
        return FM;
    }
}
