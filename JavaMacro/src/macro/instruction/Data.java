package macro.instruction;

public record Data<T>(T value) {

    public int toInt() {
        if (value instanceof Integer) {
            return (Integer) value;
        } else {
            throw new ClassCastException("Cannot cast " + value.getClass().getSimpleName() + " to Integer");
        }
    }
    public int toShort() {
        if (value instanceof Short) {
            return (Short) value;
        } else {
            throw new ClassCastException("Cannot cast " + value.getClass().getSimpleName() + " to Short");
        }
    }

    public String toString() {
        if (value instanceof String) {
            return (String) value;
        } else {
            throw new ClassCastException("Cannot cast " + value.getClass().getSimpleName() + " to String");
        }
    }

    public long toLong() {
        if (value instanceof Long) {
            return (long) value;
        } else {
            throw new ClassCastException("Cannot cast " + value.getClass().getSimpleName() + " to Long");
        }
    }

    public char[] toCharArray() {
        if (value instanceof String) {
            return ((String) value).toCharArray();
        } else if (value instanceof char[]) {
            return (char[]) value;
        } else {
            throw new ClassCastException("Cannot cast " + value.getClass().getSimpleName() + " to Char array");
        }
    }

}