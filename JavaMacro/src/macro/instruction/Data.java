package macro.instruction;

public class Data<T>
{
    private T value;

    public Data(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public int toInt() {
        if(value instanceof Integer) {
            return (Integer) value;
        }
        else {
            throw new ClassCastException("Cannot cast " + value.getClass().getSimpleName() + " to int");
        }
    }
    public String toString() {
        if(value instanceof String) {
            return (String) value;
        }
        else {
            throw new ClassCastException("Cannot cast " + value.getClass().getSimpleName() + " to String");
        }
    }
    public long toLong() {
        if(value instanceof Long) {
            return (long) value;
        }
        else {
            throw new ClassCastException("Cannot cast " + value.getClass().getSimpleName() + " to Long");
        }
    }

}