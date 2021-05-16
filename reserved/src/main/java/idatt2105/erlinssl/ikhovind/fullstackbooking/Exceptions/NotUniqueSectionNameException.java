package idatt2105.erlinssl.ikhovind.fullstackbooking.Exceptions;

public class NotUniqueSectionNameException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "The given section name is not unique";

    public NotUniqueSectionNameException(String errorMessage) {
        super(errorMessage);
    }


    public NotUniqueSectionNameException() {
        super(DEFAULT_MESSAGE);
    }
}
