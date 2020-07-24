import java.util.TimerTask;

public class RecieverTimer extends TimerTask {

    RecieverSharedResource rs = RecieverSharedResource.getInstance();

    @Override
    public void run() {
        rs.sendAcks();
    }
}
