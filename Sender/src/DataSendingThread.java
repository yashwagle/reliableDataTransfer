public class DataSendingThread extends Thread{
    volatile SharedResource sr = SharedResource.getInstance();
    int maxUnackedPackets;
    int windowsize;
    DataSender dataSender;

    DataSendingThread(int maxUnackedPackets,int windowsize, DataSender ds){
        this.maxUnackedPackets =maxUnackedPackets;
        this.windowsize = windowsize;
        this.dataSender = ds;
    }

    public int getWindowsize() {
        return windowsize;
    }

    public void setWindowsize(int windowsize) {
        this.windowsize = windowsize;
    }



    public DataSendingThread(int maxUnackedPackets){
        this.maxUnackedPackets = maxUnackedPackets;
    }

    public DataSender getDataSender() {
        return dataSender;
    }

    public void setDataSender(DataSender dataSender) {
        this.dataSender = dataSender;
    }



    @Override
    public void run() {
        while (!sr.isFIN()){
          //  System.out.println("a");
        //sr.geFirstUnacknowldgedQueue()+windowsize<sr.getFirstSendingQueue()
           // System.out.println("Sender queue size="+sr.getSenderQueueSize()+" unacknowledgedqueue size="+sr.getUnacknowledgedQueueSize()+" maxunacked packet="+maxUnackedPackets);
            if(sr.getSenderQueueSize()>0 && sr.getUnacknowledgedQueueSize()<maxUnackedPackets) {
                sr.sendPacket();
                sr.resetTimer();
            }

        }
    }
}
