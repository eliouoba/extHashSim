
import java.util.*;

public class ExtHashingSim { 

    private static final int H_FACTOR = 32;
    private static final int B_LENGTH = 
        Integer.toBinaryString(H_FACTOR-1).length();
    private static int globalDepth;
    private static ArrayList<ToyBucket> directory;

    private static void initializeDir() {
        globalDepth = 0;
        directory = new ArrayList<ToyBucket>(1);
        directory.add(new ToyBucket());
    }

    private static void setEmptyBuckets() {
        for (int i = 0; i < directory.size(); i++) {
            if (directory.get(i).isEmpty()) directory.get(i).setLocalDepth(-1);
        }
    }
    
    private static String toBinary(int n) {
        return String.format("%"+B_LENGTH+"s", Integer.toBinaryString(n))
                .replaceAll(" ", "0");
    }

    private static int hashKey(int key) {
	    return key % H_FACTOR;
    }
    
    //return dir index in binary
    private static String getBinaryDirIndex(int i) {
        String binaryIndex = toBinary(i);
        return binaryIndex.substring(B_LENGTH - globalDepth, B_LENGTH );
    }

    //return hashed key in binary according to global depth
    private static String getBinaryHashedKey(int hashedKey) {
        String binaryHashed = toBinary(hashedKey);
        return binaryHashed.substring(0,globalDepth);
    }

    //return hashed key in binary, pass in # of bits
    private static String getBinaryHashedKey2(int key, int depth) {
        String binaryHashed = toBinary(hashKey(key));
        return binaryHashed.substring(0,depth);
    }
    
    private static void printBuckets() {
        for (int i = 0; i < directory.size(); i++) {
            String binaryI = toBinary(i);
            String binaryID = binaryI.substring(B_LENGTH - globalDepth, B_LENGTH);
            System.out.println("Directory: " + binaryID);
            System.out.println(directory.get(i));
        }
    }

    public static void p(Object o) {System.out.println(o);}
		
    public static void insertRecord(String name, int id) {
        ToyBucket bucket;
        
        //identify the right bucket
        if (globalDepth == 0) bucket = directory.get(0);
        else {
            int bucketIndex = Integer.parseInt(getBinaryHashedKey(hashKey(id)), 2);
            bucket = directory.get(bucketIndex);
        }

        //take care of easy case: non-full bucket
        if (!bucket.isFull()) {
            bucket.addRecord(new ToyRecord(id, name));
            p("\nDirectory structure:"); 
            printBuckets();
            return;
        }
       
        //increment its local depth and
        // create the second bucket with the same local depth
        int newLD = bucket.getLocalDepth() + 1;
        bucket.setLocalDepth(newLD);
        ToyBucket newBucket = new ToyBucket();
        newBucket.setLocalDepth(newLD);
        
        //redistribute the values
        ArrayList<ToyRecord> bucketItems = bucket.getAllRecords();
        bucketItems.add(new ToyRecord(id, name));

        String oldBHK = 
            getBinaryHashedKey2(hashKey(bucketItems.get(0).getID()), newLD-1);

        bucket.deleteAllRecords();

        for (ToyRecord tr: bucketItems) {
            String bhk = getBinaryHashedKey2(hashKey(tr.getID()), newLD);
            if (bhk.equals(oldBHK + "0")) bucket.addRecord(tr);
            else newBucket.addRecord(tr);   // + "1"
        }
    
        // if global depth needs to be incremented:
        if(newLD-1 >= globalDepth) {
            //create a new directory
            globalDepth++;
            ArrayList<ToyBucket> tempdir = 
                new ArrayList<ToyBucket>(1 << globalDepth);

            String localBHK;

            //loop through old directory and "duplicate" buckets
            //with a local depth less than the global depth.
            //also insert the split buckets.
            for (int i = 0; i < directory.size(); i++) { 
                localBHK = getBinaryDirIndex(i).substring(1);//leading 0

                if(localBHK.equals(oldBHK)) {  //the one that caused a split
                    tempdir.add(2*i, bucket);
                    tempdir.add(2*i+1, newBucket);
                } else {
                    tempdir.add(2*i, directory.get(i)); 
                    tempdir.add(2*i+1, directory.get(i)); 
                }
            }
            directory = tempdir;

        } else {    //bucket depth is less than global depth      
            //reassign the pointers
            for (int i = 0; i < directory.size(); i++) { 
                String tempDirI = getBinaryDirIndex(i).substring(0, newLD);
                String bhk =
                    getBinaryHashedKey(hashKey(bucketItems.get(0).getID()));
                String temp1 = tempDirI.substring(0, newLD-1);
                String temp2 = bhk.substring(0, newLD-1);

                if (temp1.equals(temp2)) {  //the one that caused a split
                    if (tempDirI.equals(temp2 + "0"))
                        directory.set(i, bucket);
                    else directory.set(i, newBucket);
                }
            }
        }
        p("\nDirectory structure:"); 
        printBuckets();
    }

    public static void retrieveRecord(int id) {
        String name = "";
        int bucketIndex = Integer.parseInt(getBinaryHashedKey(hashKey(id)),2);
        ToyBucket bucket = directory.get(bucketIndex);
        ArrayList<ToyRecord> records = bucket.getAllRecords();
        for (ToyRecord record: records) {
            if (record.getID() == id) {
                name = record.getName();
                break;
            }
        }
        
        p(name.equals("")?"Record doesn't exist":"Name of record is "+ name);
    }

    public static void displayInfo() {
        p("Directory size: " + directory.size());
        p("Global depth: " + globalDepth);
        Set<ToyBucket> uniqueBuckets = new HashSet<ToyBucket>();
        uniqueBuckets.addAll(directory);
        p("Number of buckets: " + uniqueBuckets.size());

        int count = 0;
        int empty = 0;
        for(ToyBucket b: uniqueBuckets) {
            if (b.isEmpty()) empty++;
            count += b.getNumOfRecords();
        }
        p("Number of empty buckets: " + empty);
        p("Number of records total: " + count);
    }

    public static void main(String[] args) {
        Scanner k = new Scanner(System.in);
        String prompt;
        String choice;
        
        initializeDir();
        
        // testing
        // insertRecord("R1", 2369);
        // insertRecord("R2", 3760);
        // insertRecord("R3", 4692);
        // insertRecord("R4", 4871);
        // insertRecord("R5", 5659);
        // insertRecord("R6", 1821);
        // insertRecord("R7", 1074);
        // insertRecord("R8", 7115);
        // insertRecord("R9", 1620);
        // insertRecord("R10", 2428);
        // insertRecord("R11", 3943);
        // insertRecord("R12", 4750);
        // insertRecord("R13", 6975);
        // insertRecord("R14", 4981);
        // insertRecord("R14", 4981);

		for (;;) {
            prompt = ("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~Choose an action:~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n\t1: Insert a record \n\t2: Retrieve a record\n\t3: See all info\n\tq: Quit");
		    System.out.print(prompt);
            choice = "";
			System.out.print("\n--> ");
            choice = k.nextLine();
            if (choice.equalsIgnoreCase("q")) {
                p("Quitting...");
                k.close();       
                System.exit(0);
            } else try {
                int choiceint = Integer.parseInt(choice);
				if (choiceint > 3 || choiceint < 1) throw new Exception();
                if (choiceint == 1) {
                    p("Enter a name to insert:");
                    System.out.print("\n--> ");
                    String name = k.nextLine();
                    p("Enter an id to insert:");
                    System.out.print("\n--> ");
                    int id = Integer.parseInt(k.nextLine());
                    insertRecord(name, id);
                } else if (choiceint == 2) {
                    p("Enter a record to retrieve:");
                    int record = Integer.parseInt(k.nextLine());
                    retrieveRecord(record);
                } else if (choiceint == 3) {
                    displayInfo(); 
                } else throw new Exception();                
			} catch (Exception e) {
				p("Try again.");
			}
		} 
    }
}
