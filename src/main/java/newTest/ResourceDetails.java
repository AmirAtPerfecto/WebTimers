package newTest;

import static perfecto.CSVHandler.COMMA_DELIMITER;

public class ResourceDetails {
    private String name;
    private String type;
    private long size;
    private double duration;
    private String comparisonResult;
    public static final String RESOURCE_DETAIL_CSV_FILE_HEADER = "name, type, size, duration, comparison result";




    // ************* Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getComparisonResult() {
        return comparisonResult;
    }

    public void setComparisonResult(String comparisonResult) {
        this.comparisonResult = comparisonResult;
    }

    //Resource attributes index for CSV reading
    private enum ResourceTimers {
        ID,
        NAME,
        DATE,
        TIME,
        PAGE,
        OS_NAME,
        OS_VERSION,
        BROWSER_NAME,
        BROWSER_VERSION,
        RESOURCE_NAME,
        RESOURCE_TYPE,
        RESOURCE_SIZE,
        RESOURCE_DURATION;
    }
    //  ************* Constructors

    public ResourceDetails(){
        super();
        this.name = "";
        this.type = "";
        this.size = 0L;
        this.duration = 0L;
        this.comparisonResult = "";
    }
    public ResourceDetails(String name, String type, long size, double duration){
        super();
        this.name = name;
        this.type = type;
        this.size = size;
        this.duration = duration;
        this.comparisonResult = "";
    }
    public ResourceDetails(String name, String type, long size, double duration, String comparisonResult){
        this(name, type, size, duration);
        this.comparisonResult = comparisonResult;
    }
    public ResourceDetails(String line) {
        super();
        String[] tokens = line.split(COMMA_DELIMITER);
        this.name = tokens[ResourceTimers.RESOURCE_NAME.ordinal()];
        this.type = tokens[ResourceTimers.RESOURCE_TYPE.ordinal()];
        this.size = Long.parseLong(tokens[ResourceTimers.RESOURCE_SIZE.ordinal()]);
        this.duration = Double.parseDouble(tokens[ResourceTimers.RESOURCE_DURATION.ordinal()]);
        this.comparisonResult = "";
    }

    // ************* Print out data


    @Override
    public String toString(){
        return
                System.lineSeparator()+"Name= "+name+
                        System.lineSeparator()+"Type= "+type+
                        System.lineSeparator()+"Size= "+size+
                        System.lineSeparator()+"Duration= "+duration;
    }
    public String toCSVString() {
        String send = name+ COMMA_DELIMITER;
        send = send + type+ COMMA_DELIMITER;
        send = send + size+ COMMA_DELIMITER;
        send = send + duration;
        return send;
    }
    public String toCSVStringWithDiff() {
        return toCSVString() + COMMA_DELIMITER + comparisonResult;
    }

}


