package newTest;

import org.openqa.selenium.remote.RemoteWebDriver;
import perfecto.CSVHandler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static perfecto.CSVHandler.COMMA_DELIMITER;

public class WebPageTimersClass {
    private long id;
    private String runName;
    private String date;
    private String time;
    private String page;
    private String OSName, OSVersion, browserName, browserVersion;
    private long duration,
    networkTime,
    httpRequest,
    httpResponse,
    buildDOM,
    render;
    private static final String CSV_FILE_HEADER = "id, name, date, time, page, OS name, OS version, browser name, browser version, duration, network, http Request, http response, build DOM, render";

    //Page attributes index
    private enum PageTimers {
        ID,
        NAME,
        DATE,
        TIME,
        PAGE,
        OS_NAME,
        OS_VERSION,
        BROWSER_NAME,
        BROWSER_VERSION,
        DURATION,
        NETWORK,
        HTTPREQ,
        HTTPRES,
        BUILDDOM,
        RENDER;
    }
    // What method to compare page load time by
    public enum CompareMethod {
        VS_AVG,
        VS_MIN,
        VS_MAX,
        VS_BASE;
    }

    // build a web page timers class from a web page driver
    public WebPageTimersClass (RemoteWebDriver w, String name) {
        super();
        this.page = w.getCurrentUrl();
        this.runName = name;
        this.date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        this.time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        this.OSName = w.getCapabilities().getCapability("platformName").toString();
        this.OSVersion = w.getCapabilities().getCapability("platformVersion").toString();
        this.browserName = w.getCapabilities().getCapability("browserName").toString();
        this.browserVersion = w.getCapabilities().getCapability("browserVersion").toString();

        Map<String,String> pageTimers = new HashMap<String,String>();
        Object pageTimersO =  w.executeScript("var a =  window.performance.timing ;     return a; ", pageTimers);
        organizePageTimers ((Map<String, Long>) pageTimersO);
    }
    // build from a string, we will need this for the CSV comparison
    public WebPageTimersClass(String line){
        super();
        String[] tokens = line.split(COMMA_DELIMITER);
        this.id = Long.parseLong(tokens[PageTimers.ID.ordinal()]);
        this.runName = tokens[PageTimers.NAME.ordinal()];
        this.date = tokens[PageTimers.DATE.ordinal()];
        this.time = tokens[PageTimers.TIME.ordinal()];
        this.page = tokens[PageTimers.PAGE.ordinal()];

        this.OSName = tokens[PageTimers.OS_NAME.ordinal()];
        this.OSVersion = tokens[PageTimers.OS_VERSION.ordinal()];
        this.browserName = tokens[PageTimers.BROWSER_NAME.ordinal()];
        this.browserVersion = tokens[PageTimers.BROWSER_VERSION.ordinal()];
        this.duration = Long.parseLong(tokens[PageTimers.DURATION.ordinal()]);;
        this.networkTime = Long.parseLong(tokens[PageTimers.NETWORK.ordinal()]);;
        this.httpRequest = Long.parseLong(tokens[PageTimers.HTTPREQ.ordinal()]);;
        this.httpResponse = Long.parseLong(tokens[PageTimers.HTTPRES.ordinal()]);;
        this.buildDOM = Long.parseLong(tokens[PageTimers.BUILDDOM.ordinal()]);;
        this.render = Long.parseLong(tokens[PageTimers.RENDER.ordinal()]);;
    }
    // build from scratch. Not sure why I did this
    public WebPageTimersClass(long id, String runName, String date, String time, String page,
                              String OSName, String OSVersion, String browserName, String browserVersion,
                              long duration, long networkTime, long httpRequest, long httpResponse,
                              long buildDOM, long render){
        super();
        this.id = id;
        this.runName = runName;
        this.date = date;
        this.time = time;
        this.page = page;
        this.OSName = OSName;
        this.OSVersion = OSVersion;
        this.browserName = browserName;
        this.browserVersion = browserVersion;
        this.duration = duration;
        this.networkTime = networkTime;
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        this.buildDOM = buildDOM;
        this.render = render;
    }


    @Override
    public String toString(){
        return System.lineSeparator()+"***********"+
                System.lineSeparator()+"Page ID= "+id+
                System.lineSeparator()+"runName= "+runName+
                System.lineSeparator()+"executionTime= "+date+ " " + time+

                System.lineSeparator()+"Page= "+page+
                System.lineSeparator()+"OS Name= "+OSName+
                System.lineSeparator()+"OS Version= "+OSVersion+
                System.lineSeparator()+"Browser Name= "+browserName+
                System.lineSeparator()+"Browser Version= "+browserVersion+

                System.lineSeparator()+"Duration= "+duration+
                System.lineSeparator()+"networkTime= "+networkTime+
                System.lineSeparator()+"httpRequest= "+httpRequest+
                System.lineSeparator()+"httpResponse= "+httpResponse+
                System.lineSeparator()+"buildDOM= "+buildDOM+
                System.lineSeparator()+"render= "+render+
                System.lineSeparator()+"***********";
    }
    public void appendToCSV(String fileNameAdd){
        String fileName = System.getenv().get("LOCAL_PATH") + System.getenv().get("WEB_TIMERS_FILE_NAME");
        if (null != fileNameAdd)
            fileName = fileName.replace(".csv", "_"+fileNameAdd+"_.csv");
        CSVHandler.writeCsvFile(fileName, this.toCSVString(), CSV_FILE_HEADER);
    }
    public String toCSVString(){
        String send = String.valueOf(id) + COMMA_DELIMITER;
        send = send + runName+ COMMA_DELIMITER;
        send = send + date+ COMMA_DELIMITER;
        send = send + time+ COMMA_DELIMITER;
        send = send + page+ COMMA_DELIMITER;

        send = send + OSName+ COMMA_DELIMITER;
        send = send + OSVersion+ COMMA_DELIMITER;
        send = send + browserName+ COMMA_DELIMITER;
        send = send + browserVersion+ COMMA_DELIMITER;

        send = send + duration+ COMMA_DELIMITER;
        send = send + networkTime+ COMMA_DELIMITER;
        send = send + httpRequest+ COMMA_DELIMITER;
        send = send + httpResponse+ COMMA_DELIMITER;
        send = send + buildDOM+ COMMA_DELIMITER;
        send = send + render;
        return send;
    }
    // compare current page load time vs. whats been recorded in past runs
    public boolean isPageLoadLongerThanBaseline(int KPI, CompareMethod method, String fileNameAdd){
        String fileName = System.getenv().get("LOCAL_PATH") + System.getenv().get("WEB_TIMERS_FILE_NAME");
        if (null != fileNameAdd)
            fileName = fileName.replace(".csv", "_"+fileNameAdd+"_.csv");

        // read from the CSV file
        List<String> csvFileStrings = CSVHandler.readCsvFile(fileName);
        // Is the file empty?
        if (null == csvFileStrings || csvFileStrings.size() == 0) return false;
        long min = Long.MAX_VALUE, max = 0, avg = 0;
        // Let's read the strings
        for (String line:csvFileStrings){
            WebPageTimersClass w = new WebPageTimersClass(line);
            if (min > w.duration) min = w.duration;
            if (max < w.duration) max = w.duration;
            avg = avg + w.duration;
            if (null != w.runName && CompareMethod.VS_BASE == method)
                // compare against baseline, or, if there is only one record, compare against it.
                if (csvFileStrings.size() == 1 || w.runName.toLowerCase().contains("base") ){
                System.out.println("comparing against: "+ w.toString());
                return (this.compare(w, KPI));
            }
        }
        avg = avg/csvFileStrings.size();
        System.out.println("Metrics from file, min= " + min+System.lineSeparator()+"max= "+max+
                System.lineSeparator()+"avg= "+avg);
        switch(method){
            case VS_AVG:
                System.out.println("comparing against AVG: "+ avg);
                return (duration - avg) > KPI;
            case VS_MAX:
                System.out.println("comparing against AVG: "+ max);
                return (duration - max) > KPI;
            case VS_MIN:
                System.out.println("comparing against min: "+ min);
                return (duration - min) > KPI;
            default:
                System.out.println("comparing against N/A: "+ avg);
                return false;

        }

    }
    // is the current page load longer than the reference by KPI (seconds)?
    private boolean compare(WebPageTimersClass t, int KPI){
        return (this.duration - t.duration > KPI) ;
    }

    private void organizePageTimers( Map<String, Long> data)
    {
        long navStart = data.get("navigationStart");
        long loadEventEnd = data.get("loadEventEnd");
        long connectEnd = data.get("connectEnd");
        long requestStart = data.get("requestStart");
        long responseStart = data.get("responseStart");
        long responseEnd = data.get("responseEnd");
        long domLoaded = data.get("domContentLoadedEventStart");

        this.duration = loadEventEnd - navStart;
        this.networkTime = connectEnd - navStart;
        this.httpRequest = responseStart - requestStart;
        this.httpResponse = responseEnd - responseStart;
        this.buildDOM = domLoaded - responseEnd;
        this.render = loadEventEnd - domLoaded;

        System.out.println("Page Timing: " +  this.toString());


    }


}
