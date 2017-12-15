package newTest;

import org.openqa.selenium.remote.RemoteWebDriver;
import perfecto.CSVHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static perfecto.CSVHandler.COMMA_DELIMITER;
import static perfecto.CSVHandler.NEW_LINE_SEPARATOR;

public class WebPageResourceTimerClass {
    private class ResourceDetails{
        private String name, type;
        private long size;
        private double duration;
        public ResourceDetails(String name, String type, long size, double duration){
            super();
            this.name = name;
            this.type = type;
            this.size = size;
            this.duration = duration;
        }
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

        }
    private long id;
    private String runName;
    private String date;
    private String time;
    private String page;
    private String OSName, OSVersion, browserName, browserVersion;
    private List<ResourceDetails> resourceDetailsArray;
    private static final String CSV_FILE_HEADER = "id, name, date, time, page, OS name, OS version, browser name, browser version, name, type, size, duration";

    // build a web page timers class from a web page driver
    public WebPageResourceTimerClass (RemoteWebDriver w, String name) {
        super();
        this.page = w.getCurrentUrl();
        this.runName = name;
        this.date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        this.time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        this.OSName = w.getCapabilities().getCapability("platformName").toString();
        this.OSVersion = w.getCapabilities().getCapability("platformVersion").toString();
        this.browserName = w.getCapabilities().getCapability("browserName").toString();
        this.browserVersion = w.getCapabilities().getCapability("browserVersion").toString();
        this.resourceDetailsArray = new ArrayList<ResourceDetails>();
        List<Map<String, String>> resourceTimers = new ArrayList<Map<String, String>>();
        ArrayList<Map<String, Object>> resourceTimersO =   (ArrayList<Map<String,Object>>) w.executeScript("var a =  window.performance.getEntriesByType(\"resource\") ;     return a; ", resourceTimers);
        System.out.println(resourceTimersO.getClass().toString());
        organizePageResourceTimers(resourceTimersO);
}

    @Override
    public String toString(){
        String s = System.lineSeparator()+"***********"+
                System.lineSeparator()+ "Page ID= "+id+
                System.lineSeparator()+ "runName= "+runName+
                System.lineSeparator()+ "executionTime= "+date+ " " + time+

                System.lineSeparator()+"Page= "+page+
                System.lineSeparator()+"OS Name= "+OSName+
                System.lineSeparator()+"OS Version= "+OSVersion+
                System.lineSeparator()+"Browser Name= "+browserName+
                System.lineSeparator()+"Browser Version= "+browserVersion;

                for(ResourceDetails rd:resourceDetailsArray)
                    s = s+ "***********" + System.lineSeparator()+rd.toString();
                s = s+ System.lineSeparator()+"***********";
                return s;
    }
    public void appendToCSV(String fileNameAdd){
        String fileName = System.getenv().get("LOCAL_PATH") + System.getenv().get("WEB_RESOURCE_TIMERS_FILE_NAME");
        if (null != fileNameAdd)
            fileName = fileName.replace(".csv", "_"+fileNameAdd+"_.csv");
        if (null != System.getenv().get("APPLY_TIMESTAMP_TO_RESOURCE_FILENAME"))
            fileName = fileName.replace(".csv", System.currentTimeMillis()+".csv");
        CSVHandler.writeCsvFile(fileName, this.toCSVString(), CSV_FILE_HEADER);
    }
    public String toCSVString(){
        String preamble = String.valueOf(id) + COMMA_DELIMITER;
        preamble = preamble + runName+ COMMA_DELIMITER;
        preamble = preamble + date+ COMMA_DELIMITER;
        preamble = preamble + time+ COMMA_DELIMITER;
        preamble = preamble + page+ COMMA_DELIMITER;

        preamble = preamble + OSName+ COMMA_DELIMITER;
        preamble = preamble + OSVersion+ COMMA_DELIMITER;
        preamble = preamble + browserName+ COMMA_DELIMITER;
        preamble = preamble + browserVersion+ COMMA_DELIMITER;

        String details = "";
        for(int i = 0; i< resourceDetailsArray.size()-1; i++)
            details = details + preamble + resourceDetailsArray.get(i).toCSVString() + NEW_LINE_SEPARATOR ;
        details = details + preamble + resourceDetailsArray.get(resourceDetailsArray.size()-1).toCSVString();
        return details;
    }

    private void organizePageResourceTimers( ArrayList<Map<String, Object>> data)
    {
        ResourceDetails rd;
        for (int i=0; i< data.size(); i++) {
            String name = data.get(i).get("name").toString();
            try {
                if (name.contains(","))
                    name = URLEncoder.encode(name, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String type = data.get(i).get("initiatorType").toString();
            double responseEnd = Double.parseDouble(data.get(i).get("responseEnd").toString());
            double startTime = Double.parseDouble(data.get(i).get("startTime").toString());
            long size = 0;
            try {
                size = Long.parseLong(data.get(i).get("transferSize").toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            rd = new ResourceDetails(name, type, size, responseEnd - startTime);
            resourceDetailsArray.add(rd);
            System.out.println("Resource Timing Added: " +  rd.toString());
        }

    }

}
