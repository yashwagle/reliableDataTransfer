import java.util.TimerTask;

public class SenderTimer extends TimerTask {

    SharedResource sr;
    public SenderTimer(){
      sr = SharedResource.getInstance();
    }

    @Override
    public void run() {
        sr.sendUnacknowledgedPackets();
    }
}
