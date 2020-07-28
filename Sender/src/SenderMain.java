import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SenderMain {

    public static String getSenderIP(){
        InetAddress ip = null;
        try {
            ip = InetAddress.getLocalHost();

        }catch (UnknownHostException ue){
            System.out.println(ue);
        }
        return ip.getHostAddress();
    }


    public static void main(String[] args) {
        int port= Integer.parseInt(args[0]);
        String sourceIP=getSenderIP();
        String destinationIP=args[1];
        String Filepath=args[2];
        int dataByteLength=20000;
        int maxUnSentData= 30;
        int initialSequenceNumber = 200;
        int maxUnackedPacket = 10;
        int windowSize = 20;
        DataSender ds = null;
        setUpConnection st = null;
        int sendingPort;
        if(args.length>3) {
            sendingPort = Integer.parseInt(args[3]);
            ds = new DataSender(destinationIP, sendingPort);
        }
        else{
            ds = new DataSender(destinationIP, port);

        }
        try {
            st = new setUpConnection(ds,initialSequenceNumber,port,sourceIP,destinationIP, Filepath);
            st.start();
            st.join();
        } catch (InterruptedException | SocketException e) {
            e.printStackTrace();
        }
        /////////////////////////
        initialSequenceNumber++;
        SharedResource sr = SharedResource.getInstance();
        sr.setDataSender(ds);
        sr.setLastCumalativeAck(initialSequenceNumber);
        AcknowledgementHandler ackHandler=null;
        try {
             ackHandler = new AcknowledgementHandler(port);
        } catch (SocketException e) {
            System.out.println("Error while start ackHandler Thread");
            e.printStackTrace();
        }
        ackHandler.start();


        ReadingFileAddingFileToQueue readingFileAddingFileToQueue = new ReadingFileAddingFileToQueue(sourceIP,destinationIP);
        readingFileAddingFileToQueue.setMaxUnsentData(maxUnSentData);
        try {
            readingFileAddingFileToQueue.setFilepath(Filepath);
        } catch (FileNotFoundException e) {
            System.out.println("Error in file handling");
            e.printStackTrace();
        }
        readingFileAddingFileToQueue.setDatabytelength(dataByteLength);
        readingFileAddingFileToQueue.setSequenceNumber(initialSequenceNumber);
        readingFileAddingFileToQueue.start();
        DataSendingThread dataSendingThread = new DataSendingThread(maxUnackedPacket,windowSize,ds);
        dataSendingThread.start();

    }
}
