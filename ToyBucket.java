/**
 * Class for a simulation of bucket
 */

import java.util.*;

public class ToyBucket {
    private static final int  SIZE = 2;  // two records per bucket
    
    private int localDepth;
    private boolean full;
    private ArrayList<ToyRecord> records;
    
    public ToyBucket() {
		localDepth = 0;
		full = false;
		records = new ArrayList<ToyRecord>();
    }

    public int getLocalDepth() {
		return localDepth;
    }

    public boolean isFull() {
		return full;
    }

    public boolean isEmpty() {
		if (records.size() == 0) return true;
		else return false;
    }

    public int getNumOfRecords() {
		return records.size();
    }
    
    public void setLocalDepth(int d) {
		localDepth = d;
    }

    public boolean addRecord(ToyRecord r) {
		if (!full) {
			records.add(r);
		
			if (records.size() == SIZE) full = true;
			return true;
		} else {
			return false;
		}	
    }

    public boolean deleteRecord(int id) {
		for (ToyRecord r : records) {
			if (r.getID() == id) {
			records.remove(r);
			if (full) full = false;
			return true;
			}
		}
		return false;
    }

    public void deleteAllRecords() {
		records.clear();
		full = false;	
    }

	public ArrayList<ToyRecord> getAllRecords() {
		if (records.size() != 0) {
			ArrayList<ToyRecord> temp = new ArrayList<ToyRecord>(records);
			return temp;
		} else return null;		
    }

   
    public String toString() {
		String contents = "Local depth: " + localDepth + "\n";
		for (int i = 0; i < records.size(); i++) {
			contents += records.get(i).toString() + "\n";
		}
		return contents;
    }

}
