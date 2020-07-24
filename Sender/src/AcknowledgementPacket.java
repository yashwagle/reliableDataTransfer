import java.io.Serializable;
import java.util.ArrayList;

public class AcknowledgementPacket implements Serializable {

    boolean SYN,FIN;
    byte marshalledData[];
    String sourceIP;
    String destinationIP;
    int postiveAckLen;
    int cumulativeAck;
    ArrayList<Integer> positiveAcks=new ArrayList<>();


    public AcknowledgementPacket(String sourceIP, String destinationIP){
        this.sourceIP = sourceIP;
        this.destinationIP = destinationIP;
    }

    public AcknowledgementPacket(byte[] marshalledData){
        this.marshalledData = marshalledData;
    }

    public boolean isSYN() {
        return SYN;
    }

    public void setSYN(boolean SYN) {
        this.SYN = SYN;
    }

    public boolean isFIN() {
        return FIN;
    }

    public void setFIN(boolean FIN) {
        this.FIN = FIN;
    }



    public int getCumulativeAck() {
        return cumulativeAck;
    }

    public void setCumulativeAck(int cumulativeAck) {
        this.cumulativeAck = cumulativeAck;
    }


    public ArrayList<Integer> getPositiveAcks() {
        return positiveAcks;
    }

    public void insertpPositiveAcks(int negative) {
        this.positiveAcks.add(negative);
    }


    @Override
    public String toString() {
        return "cumulative Acks="+cumulativeAck+"      positive Acks="+positiveAcks+"FIN="+FIN+"\n";
    }

    public byte[] IPAddressToByte(String IP){
        System.out.println(IP);
        String[] arr = IP.split("\\.");
        byte[] IPAddressBytes = new byte[4];
        System.out.println(arr[0]+" "+arr[1]+" "+arr[2]+" "+arr[3]);
        IPAddressBytes[0] = (byte) ((byte) Integer.parseInt(arr[0]) & 0xFF);
        IPAddressBytes[1] = (byte) ((byte) Integer.parseInt(arr[1]) & 0xFF);
        IPAddressBytes[2] = (byte) ((byte) Integer.parseInt(arr[2]) & 0xFF);
        IPAddressBytes[3] = (byte) ((byte) Integer.parseInt(arr[3]) & 0xFF);
        return IPAddressBytes;
    }


    public String byteIPAddressToString(byte[] IP){
        String IPAddress="";
        IPAddress = IPAddress +"."+ (IP[0] & 0xFF);
        IPAddress = IPAddress + "."+ (IP[1] & 0xFF);
        IPAddress = IPAddress + "."+(IP[2] & 0xFF);
        IPAddress = IPAddress + "."+(IP[3] & 0xFF);
        return IPAddress.substring(1);
    }

    private byte[] convertIntToByte(int num){
        byte numByteArray[] = new byte[4];
        numByteArray[0] = (byte) ((num>>24) & 0xFF);
        numByteArray[1] = (byte) ((num>>16) & 0xFF);
        numByteArray[2] = (byte) ((num>>8) & 0xFF);
        numByteArray[3] = (byte) ((num>>0) & 0xFF);
        return numByteArray;
    }


    private int convertByteToInt(byte[] numArray){
        int num;
        num = numArray[0] & 0xFF;
        num = num<<8;
        num = num+(numArray[1] & 0xFF);
        num = num<<8;
        num = num+(numArray[2] & 0xFF);
        num = num<<8;
        num = num+(numArray[3] & 0xFF);
        return num;
    }


    public byte[] marshall(){
        //Todo
        byte flags = 1;
        flags= (byte) (flags<<1);
        flags = (byte) (flags+(SYN?1:0));
        flags= (byte) (flags<<1);
        flags = (byte) (flags+(FIN?1:0));
        byte cumulativeAckArr[] = convertIntToByte(cumulativeAck);
        byte sourceIPArray[] = IPAddressToByte(sourceIP);
        byte destinationIPArray[] = IPAddressToByte(destinationIP);
        postiveAckLen = positiveAcks.size();
        byte postiveAckLenByte[] = convertIntToByte(postiveAckLen);
        marshalledData = new byte[17+postiveAckLen*4];
        byte data[];
        marshalledData[0]=flags;
        copyMarshall(cumulativeAckArr,marshalledData,1,4);
        copyMarshall(postiveAckLenByte,marshalledData,5,8);
        copyMarshall(sourceIPArray,marshalledData,9,12);
        copyMarshall(destinationIPArray,marshalledData,13,16);
        int j =17,k=0;
        for(int i=0;i<postiveAckLen;i++){
            data = convertIntToByte(positiveAcks.get(i));
            for(k=0;k<4;k++){
                marshalledData[j++]=data[k];
            }
        }
        return marshalledData;
    }

    public void copyMarshall(byte[] orignal, byte[] copy, int startpos, int endpos){
        int j=0;
        for(int i=startpos;i<=endpos;i++){
            copy[i]=orignal[j++];
        }
    }

    public void copy(byte[] orignal, byte[] copy, int startpos){
        int j=0;
        for(int i=startpos;i<startpos+copy.length;i++){
            copy[j++]=orignal[i];
        }
    }

    public void unmarshall(){
        //Todo
        FIN = (marshalledData[0] & 0x01) != 0;
        byte b = (byte) (marshalledData[0]>>1);
        SYN = (b & 0x01) != 0;
        byte d[] = new byte[4];
        copy(marshalledData,d,1);
        cumulativeAck = convertByteToInt(d);
        copy(marshalledData,d,5);
        postiveAckLen = convertByteToInt(d);
        copy(marshalledData,d,9);
        sourceIP = byteIPAddressToString(d);
        copy(marshalledData,d,13);
        destinationIP = byteIPAddressToString(d);
        int k=17;
        for(int i=0;i<postiveAckLen;i++){
            copy(marshalledData,d,k);
            positiveAcks.add(convertByteToInt(d));
            k = k +4;
        }
    }


}
