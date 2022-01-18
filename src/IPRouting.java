
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class IPRouting {
    public IPRouting() {
    }

    public static void main(String[] args) throws Exception {
        File RT = new File("src/RoutingTable.txt");
        BufferedReader RTReader = new BufferedReader(new FileReader(RT));
        ArrayList<String> completeList = new ArrayList();
        ArrayList<String> destList = new ArrayList();
        ArrayList<Integer> maskList = new ArrayList();
        ArrayList<String> nextHopList = new ArrayList();
        ArrayList nextInterfaceList = new ArrayList();

        String dest;
        while((dest = RTReader.readLine()) != null) {
            completeList.add(dest);
            String[] addrMask = dest.split("/");
            destList.add(addrMask[0]);
            maskList.add(Integer.valueOf(addrMask[1]));
            nextHopList.add(RTReader.readLine());
            nextInterfaceList.add(RTReader.readLine());
        }

        int tableSize = nextHopList.size();
        File Packets = new File("src/RandomPackets.txt");
        BufferedReader PacketsReader = new BufferedReader(new FileReader(Packets));
        new ArrayList();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("src/RoutingOutput.txt"), StandardCharsets.UTF_8));

        String packets;
        try {
            while((packets = PacketsReader.readLine()) != null) {
                int winner = -1;
                int winnermask = -1;
                ArrayList<Integer> validList = new ArrayList();
                String[] currpacketnums = packets.split("\\.");

                int i;
                for(i = 0; i < tableSize; ++i) {
                    int maskNum = (Integer)maskList.get(i);
                    String[] addrnums = ((String)destList.get(i)).split("\\.");
                    int maskCount = 0;
                    int countpos = 0;

                    boolean match;
                    for(match = true; maskCount < maskNum && match && countpos <= 3; ++countpos) {
                        maskCount += 8;
                        if (!Integer.valueOf(currpacketnums[countpos]).equals(Integer.valueOf(addrnums[countpos]))) {
                            match = false;
                        }
                    }

                    if (match) {
                        validList.add(i);
                    }
                }

                for(i = 0; i < validList.size(); ++i) {
                    if (winnermask < (Integer)maskList.get((Integer)validList.get(i))) {
                        winner = (Integer)validList.get(i);
                        winnermask = (Integer)maskList.get((Integer)validList.get(i));
                    }
                }

                if (currpacketnums[0].equals("127")) {
                    writer.write(packets + " is loopback; discarded\n");
                    System.out.println(packets + " is loopback; discarded");
                } else if (Integer.parseInt(currpacketnums[0]) > 223) {
                    writer.write(packets + " is malformed; discarded\n");
                    System.out.println(packets + " is malformed; discarded");
                } else if (((String)nextHopList.get(winner)).equals("-")) {
                    writer.write(packets + " will be forwarded on the directly connected network on interface " + (String)nextInterfaceList.get(winner) + ".\n");
                    System.out.println(packets + " will be forwarded on the directly connected network on interface " + (String)nextInterfaceList.get(winner) + ".");
                } else {
                    writer.write(packets + " will be forwarded to " + (String)nextHopList.get(winner) + " out on interface " + (String)nextInterfaceList.get(winner) + "\n");
                    System.out.println(packets + " will be forwarded to " + (String)nextHopList.get(winner) + " out on interface " + (String)nextInterfaceList.get(winner));
                }
            }
        } catch (Throwable var27) {
            try {
                writer.close();
            } catch (Throwable var26) {
                var27.addSuppressed(var26);
            }

            throw var27;
        }

        writer.close();
    }
}