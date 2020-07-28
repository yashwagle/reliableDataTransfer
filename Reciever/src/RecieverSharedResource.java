import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TreeMap;

public class RecieverSharedResource {
    /**
     *Shared Resource Singleton Class
     *A treemap to store the received packets with their sequence number as the key
     * Once all packets upto a particular sequence number have been received
     * they are written to the file and the cumulative acknowledgement is sent to the
     * sender.
     * A packet received out of order will have the extended positive acknowledgements
     * stored and an acknowledgement with the cumulative positive acknowledgements and extended acknowledgement
     * will be sent to the sender
     * */
    TreeMap<Integer,DataPacket> recievedPackets = new TreeMap<>();
    AcknowledgementSender ackSender;
    static RecieverSharedResource rs = new RecieverSharedResource();
    boolean FIN;
    Timer timeout;
    int sequenceAcked;
    int lastPrinted;
    String sourceIP;
    String destinationIP = null;

    public String getSourceIP() {
        return sourceIP;
    }

    public void setSourceIP(String sourceIP) {
        this.sourceIP = sourceIP;
    }




    public String getDestinationIP() {
        return destinationIP;
    }

    public void setDestinationIP(String destinationIP) {
        this.destinationIP = destinationIP;
    }


    private RecieverSharedResource(){
        timeout = new Timer();
    }

    static RecieverSharedResource getInstance(){
        return rs;
    }

    public AcknowledgementSender getAckSender() {
        return ackSender;
    }

    public void setAckSender(AcknowledgementSender ackSender) {
        this.ackSender = ackSender;
    }

    public FWriter getfWriter() {
        return fWriter;
    }

    public void setfWriter(FWriter fWriter) {
        this.fWriter = fWriter;
    }

    FWriter fWriter;





    public synchronized void insertData(DataPacket p){
        /**
         * If SYN packet then no data just acknowledge
         */
        if(p.isSYN()){
            try {
                sequenceAcked = p.getSequenceNumber();
                lastPrinted = sequenceAcked;
                ackSender.sendSYNResponse();
            } catch (IOException e) {
                System.out.println("Error when sending SYN response");
                e.printStackTrace();
            }
        }
        //set FIN
        else {
            if(p.isFIN()){
                FIN = true;
            }
            if(recievedPackets.size()==0) {
                timeout.cancel();
                timeout = new Timer();
                timeout.schedule(new RecieverTimer(),500);
            }
            /**
             * The sequence number of the acknowledged packet is greater than
             * the cumulative ack which means we have not acked this packet
             * so we need to process this packet
             */

            if(p.getSequenceNumber()>=sequenceAcked)
                recievedPackets.put(p.getSequenceNumber(), p);
            /**
             * The sequence number of the acknowledged packet is less than the cumulative
             * ack which means the packet was already acked and the acknoledgement was
             * probably lost hence the reciever is resending the packet
             */
            else {
                AcknowledgementPacket ack= new AcknowledgementPacket(sourceIP,destinationIP);
                ack.setCumulativeAck(sequenceAcked);
                try {
                    ackSender.send(ack);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //function that will set send the acknowledgements
    public synchronized void sendAcks(){
        AcknowledgementPacket ack= new AcknowledgementPacket(sourceIP,destinationIP);
        ArrayList<DataPacket> dataPackets = new ArrayList<>();
        if(recievedPackets.size()>0) {
            int start = recievedPackets.firstKey();
            int last = recievedPackets.lastKey();
            int i, cumulativeAck = start;
            boolean cumulativeReached = false;
            for (i = start ; i <= last; i++) {
                if (recievedPackets.containsKey(i) && cumulativeReached == false) {
                    cumulativeAck++;
                    dataPackets.add(recievedPackets.get(i));
                }
                else{
                    cumulativeReached =true;
                    if (recievedPackets.containsKey(i)) {

                    ack.insertpPositiveAcks(i);
                }
                }
                if(recievedPackets.containsKey(i) && lastPrinted+1==i){
                    try {
                        fWriter.writeFile(recievedPackets.get(i));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    lastPrinted++;
                }
            }
            sequenceAcked = lastPrinted;
            ack.setCumulativeAck(sequenceAcked);
            if (FIN)
                ack.setFIN(true);
            try {
                ackSender.send(ack);
                System.out.println("Acknowledgement Sent is "+ack);
            } catch (IOException e) {
                System.out.println("Exception while sending acknowledgement");
                e.printStackTrace();
            }
            removePackets(start, lastPrinted-1);
            if(!FIN) {
                timeout.cancel();
                timeout = new Timer();
                timeout.schedule(new RecieverTimer(), 500);
            }
            if(FIN){
                timeout.cancel();
                System.out.println("Timer finished");
            }
        }
    }

    public synchronized void removePackets(int start,int cumulativeEnd){
        int i;
        for(i=start;i<=cumulativeEnd;i++)
            recievedPackets.remove(i);
    }
}
