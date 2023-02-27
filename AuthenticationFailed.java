public class AuthenticationFailed extends Exception {
    public AuthenticationFailed(String authenticationError) {
        super(authenticationError);
    }
}
