import java.io.FileNotFoundException;
import java.net.SocketException;

public class SenderMain {




    public static void main(String[] args) {
        String IP=args[0];
        int Sendingport= Integer.parseInt(args[2]);
        int recievingPort = Integer.parseInt(args[1]);
        String sourceIP="127.0.0.1",destinationIP="127.0.0.1";
        String Filepath=args[3];
        int dataByteLength=20000;
        int maxUnSentData= 30;
        int initialSequenceNumber = 200;
        int maxUnackedPacket = 10;
        int windowSize = 20;
        DataSender ds = new DataSender(IP,Sendingport);
        try {
            setUpConnection st = new setUpConnection(ds,initialSequenceNumber,recievingPort,sourceIP,destinationIP);
            st.start();
            st.join();
        } catch (SocketException | InterruptedException e) {
            e.printStackTrace();
        }
        /////////////////////////
        initialSequenceNumber++;
        SharedResource sr = SharedResource.getInstance();
        sr.setDataSender(ds);
        sr.setLastCumalativeAck(initialSequenceNumber);
        AcknowledgementHandler ackHandler=null;
        try {
             ackHandler = new AcknowledgementHandler(recievingPort);
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
