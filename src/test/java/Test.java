import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) throws IOException {
        String path = "/Users/macman/Desktop/mytest";
        File[] files = getFiles(path);


    }


    private static File[] getFiles(String path) {
        File file = new File(path);
        File[] files = file.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    getFiles(pathname.getPath());
                    return true;
                } else {
                    pathname.renameTo(new File(pathname.getPath()+"V2.txt"));
//                    pathname.renameTo(new File(pathname.getPath().replace("V2.txt","")));
                    return false;
                }
            }
        });
        return files;
    }
}
