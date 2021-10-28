import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) throws IOException {
        String path = "/Users/macman/Desktop/test.jpeg";
        int length = 16;
        RandomAccessFile file = new RandomAccessFile(path, "rw");
        byte[] startBytes = new byte[length];
        file.seek(0);
        file.write(startBytes);

    }
}
