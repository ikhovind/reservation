package idatt2105.erlinssl.ikhovind.fullstackbooking.Exceptions;

public class AdminPermissionMissingException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "You do not have access to that resource";

    public AdminPermissionMissingException() {
        super(DEFAULT_MESSAGE);
    }

    public AdminPermissionMissingException(String errorMessage) {
        super(errorMessage);
    }
}
