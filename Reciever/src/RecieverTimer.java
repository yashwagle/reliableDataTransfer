import java.util.TimerTask;

public class RecieverTimer extends TimerTask {
    /**
    *
     * Timer task for the receiver.
     * Once the receiver times out an acknowledgement packet with cumulative acknowledgements
     * and extended acknowledgements
     *
     * */
    RecieverSharedResource rs = RecieverSharedResource.getInstance();

    @Override
    public void run() {
        rs.sendAcks();
    }
}
