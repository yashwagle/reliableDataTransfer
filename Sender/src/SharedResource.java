import java.io.IOException;
import java.util.*;

public class SharedResource {

    private volatile TreeMap<Integer,DataPacket> sendingQueue;
    private volatile TreeMap<Integer, DataPacket> unacknowledgedQueue;
    DataSender dataSender;
    private Timer timeout;
    private static SharedResource sharedResource = new SharedResource();
    Object timerLockObject = new Object();

    public boolean isFIN() {
        return FIN;
    }

    public void setFIN(boolean FIN) {
        this.FIN = FIN;
        if(FIN) {
            timeout.cancel();
            System.out.println("^^^");
        }
    }

    boolean FIN;
    public void setLastCumalativeAck(int lastCumalativeAck) {
        this.lastCumalativeAck = lastCumalativeAck;
    }

    private int lastCumalativeAck;

    public void setDataSender(DataSender dataSender) {
        this.dataSender = dataSender;
    }

    public int getSenderQueueSize(){
        return sendingQueue.size();

    }

    public int getUnacknowledgedQueueSize(){
        return unacknowledgedQueue.size();
    }



    private SharedResource(){
        sendingQueue = new TreeMap<>();
        unacknowledgedQueue = new TreeMap<>();
        timeout = new Timer();
    }

     public static SharedResource getInstance() {
        return sharedResource;
    }

    public void addPacketToSendingQueue(DataPacket packet){
        System.out.println("Added packet");
          sendingQueue.put(packet.getSequenceNumber(),packet);
    }

    public int getFirstSendingQueue(){
        return sendingQueue.firstKey();
    }

    public int getFirstUnacknowldgedQueue(){
        return sendingQueue.firstKey();
    }

    public synchronized void  sendPacket(){
        int sequence = sendingQueue.firstKey();
        DataPacket p = sendingQueue.get(sequence);
        try {
            dataSender.send(p);
            System.out.println("Packet sent is "+p);
        }catch (IOException ie){
            System.out.println("Failed to send packet "+p+" Error"+ie.getStackTrace());
        }
        sendingQueue.remove(sequence);
        unacknowledgedQueue.put(sequence,p);
    }

    public synchronized void acknowledgeDataPacket(AcknowledgementPacket ack){
        int i,sequenceNumber;
        DataPacket p;
        for(i=lastCumalativeAck;i<=ack.cumulativeAck;i++) {
            if (unacknowledgedQueue.containsKey(i))
                unacknowledgedQueue.remove(i);
        }
        lastCumalativeAck=ack.cumulativeAck;
        Iterator<Integer> ackIterator;
            if(ack.positiveAcks!=null) {
                ackIterator = ack.positiveAcks.iterator();
                while (ackIterator.hasNext()) {
                    sequenceNumber = ackIterator.next();
                    if(unacknowledgedQueue.containsKey(sequenceNumber))
                        unacknowledgedQueue.remove(sequenceNumber);
                }
            }
            sendUnacknowledgedPackets();
        System.out.println("Acknowledgement processesd is "+ack);
        System.out.println(unacknowledgedQueue.size());
        resetTimer();
    }

    public synchronized void sendUnacknowledgedPackets(){
        Set<Map.Entry<Integer,DataPacket>> unackedSet = unacknowledgedQueue.entrySet();
        Iterator<Map.Entry<Integer,DataPacket>> unackedIter = unackedSet.iterator();
        DataPacket p;
        while (unackedIter.hasNext()){
            p = unackedIter.next().getValue();
            try {
                dataSender.send(p);
            } catch (IOException e) {
                System.out.println("Unable to resend unacknowledged packet"+p);
                e.printStackTrace();
            }
        }
        resetTimer();
    }

    public void resetTimer(){
        synchronized (timerLockObject) {
            timeout.cancel();
            timeout = new Timer();
            timeout.schedule(new SenderTimer(), 10000);
        }
    }



}
