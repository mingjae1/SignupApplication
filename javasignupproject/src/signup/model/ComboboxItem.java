package signup.model;

/**
 * JComboBox 항목의 이름(View)과 DB ID(Controller/DAO)를 함께 담는 DTO입니다.
 */
public class ComboboxItem {
    private String name;
    private int id;

    public ComboboxItem(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() { return name; }
    public int getId() { return id; }

    @Override
    public String toString() { return name; }
    
}