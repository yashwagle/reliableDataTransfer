import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class DataSender {
    /**
     * Sends the given data packet to the given port and host
     *
     *
     * */
    String host;
    int port;
    DatagramSocket datagramSocket;
    public DataSender(String host, int port){
        this.host = host;
        this.port = port;
        try {
            datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    public void send(DataPacket dataPacket) throws IOException {
        // Creating the byte array
       // ByteArrayOutputStream packetSenderByteArrayStream = new ByteArrayOutputStream();
       // ObjectOutputStream packetSenderObjectStream =new ObjectOutputStream(packetSenderByteArrayStream);
       // packetSenderObjectStream.writeObject(dataPacket);

         byte data[]= dataPacket.marshal();
        //Sending the data
        InetAddress IP = InetAddress.getByName(host);
        DatagramPacket datagramPacket = new DatagramPacket(data,data.length,IP,port);
        datagramSocket.send(datagramPacket);
    }





}
