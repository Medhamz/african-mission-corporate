import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Test {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("admin123   -> " + encoder.encode("admin123"));
        System.out.println("manager123 -> " + encoder.encode("manager123"));
        System.out.println("editor123  -> " + encoder.encode("editor123"));
    }
}