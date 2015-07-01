package org.hinex.alpha.arsenal.nio.tftp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * TFTP Client Using Java NIO
 * @author http://javapapers.com/java/java-nio-tftp-client/
 */
public class TFTPClientNIO {
    
    private String TFTP_SERVER_ADDRESS = "192.168.1.11";
    private int TFTP_SERVER_PORT = 69;
    
    private Selector selector;
    private List<DatagramChannel> datagramChannels;
    private SocketAddress socketAddress;
    private int fileCount;
    
    private final byte OP_RRQ = 1;
    private final byte OP_DATAPACKET = 3;
    private final byte OP_ACK = 4;
    private final byte OP_ERROR = 5;
    private final int PACKET_SIZE = 1024;
    
    public static void main(String[] args) throws Exception {
        TFTPClientNIO tFTPClientNio = new TFTPClientNIO();
        String[] files = { "demo.txt", "TFTP.pdf" };
        tFTPClientNio.getFiles(files);
    }
    
    public void getFiles(String[] files) throws IOException {
        registerAndRequest(files);
        readFiles();
    }
    
    /*
     * create/register as many channels and send request to read
     */
    private void registerAndRequest(String[] files) throws IOException {
        fileCount = files.length;
        selector = Selector.open();
        socketAddress = new InetSocketAddress(TFTP_SERVER_ADDRESS, TFTP_SERVER_PORT);
        datagramChannels = new ArrayList<DatagramChannel>();
        for (int i = 0; i < fileCount; i++) {
            DatagramChannel dc = DatagramChannel.open();
            dc.configureBlocking(false);
            SelectionKey selectionKey = dc.register(selector, SelectionKey.OP_READ);
            selectionKey.attach(files[i]);
            sendRequest(files[i], dc);
            datagramChannels.add(dc);
        }
    }
    
    /*
     * to send request to TFTP server
     */
    private void sendRequest(String fileName, DatagramChannel dChannel) throws IOException {
        String mode = "octet";
        ByteBuffer rrqByteBuffer = createRequest(OP_RRQ, fileName, mode);
        System.out.println("Sending Request to TFTP Server.");
        dChannel.send(rrqByteBuffer, socketAddress);
        System.out.println("Request Sent Success.");
    }
    
    private void readFiles() throws IOException {
        int counter = 0;
        while (counter < fileCount) {
            counter ++;
            int readyChannels = selector.select();
            if (readyChannels == 0) {
                continue;
            }
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {
                    System.out.println("connection accepted: " + key.channel());
                } else if (key.isConnectable()) {
                    System.out.println("connection established: " + key.channel());
                } else if (key.isReadable()) {
                    System.out.println("Channel Readable: " + key.channel());
                    receiveFile((DatagramChannel) key.channel(), (String) key.attachment());
                    System.out.println("File received.");
                } else if (key.isWritable()) {
                    System.out.println("writable: " + key.channel());
                }
                keyIterator.remove();
            }
        }
    }
    
    /*
     * Receive the file from TFTP Server
     */
    private void receiveFile(DatagramChannel dc, String fileName) throws IOException {
        ByteBuffer dst = null;
        do {
            dst = ByteBuffer.allocateDirect(PACKET_SIZE);
            
            // STEP 2.1: Receive
            // TFTP Server chooses to communicate back on a new PORT
            // so we need this to send acknowledgment back
            SocketAddress remoteSocketAddress = dc.receive(dst);
            
            // System.out.println("Packet Received from TFTP Server: " + remoteSocketAddress);
            
            // STEP 2.2: Read OPCODE
            byte[] opCode = { dst.get(0), dst.get(1) };
            // System.out.println("Got OPCODE from TFTP Server for this Packet: " + opCode[0] + ", " + opCode[1]);
            
            if (opCode[1] == OP_ERROR) {
                System.out.println("Type of PACKET Received is ERROR!");
            } else if (opCode[1] == OP_DATAPACKET) {
                System.out.println("Type of PACKET Recevied is DATA.");
                byte[] packetBlockNumber = { dst.get(2), dst.get(3) };
                System.out.println("Packet Block Number: " + packetBlockNumber[0] + ", " + packetBlockNumber[1]);
                
                // STEP 2.3: Read Packet
                readPacketData(dst, fileName);
                
                // STEP 2.4: Send acknowledgment for packet
                sendAcknowledgment(packetBlockNumber, remoteSocketAddress, dc);
            }
        } while (!isLastPacket(dst));
    }
    
    /*
     * Read content of a PACKET (STEP 2.3)
     */
    private byte[] readPacketData(ByteBuffer dst, String fileName) throws IOException {
        System.out.println("Read contents of this packet..");
        byte fileContent[] = new byte[PACKET_SIZE];
        dst.flip(); // make buffer ready for read
        
        int m = 0, counter = 0;
        while (dst.hasRemaining()) {
            // skipping the first four control bytes
            // first two is OPCODE
            // second two is packet number
            if (counter > 3) {
                fileContent[m] = dst.get();
                m ++;
            } else {
                dst.get();
            }
            counter ++;
        }
        System.out.println("Packet Reading Done.");
        
        Path filePath = Paths.get(fileName);
        
        byte[] toWrite = new byte[m];
        System.arraycopy(fileContent, 0, toWrite, 0, m);
        
        writeToFile(filePath, toWrite);
        
        return fileContent;
    }
    
    private void writeToFile(Path filePath, byte[] toWrite) throws IOException {
        if (Files.exists(filePath)) {
            Files.write(filePath, toWrite, StandardOpenOption.APPEND);
        } else {
            Files.write(filePath, toWrite, StandardOpenOption.CREATE);
        }
    }
    
    /*
     * To check if the its the last packet In TFTP, 
     * last packet will be of size < 512
     */
    private boolean isLastPacket(ByteBuffer bb) {
        if (bb.limit() < 512) {
            return true;
        } else {
            return false;
        }
    }
    
    /*
     * To send acknowledgment back to the TFTP server (STEP 2.4)
     */
    private void sendAcknowledgment(byte[] blockNumber, SocketAddress socketAddress, DatagramChannel dc) throws IOException {
        System.out.println("Sending ACK... " + blockNumber[0] + ", " + blockNumber[1]);
        byte[] ACK = { 0, OP_ACK, blockNumber[0], blockNumber[1] };
        dc.send(ByteBuffer.wrap(ACK), socketAddress);
        System.out.println("Acknowledgement Sent");
    }
    
    /*
     * To create a request to GET file from TFTP server
     */
    private ByteBuffer createRequest(final byte opCode, final String fileName, String mode) {
        byte zeroByte = 0;
        int rrqByteLength = 2 + fileName.length() + 1 + mode.length() + 1;
        byte[] rrqByteArray = new byte[rrqByteLength];
        
        int position = 0;
        rrqByteArray[position] = zeroByte;
        position ++;
        rrqByteArray[position] = opCode;
        position ++;
        for (int i = 0; i < fileName.length(); i++) {
            rrqByteArray[position] = (byte) fileName.charAt(i);
            position++;
        }
        rrqByteArray[position] = zeroByte;
        position++;
        for (int i = 0; i< mode.length(); i++) {
            rrqByteArray[position] = (byte) mode.charAt(i);
            position++;
        }
        rrqByteArray[position] = zeroByte;
        ByteBuffer byteBuffer = ByteBuffer.wrap(rrqByteArray);
        return byteBuffer;
    }
    
}
