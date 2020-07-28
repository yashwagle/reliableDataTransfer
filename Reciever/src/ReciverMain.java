import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ReciverMain {


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
        String IPAddress=getSenderIP();
        int sourcePort=Integer.parseInt(args[0]);
        RecieverSharedResource rs = RecieverSharedResource.getInstance();
        rs.setSourceIP(IPAddress);
        try {
            if(args.length>1){
                int destinationPort = Integer.parseInt(args[1]);
                RecieveData rsData = new RecieveData(IPAddress, sourcePort, destinationPort);
                rsData.start();
            }
            else {
                RecieveData rsData = new RecieveData(IPAddress, sourcePort);
                rsData.start();
            }
        } catch (SocketException e) {
            System.out.println("Error while setting up port");
            e.printStackTrace();
        }

    }
}

