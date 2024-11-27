package fonction;

public class DetailedValidationException extends Exception {
    private final String fieldName;
    private final Object invalidValue;
    private final String errorMessage;

    public DetailedValidationException(String fieldName, Object invalidValue, String errorMessage) {
        super(String.format("Validation error in field '%s': %s (Invalid value: %s)",
                fieldName, errorMessage, invalidValue));
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
        this.errorMessage = errorMessage;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getInvalidValue() {
        return invalidValue;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String getMessage() {
        return String.format("Validation error in field '%s': %s (Invalid value: %s)",
                fieldName, errorMessage, invalidValue);
    }
}