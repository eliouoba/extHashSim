/**
 * Class for a simple record
 */

public class ToyRecord {
    private int id; //for key
    private String name;

    public ToyRecord(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getID() {
	    return id;
    }

    public String getName() {
	    return name;
    }

    public void setID(int id) {
	    this.id = id;
    }

    public void setName(String name) {
	    this.name = name;
    }

    public String toString() {
	    return "ID: " + id + "\t\tName: " + name;
    }

}
