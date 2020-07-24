import java.io.Serializable;

public class DataPacket implements Serializable {
    private byte[] data;
    private int  sequenceNumber;
    private byte[] marshalledData;

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    private int dataLength;
    String sourceIP;
    String destinationIP;

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

    private boolean SYN,FIN;

    public DataPacket(byte[] data, int sequenceNumber, int dataLength, String sourceIP, String destinationIP){
        this.sourceIP=sourceIP;
        this.destinationIP = destinationIP;
        this.dataLength = dataLength;
        this.data = data;
        if(sequenceNumber>=0)
        this.sequenceNumber = sequenceNumber;
        else this.sequenceNumber=0;
    }

    public DataPacket(int sequenceNumber, String sourceIP, String destinationIP){
        this.sourceIP=sourceIP;
        this.destinationIP = destinationIP;
        if(sequenceNumber>=0)
            this.sequenceNumber = sequenceNumber;
        else this.sequenceNumber=0;
    }



    public DataPacket(byte[] marshalled){
        this.marshalledData = marshalled;
        unmarshal();
    }


    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    @Override
    public String toString() {
        return "Sequence Number = "+sequenceNumber+" FIN = "+FIN;
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


    public byte[] IPAddressToByte(String IP){
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

    public byte[] marshal(){
        //Todo
        byte flags = (byte) (SYN?1:0);
        flags = (byte) (flags << 1);
        flags = (byte) (flags + (FIN?1:0));
        byte[] sequenceNumberByte = convertIntToByte(sequenceNumber);
        byte[] dataLengthByte = convertIntToByte(dataLength);
        byte[] sourceIPByte = IPAddressToByte(sourceIP);
        byte[] destinationIPByte = IPAddressToByte(destinationIP);
        marshalledData = new byte[17+dataLength];
        marshalledData[0] =flags;
        marshalledData[1] = sequenceNumberByte[0];
        marshalledData[2] = sequenceNumberByte[1];
        marshalledData[3] = sequenceNumberByte[2];
        marshalledData[4] = sequenceNumberByte[3];
        marshalledData[5] = dataLengthByte[0];
        marshalledData[6] = dataLengthByte[1];
        marshalledData[7] = dataLengthByte[2];
        marshalledData[8] = dataLengthByte[3];
        marshalledData[9] = sourceIPByte[0];
        marshalledData[10] = sourceIPByte[1];
        marshalledData[11] = sourceIPByte[2];
        marshalledData[12] = sourceIPByte[3];
        marshalledData[13] = destinationIPByte[0];
        marshalledData[14] = destinationIPByte[1];
        marshalledData[15] = destinationIPByte[2];
        marshalledData[16] = destinationIPByte[3];
        if(SYN==false) {
            int j = 0;
            for (int i = 17; i < dataLength + 17; i++) {
                marshalledData[i] = data[j++];
            }
        }
        return marshalledData;

    }

    public void copy(byte[] orignal, byte[] copy, int startpos){
        int j=0;
        for(int i=startpos;i<startpos+copy.length;i++){
            copy[j++]=orignal[i];
        }
    }

    public void unmarshal(){
        FIN = (marshalledData[0] & 0x01) != 0;
        byte b = (byte) (marshalledData[0]>>1);
        SYN = (b & 0x01) != 0;
        byte temparr[] = new byte[4];
        copy(marshalledData,temparr,1);
        sequenceNumber=convertByteToInt(temparr);
        copy(marshalledData,temparr,5);
        dataLength = convertByteToInt(temparr);
        copy(marshalledData,temparr,8);
        sourceIP = byteIPAddressToString(temparr);
        copy(marshalledData,temparr,13);
        destinationIP = byteIPAddressToString(temparr);
        if(dataLength!=-1) {
            data = new byte[dataLength];
            copy(marshalledData, data, 17);
        }
    }

}
