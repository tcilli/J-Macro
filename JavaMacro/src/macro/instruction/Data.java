package macro.instruction;

public record Data<T>(T value) {

    public int toInt() {
        if (value instanceof Integer) {
            return (Integer) value;
        } else {
            throw new ClassCastException("Cannot cast " + value.getClass().getSimpleName() + " to int");
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

	public String print() {
		return String.valueOf(value);
	}
}