package macro.instruction;

import java.util.ArrayList;
import java.util.List;

public class Instruction {

    private int flag;
    private List<Data<?>> data;

    public Instruction(int flag) {
        this.flag = flag;
        this.data = new ArrayList<>();
    }

    public int getFlag() {
        return flag;
    }

    public List<Data<?>> getData() {
        return data;
    }

    public Data<?> get(int index) {
        if (index < 0 || index >= data.size() || data.isEmpty()) {
            throw new IndexOutOfBoundsException("Invalid index: "+ index+" instruction.data size: "+ data.size());
        }
        return data.get(index);
    }

    public void insert(Data<?> data) {
        this.data.add(data);
    }


}