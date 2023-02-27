public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String fundsError) {
        super(fundsError);
    }
}
