package GlobalLogicTask;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.io.*;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Unzip {

    private final String filePath;
    private Comparator comp = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    };
    private TreeMap<String, String> prodactMap = new TreeMap<String, String>(comp);

    public Unzip(String filePath) {

        this.filePath = filePath;

    }

    public Map<String, String> execUnzip() throws FileNotFoundException {

        File fileZip = new File(filePath);

        if (!fileZip.exists() || !fileZip.canRead()) {
            throw new FileNotFoundException(filePath + " cannot be read");
        }

        try {
            ZipFile zip = new ZipFile(filePath);
            Enumeration entries = zip.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();

                if (entry.isDirectory()) {
                    new File(fileZip.getParent(), entry.getName()).mkdirs();
                } else {
                    write(zip.getInputStream(entry),
                            new BufferedOutputStream(new FileOutputStream(
                                    new File(fileZip.getParent(), entry.getName()))));
                }

                File unzipFile = new File(fileZip.getParent(), entry.getName());
                readFile(unzipFile, prodactMap);
            }

            zip.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return prodactMap;
    }

    // Read file
    private static void readFile(File unzipFile, TreeMap prodactMap) throws FileNotFoundException {

        try {
            InputStreamReader fr = new InputStreamReader(new FileInputStream(unzipFile.getAbsoluteFile()), "UTF-8");
            BufferedReader in = new BufferedReader(fr);
            try {
                String s;
                while ((s = in.readLine()) != null) {
                    prodactMap.put(s, unzipFile.getName());
                }
            } finally {
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Create unzip file
    private static void write(InputStream in, OutputStream out) throws IOException {
        byte [] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
        out.close();
        in.close();
    }

    // Print map
    private static void printMap(TreeMap<String, String> prodactMap) {

        char capitalFirstLetter = ' ';
        for (Map.Entry<String, String> values : prodactMap.entrySet()) {
            char productFirstLetter = Character.toUpperCase(values.getKey().charAt(0));
            if (capitalFirstLetter != productFirstLetter) {
                capitalFirstLetter = productFirstLetter;
                System.out.println(capitalFirstLetter + " : " + values.getKey());
            } else {
                System.out.println("    " + values.getKey());
            }
        }
    }

}
