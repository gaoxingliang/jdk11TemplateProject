import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class SignJRE {

    public static String CERTFILE = "", CERTPASS = "", OSSLSIGNBIN = "";

    public static void main(String[] args) {
        if (args.length < 4) {
            usage();
            return;
        }
        String jreFolder = args[0];
        OSSLSIGNBIN = args[1];
        CERTFILE = args[2];
        CERTPASS = args[3];

        File jre = new File(jreFolder);
        verify(jre);
    }
    private static void usage() {
        System.out.println("This tool is using osslsigncode to check and then sign files not signed after using jlink.");
        System.out.println("!!!The files will be replaced!!!!");
        System.out.println("Usage: SignJRE [jreFolder] [OSSLSIGNTOOL_BIN] [CERTFILE] [CERTPASS]");
    }

    public static boolean sign(File input, File output) {
        String cmd = OSSLSIGNBIN
                + " sign -pkcs12 "
                + CERTFILE
                + " -pass "
                + CERTPASS
                + " -in " + input.getAbsolutePath()
                + " -out " + output.getAbsolutePath();
        CommandLine cmdLine = CommandLine.parse(cmd);
        CmdResult r = exec(cmdLine);
        if (r.output.contains("Succeeded")) {
            return true;
        }
        System.out.println("Sign error - " + r);
        return false;
    }

    public static void verify(File src) {
        if (src.isDirectory()) {
            File[] subs = src.listFiles();
            for (File f : subs) {
                verify(f);
            }
        }
        else if (src.isFile() && fileExtensionMatched(src)) {
            if (isSigned(src)) {
                System.out.println("\t\tSigned     - " + src.getAbsolutePath());
            }
            else {
                System.out.println("\t\tNot signed - " + src.getAbsolutePath());
                File tgtFile = new File("temp.dll");
                if (sign(src, tgtFile)) {
                    src.delete();
                    tgtFile.renameTo(src);
                    System.out.println("\t\t\t\t re-sign with our certs success");
                }
                else {
                    System.out.println("\t\t\t\t re-sign failed");
                }
            }
            System.out.println();
        }
    }

    public static boolean fileExtensionMatched(File f) {
        if (f.isFile()) {
            String name = f.getName();
            return name.endsWith(".dll") || name.endsWith(".exe");
        }
        return false;
    }

    public static CmdResult exec(CommandLine cmd) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        DefaultExecutor exec = new DefaultExecutor();
        PumpStreamHandler streamHandler = new PumpStreamHandler(output, err);
        exec.setStreamHandler(streamHandler);
        CmdResult r = new CmdResult();
        try {
            int exit = exec.execute(cmd);
            r.output = output.toString();
            r.error = err.toString();
            r.exitCode = exit;
        }
        catch (Exception e) {
            r.error = e.getMessage();
            r.exitCode = -1;
        }
        return r;
    }


    public static boolean isSigned(File f) {
        System.out.println("===============  Checking  " + f.getAbsolutePath());
        String line = OSSLSIGNBIN + " verify " + f.getAbsolutePath();
        CommandLine cmdLine = CommandLine.parse(line);
        CmdResult r = exec(cmdLine);
        if (r.output.contains("Signature verification: ok")) {
            return true;
        }

        return false;
    }

    public static class CmdResult {
        public int exitCode = -1;
        public String output = "", error = "";

        @Override
        public String toString() {
            return "CmdResult{" +
                    "exitCode=" + exitCode +
                    ", output='" + output + '\'' +
                    ", error='" + error + '\'' +
                    '}';
        }
    }

}
