import jdk.internal.misc.JavaLangAccess;

public class TestInternalAPI {
    public static void main(String[] args) {
        JavaLangAccess access = jdk.internal.misc.SharedSecrets.getJavaLangAccess();
        System.out.println("Access got " + access);
    }
}
