import java.io.FileNotFoundException;
import java.net.SocketException;

public class ReciverMain {
    public static void main(String[] args) {
        String IPAddress=args[0];
        int Recievingport=Integer.parseInt(args[1]);
        int Sendingport=Integer.parseInt(args[2]);
        String sourceIP="127.0.0.1";
        String destinationIP="127.0.0.1";
        String filepath=args[3];
        AcknowledgementSender ackSender = new AcknowledgementSender(IPAddress,Sendingport,"127.0.0.1","127.0.0.1");
        FWriter fWriter = null;
        try {
            fWriter = new FWriter(filepath);
        } catch (FileNotFoundException e) {
            System.out.println("Error in creating file");
            e.printStackTrace();
        }
        RecieverSharedResource rs = RecieverSharedResource.getInstance();
        rs.setSourceIP(sourceIP);
        rs.setDestinationIP(destinationIP);
        rs.setAckSender(ackSender);
        rs.setfWriter(fWriter);
        try {
            RecieveData rsData = new RecieveData(Recievingport);
            rsData.start();
        } catch (SocketException e) {
            System.out.println("Error while setting up port");
            e.printStackTrace();
        }

    }
}

