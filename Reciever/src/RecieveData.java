import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class RecieveData extends Thread {

    /**
     * Continuously listening on the port
     * processes the SYN packet and then gives the data packets
     * to ReceiverSharedResource
     * Once the FIN packet is received sets the FIN flag
     *
     * */
    int port;
    int destinationPort=-1;
    DatagramSocket socket;
    RecieverSharedResource rs = RecieverSharedResource.getInstance();
    String sourceIP;
    private boolean SYNSet=false;
    public RecieveData(String sourceIP, int port) throws SocketException {
        this.port = port;
        this.sourceIP = sourceIP;
        socket = new DatagramSocket(port);
    }

    public RecieveData(String sourceIP, int port, int destinationPort) throws SocketException {
        this.port = port;
        this.sourceIP = sourceIP;
        this.destinationPort = destinationPort;
        socket = new DatagramSocket(port);
    }

    private void processSYN(DataPacket dataPacket){
        String filename = new String(dataPacket.getData());
        System.out.println(dataPacket.sourceIP);
        RecieverSharedResource rs = RecieverSharedResource.getInstance();
        if(this.destinationPort==-1){
            this.destinationPort = port;
        }

        rs.setDestinationIP(dataPacket.sourceIP);
        FWriter fWriter = null;
        System.out.println(dataPacket.getDataLength());
        System.out.println(filename);
        try {
            fWriter = new FWriter(filename);
            SYNSet = true;
        } catch (FileNotFoundException e) {
            System.out.println("Error in creating file");
            e.printStackTrace();
        }
        AcknowledgementSender ackSender = new AcknowledgementSender(destinationPort, sourceIP, rs.destinationIP);
        rs.setAckSender(ackSender);
        rs.setfWriter(fWriter);

    }

    @Override
    public void run() {
        byte [] buff = new byte[21000];
        byte []data;
        DatagramPacket packet;
        boolean fin=false;
        while (fin==false){
            packet=new DatagramPacket(buff,buff.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                System.out.println("Exception in receiving acknowledgements");
                e.printStackTrace();
            }

            data =packet.getData();
            DataPacket dp=new DataPacket(data);
            if(dp.isSYN())
                processSYN(dp);
            /*ByteArrayInputStream bis = new ByteArrayInputStream(data);
            try {

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Exception in getting object from Data");
                e.printStackTrace();
            }*/
            if(SYNSet){
            dp.unmarshal();
            fin = dp.isFIN();

            rs.insertData(dp);
            }

        }
    }
}
