import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
/**
 * File writer class that will write the data from
 * a given data packet to the file
 *
 *
 * */
public class FWriter {
    String filename;
    FileOutputStream fileOutputStream;
    /**
     * Initialize with the filename specified in the SYN packet
     *
     * */
    public FWriter(String filename) throws FileNotFoundException {
        this.filename = filename;
        setFileStream();
    }

    public void setFileStream() throws FileNotFoundException {
        fileOutputStream = new FileOutputStream(new File(filename),true);
    }

    public void writeFile(DataPacket dp) throws IOException {
        System.out.println("Packet written "+dp.getSequenceNumber());
        if(dp.getData()!=null && dp.getData().length==dp.getDataLength())
            fileOutputStream.write(dp.getData());
        else {
            System.out.println("Coming here");
            for(int i=0;i<dp.getDataLength();i++)
                fileOutputStream.write(dp.getData()[i]);
        }
    }
    public void close() throws IOException {
        fileOutputStream.close();
    }

    public void writeFile(ArrayList<DataPacket> dp) throws IOException {
        Iterator<DataPacket> idp =dp.iterator();
        DataPacket d;
        while (idp.hasNext()){
            d = idp.next();
            System.out.println("Packets printed are "+d.getSequenceNumber());
            fileOutputStream.write(d.getData());
        }
    }
}
