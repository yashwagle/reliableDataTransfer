import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class AcknowledgementSender {
    /**
     * Sends acknowledgement packets to the sender
     * on the given port
     *
     * */
    int port;
    String sourceIP,destinationIP;
    public AcknowledgementSender( int port, String sourceIP, String destinationIP){
        this.port = port;
        this.sourceIP = sourceIP;
        this.destinationIP =destinationIP;

    }

    public void send(AcknowledgementPacket ackPacket) throws IOException {
        // Creating the byte array
        //ByteArrayOutputStream packetSenderByteArrayStream = new ByteArrayOutputStream();
        //ObjectOutputStream packetSenderObjectStream =new ObjectOutputStream(packetSenderByteArrayStream);
        //System.out.println(ackPacket);
        //packetSenderObjectStream.writeObject(ackPacket);
        //byte data[] = packetSenderByteArrayStream.toByteArray();
        byte data[] = ackPacket.marshall();
        //Sending the data
        DatagramSocket datagramSocket = new DatagramSocket();
        InetAddress IP = InetAddress.getByName(destinationIP);
        DatagramPacket datagramPacket = new DatagramPacket(data,data.length,IP,port);
        datagramSocket.send(datagramPacket);
       // packetSenderObjectStream.close();
       // packetSenderByteArrayStream.close();
    }

    public void sendSYNResponse() throws IOException {
        AcknowledgementPacket p = new AcknowledgementPacket(sourceIP,destinationIP);
        p.setSYN(true);
        send(p);
    }
}
