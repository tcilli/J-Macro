package macro.instruction;

import java.util.ArrayList;
import java.util.List;

public class Instruction {

    private final int flag;

    private final List<Data<?>> data;

    public Instruction(final int flag) {
        this.flag = flag;
        this.data = new ArrayList<>();
    }

    public int getFlag() {
        return flag;
    }

	public List<Data<?>> getData() {
		return data;
	}

	public Data<?> get(final int index) {
        if (index < 0 || index >= data.size()) {
            throw new IndexOutOfBoundsException("Invalid index: "+ index+" instruction.data size: "+ data.size());
        }
        return data.get(index);
    }

    public <T> void insert(final T data) {
        this.data.add(new Data<>(data));
    }
}
