public class codeTester {

    private static byte[] convertIntToByte(int num){
        byte numByteArray[] = new byte[4];
        numByteArray[0] = (byte) ((num>>24) & 0xFF);
        numByteArray[1] = (byte) ((num>>16) & 0xFF);
        numByteArray[2] = (byte) ((num>>8) & 0xFF);
        numByteArray[3] = (byte) ((num>>0) & 0xFF);


        return numByteArray;
    }


    private static int convertByteToInt(byte[] numArray){
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

    public static byte[] IPAddressToByte(String IP){
        String[] arr = IP.split("\\.");
        byte[] IPAddressBytes = new byte[4];
        System.out.println(arr[0]+" "+arr[1]+" "+arr[2]+" "+arr[3]);
        IPAddressBytes[0] = (byte) ((byte) Integer.parseInt(arr[0]) & 0xFF);
        IPAddressBytes[1] = (byte) ((byte) Integer.parseInt(arr[1]) & 0xFF);
        IPAddressBytes[2] = (byte) ((byte) Integer.parseInt(arr[2]) & 0xFF);
        IPAddressBytes[3] = (byte) ((byte) Integer.parseInt(arr[3]) & 0xFF);
        return IPAddressBytes;
    }

    public static String byteIPAddressToString(byte[] IP){
        String IPAddress="";
        IPAddress = IPAddress +"."+ (IP[0] & 0xFF);
        IPAddress = IPAddress + "."+ (IP[1] & 0xFF);
        IPAddress = IPAddress + "."+(IP[2] & 0xFF);
        IPAddress = IPAddress + "."+(IP[3] & 0xFF);
        return IPAddress.substring(1);
    }

    public static void main(String[] args) {
       byte a=3;
       boolean syn,fin;
       syn = (a & 0x01)==0?false:true;
       a= (byte) (a>>1);
       fin = (a & 0x01)==0?false:true;
        System.out.println(syn+" "+fin);
    }
}
