import java.io.*;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        if (args.length == 2) {
            if ("-D".equals(args[0].toUpperCase())) {
                initFile(args[1], 1);
            } else if ("-E".equals(args[0].toUpperCase())) {
                initFile(args[1], 2);
            } else {
                help();
            }
        } else {
            help();
        }
    }

    private static void help() {
        System.out.println("-e <file> \t\t编码文件夹或者文件");
        System.out.println("-d <file> \t\t解码文件夹或者文件");
    }

    private static void initFile(String arg, int type) {
        File file = new File(arg);
        if (!file.exists()) {
            System.out.println("文件或者文件夹不存在");
            return;
        }
        if (type == 1) {
            System.out.println("开始解码");
            decodeFilesFilter(file);
            System.out.println("解码完成");
        } else {
            System.out.println("开始编码");
            encodeFilesFilter(file);
            System.out.println("编码完成");
        }

    }

    /**
     * 解码
     *
     * @param file 要解码的文件或者是文件夹
     */
    private static void decodeFilesFilter(File file) {
        // 阿里支持分享的类型
        if (file.isDirectory()) {
            File[] files = file.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    if (pathname.isDirectory()) {
                        return true;
                    } else {
                        int position = pathname.getName().lastIndexOf("_");
                        if (position != -1) {
                            String suffix = pathname.getName().substring(position).toUpperCase();
                            return (suffix.endsWith(".PNG") && suffix.length() == 37);
                        } else {
                            return false;
                        }


                    }
                }
            });
            if (files != null) {
                for (File file1 : files) {
                    // 苹果电脑有一堆隐藏文件有点烦人都删了
                    if (".DS_Store".equals(file1.getName()) && file1.exists()) {
                        file1.delete();
                        continue;
                    }
                    if ("DS_Store".equals(file1.getName()) && file1.exists()) {
                        file1.delete();
                        continue;
                    }
                    if (file1.isDirectory()) {
                        decodeFilesFilter(file1);
                        continue;
                    }
                    restoreFile(file1);
                }
            }
        } else {
            restoreFile(file);
        }

    }

    private static void restoreFile(File file) {
        String suffix = file.getName().substring(file.getName().lastIndexOf("_")).toUpperCase();
        String hex = suffix.substring(1, 33);
        fixFile(hex, file.getPath());
        file.renameTo(new File(file.getPath().substring(0, file.getPath().length() - 37)));
    }

    /**
     * 编码
     *
     * @param file 要编码的文件或者文件夹
     */
    private static void encodeFilesFilter(File file) {
        // 阿里支持分享的类型
        final String[] TYPES = {"PNG", "JPG", "JPEG", "BMP", "GIF", "WEBP", "HEIC", "AVI", "FLV", "MP4", "MPG", "ASF", "WMV", "MOV", "RMVB", "RM", "FLASH", "TS", "LIVP", "M3U8", "WMA", "MKV", "PDF", "WORD", "TXT", "PPT", "EXCEL", "OUTLOOK", "VISIO", "RTF", "TEXT", "MODE", "FONT", "AUDIO", "APPLICATION", "SRT", "SSA", "ASS", "WEBVTT", "SMI"};
        if (file.isDirectory()) {
            File[] files = file.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    if (pathname.isDirectory()) {
                        return true;
                    } else {
                        String suffix = pathname.getName().substring(pathname.getName().lastIndexOf(".") + 1).toUpperCase();
                        return !Arrays.asList(TYPES).contains(suffix);
                    }
                }
            });
            if (files != null) {
                for (File file1 : files) {
                    // 苹果电脑有一堆隐藏文件有点烦人都删了
                    if (".DS_Store".equals(file1.getName()) && file1.exists()) {
                        file1.delete();
                        continue;
                    }
                    if ("DS_Store".equals(file1.getName()) && file1.exists()) {
                        file1.delete();
                        continue;
                    }
                    if (file1.isDirectory()) {
                        encodeFilesFilter(file1);
                        continue;
                    }

                    replaceFile(file1);
                }
            }
        } else {
            replaceFile(file);
        }
    }

    private static void replaceFile(File file1) {
        String hexName = getFileName(file1.getPath());
        fixFile("89504e470d0a1a0a0000000d49484452", file1.getPath());
        file1.renameTo(new File(file1.getPath() + "_" + hexName + ".png"));
    }

    /**
     * @param path 文件系统
     * @return 获取本来文件的文件头信息
     */
    private static String getFileName(String path) {
        File file = new File(path);
        String headName = null;
        try {
            byte[] bytes = new byte[16];
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            int i = bufferedInputStream.read(bytes);
            if (i != -1)
                headName = bytesToHex(bytes).toUpperCase();
            bufferedInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return headName;
    }

    /**
     * 修改文件的头信息
     *
     * @param hex  "504B03041400080008009C84C7520000"
     * @param path "/Users/macman/Desktop/test.zip"
     */
    private static void fixFile(String hex, String path) {
        byte[] bytes = hexToByteArray(hex);
        try {
            RandomAccessFile raf = new RandomAccessFile(path, "rw");
            raf.seek(0);
            raf.write(bytes);
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] hexToByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            //奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            //偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    /**
     * Hex字符串转byte
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte
     */
    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    /**
     * 字节数组转16进制
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

}
