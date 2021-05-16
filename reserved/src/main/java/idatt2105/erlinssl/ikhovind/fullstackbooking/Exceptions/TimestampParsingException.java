package idatt2105.erlinssl.ikhovind.fullstackbooking.Exceptions;

public class TimestampParsingException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "The given timestamp could not be parsed";
    public TimestampParsingException() {
        super(DEFAULT_MESSAGE);
    }

    public TimestampParsingException(String errorMessage) {
        super(errorMessage);
    }
}
