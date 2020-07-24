import java.util.TimerTask;

public class setUpConnectionTimerTask extends TimerTask {
    setUpConnection st;
    setUpConnectionTimerTask(setUpConnection st){
        this.st = st;
    }
    @Override
    public void run() {
        st.sendSYNAgian();
    }
}
