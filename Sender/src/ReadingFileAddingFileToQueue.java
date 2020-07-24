import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ReadingFileAddingFileToQueue extends Thread {
    String filepath;
    int offset;
    int moreData;
    volatile byte data[];
    FileInputStream fileReader;
    int maxUnsentData=10;
    String sourceIP,destinationIP;

    public ReadingFileAddingFileToQueue(String sourceIP,String destinationIP){
        this.sourceIP = sourceIP;
        this.destinationIP = destinationIP;
    }

    public int getDatabytelength() {
        return databytelength;
    }

    public void setDatabytelength(int databytelength) {
        this.databytelength = databytelength;
        data = new byte[databytelength];
    }

    int databytelength;

    public int getMaxUnsentData() {
        return maxUnsentData;
    }

    public void setMaxUnsentData(int maxUnsentData) {
        this.maxUnsentData = maxUnsentData;
    }

    SharedResource sr = SharedResource.getInstance();
    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) throws FileNotFoundException {
        this.filepath = filepath;
        File f= new File(filepath);
        fileReader = new FileInputStream(f);
    }


    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    int sequenceNumber;

    public int addDataPackets(){
        int n=0;
        try {
            data = new byte[databytelength];
            System.out.println(databytelength);
            n = fileReader.read(data);
            DataPacket p = new DataPacket(data,sequenceNumber,n,sourceIP,destinationIP);
            if(n==-1)
                p.setFIN(true);
            sr.addPacketToSendingQueue(p);
            sequenceNumber++;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return n;
    }

    @Override
    public void run() {
        int n =0;
        while (n!=-1){
            n = addDataPackets();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        }
    }

