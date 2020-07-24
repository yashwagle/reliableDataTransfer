import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class RecieveData extends Thread {
    int port;
    DatagramSocket socket;
    RecieverSharedResource rs = RecieverSharedResource.getInstance();
    public RecieveData(int port) throws SocketException {
        this.port = port;
        socket = new DatagramSocket(port);
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
            /*ByteArrayInputStream bis = new ByteArrayInputStream(data);
            try {

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Exception in getting object from Data");
                e.printStackTrace();
            }*/

            dp.unmarshal();
            fin = dp.isFIN();

            rs.insertData(dp);

        }
    }
}
