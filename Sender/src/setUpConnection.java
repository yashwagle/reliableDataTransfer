import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Timer;

public class setUpConnection extends Thread{

    DataSender ds;
    int sequenceNumber;
    Timer timout;
    DatagramSocket datagramSocket;
    String sourceIP,destinationIP;
    String filePath;

    setUpConnection(DataSender ds, int sequenceNumber, int port, String sourceIP, String destinationIP, String filePath) throws SocketException {
        this.ds = ds;
        this.sequenceNumber = sequenceNumber;
        datagramSocket = new DatagramSocket(port);
        this.sourceIP = sourceIP;
        this.destinationIP = destinationIP;
        this.filePath = filePath;
    }

    public void sendSYN() throws IOException {
        DataPacket dp = new DataPacket(sequenceNumber,sourceIP,destinationIP);
        dp.setSYN(true);
        dp.setData(filePath.getBytes());
        System.out.println(dp.sourceIP);
        try {
            ds.send(dp);
        } catch (IOException e) {
            e.printStackTrace();

        }
        timout = new Timer();
        timout.schedule(new setUpConnectionTimerTask(this),500);
        byte [] synack = new byte[1000];
        DatagramPacket p = new DatagramPacket(synack,synack.length);
        datagramSocket.receive(p);
        byte[] data =p.getData();
        /*ByteArrayInputStream bis = new ByteArrayInputStream(data);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            AcknowledgementPacket ackp = (AcknowledgementPacket) ois.readObject();
            if(ackp.isSYN()==true)
                System.out.println("SYNAcked");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/
        AcknowledgementPacket ackp = new AcknowledgementPacket(data);
        ackp.unmarshall();
        System.out.println(ackp.isSYN());
        if(ackp.isSYN())
            System.out.println("SYNacked");
        datagramSocket.close();
        timout.cancel();
    }

    public void sendSYNAgian(){
        DataPacket dp = new DataPacket(sequenceNumber,sourceIP,destinationIP);
        dp.setSYN(true);
        dp.setSequenceNumber(sequenceNumber);
        try {
            ds.send(dp);
        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    @Override
    public void run() {
        try {
            sendSYN();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void resetTimer(){
        timout.cancel();
        timout = new Timer();
        timout.schedule(new setUpConnectionTimerTask(this),500);
    }
}
