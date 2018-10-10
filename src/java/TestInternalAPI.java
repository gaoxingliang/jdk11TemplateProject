import jdk.internal.misc.JavaLangAccess;

public class TestInternalAPI {
    public static void main(String[] args) throws Exception {
        JavaLangAccess access = jdk.internal.misc.SharedSecrets.getJavaLangAccess();
        System.out.println("Access got " + access);



        java.lang.management.RuntimeMXBean runtime =
                java.lang.management.ManagementFactory.getRuntimeMXBean();
        java.lang.reflect.Field jvm = runtime.getClass().getDeclaredField("jvm");
        jvm.setAccessible(true);
        sun.management.VMManagement mgmt =
                (sun.management.VMManagement) jvm.get(runtime);
        java.lang.reflect.Method pid_method =
                mgmt.getClass().getDeclaredMethod("getProcessId");
        pid_method.setAccessible(true);

        System.out.println("current pid:" + pid_method.invoke(mgmt));
    }
}
