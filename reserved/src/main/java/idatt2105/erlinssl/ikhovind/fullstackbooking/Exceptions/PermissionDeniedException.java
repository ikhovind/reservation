package idatt2105.erlinssl.ikhovind.fullstackbooking.Exceptions;

public class PermissionDeniedException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "You do not have access to that resource";
    public PermissionDeniedException() {
        super(DEFAULT_MESSAGE);
    }

    public PermissionDeniedException(String errorMessage) {
        super(errorMessage);
    }
}
