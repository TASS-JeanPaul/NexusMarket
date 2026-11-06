package application.models.ports.out;

public interface IPasswordHasher {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String storedHash);
}
