import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class AcknowledgementHandler extends Thread {
    int port;
    DatagramSocket socket;
    SharedResource sr;



    setUpConnection stt;
    public boolean isSYN() {
        return SYN;
    }

    public void setSYN(boolean SYN) {
        this.SYN = SYN;
    }

    boolean SYN;
    public AcknowledgementHandler(int port) throws SocketException {
    this.port = port;
    socket = new DatagramSocket(port);
    sr = SharedResource.getInstance();
    }

    @Override
    public void run() {
        byte [] buff = new byte[500];
        byte []data;
        DatagramPacket packet;
        boolean FIN=false;
        while (FIN==false) {
            System.out.println("b");
            packet = new DatagramPacket(buff, buff.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                System.out.println("Exception in receiving acknowledgements");
                e.printStackTrace();
            }
            data = packet.getData();


            AcknowledgementPacket ackp = new AcknowledgementPacket(data);
            ackp.unmarshall();
            if (ackp.isSYN() == false)
                sr.acknowledgeDataPacket(ackp);
            System.out.println("cumulative ck="+ackp.cumulativeAck);
            FIN = ackp.isFIN();
            System.out.println(FIN);
            sr.setFIN(FIN);

        }
    }


}
