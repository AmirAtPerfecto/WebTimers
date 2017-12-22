# WebTimers
This project demonstrates the usage of W3C Navigation Timing API (on any browser) to examine web page responsiveness and root cause in any test as part of the CI, including smoke, regression etc. The project is build such that it should be very easy to add the page performance analysis to any existing test.
Initially you can use this project to collect and analyze page and resource level timing for pages of your script. But advanced usage also allows you to compare the current run against previous executions by page load time, analysis of resources by total, types, size and duration, and then individual level resource analysis.

## Objective:
Assume you have a desktop or mobile website, and you develop it as part of your agile cycle. Would it be useful to know, by page, by browser/version/OS/OS version whether suddenly you increased the page load time significantly?
Would it be good to know that immediately, say, as part of the smoke test on commit?
Also, if the time did increase, would it be helpful to know what resources this page download, the URL for each, size, time etc.?
And last, would it be helpful to have all the data stored in CSV, or exportable, such that you can show trends, identify degredations etc.?


Well, W3C Navigation Timing API (https://www.w3.org/TR/navigation-timing/#sec-navigation-info-interface) and the Resource Timing API (https://developer.mozilla.org/en-US/docs/Web/API/Resource_Timing_API/Using_the_Resource_Timing_API) allow you to do just that. They contain the necessary data at the page level as well as the resource level.</br>
All that is left for you is to collect and analyze these as a part of your script, compare to past data if you like, and save for future use.


*************
## Getting started
**To get started**, you need to

Define a few environment variables:

- LOCAL_PATH: the folder to save all files, for example: /Users/Amir-Perfecto/Downloads/WebTimers/
- WEB_TIMERS_FILE_NAME: Optional, the name of the page level web timers file, for example: webtimers.csv
- WEB_RESOURCE_TIMERS_FILE_NAME: Optional, the name of the resource timers data file, for example: webResourcetimers.csv
- APPLY_TIMESTAMP_TO_RESOURCE_FILENAME: Optional, set this to any value (ex.: true) to save the resource timers data in a separate file each run.

- PERFECTO_CLOUD: your cloud name, for example, abc.perfectomobile.com
- PERFECTO_CLOUD_USERNAME: your cloud username, for example, abd@perfectomobile.com
- PERFECTO_CLOUD_SECURITY_TOKEN: your cloud security token. You can get it in your cloud, following these instructions: http://developers.perfectomobile.com/display/PD/Security+Token




2- Create your script- see NewTestClass.java, test() method.


So calling appendToCSV(null) would result in the file name /Users/Amir/Download/webResourceTimers.csv
or appendToCSM("Amazon") would result in the file name /Users/Amir/Download/webResourceTimers_Amazon_.csv

if APPLY_TIMESTAMP_TO_RESOURCE_FILENAME was defined, an example file name would be


WEB_PAGE_COMPARISON_FILE_NAME pageComparison.csv








This project contains the following classes and the following is their usage:
*************
## NewTestClass
This is the test itself. It initiate the driver. Add the relevant objects:
```
WebPageTimersClass pageTimers;

WebPageResourceTimerClass pageResourceTimers;
```
and then, after each page you want to measure, you can:
                
```
// analyze the page level timers </br>
pageTimers = new WebPageTimersClass(driver, "Amazon Home");
// analyze the resource level data
pageResourceTimers = new WebPageResourceTimerClass(driver, "Amazon Home");
// analyze the data, compare the page timers to saved data etc.
analyzeWebTimers("Amazon.com");
```
Of specific interest is the function:
```
       if (pageTimers.isPageLoadLongerThanBaseline(200, WebPageTimersClass.CompareMethod.VS_AVG, pageName))
            System.out.println("Page "+ pageName+ "took much longer to load!!! ");
```
The idea here is to compare the page load time vs. past data (stored in CSV), based on a method: could be average load times, compare agains a minimum, maximum, or alternatively one can define a 'base' (in the CSV Name column).
The comparison is within 'KPI', in this case 200 mSec.

*************
## WebPageTimerClass
This is where the page level timers and details are actually stored. The class has some metadata, such as OS/browser details, time of test execution etc., and then the detailed measured timers.

At a high level, the class does the following:
- Constructors (based on the webDriver or based on a String, more below)
- toString() overload
- A method to write to CSV (appendToCSV, toCSVString)
- A method to compare current page load time vs. a reference (isPageLoadLongerThanBaseline). More on this below.

In more details:

#### Constructor 1: WebPageTimersClass (RemoteWebDriver w, String name)
The constructor fills in the class based on details provided from the driver, according to:
```
        Object pageTimersO =  w.executeScript("var a =  window.performance.timing ;     return a; ", pageTimers);
```
Then, organizePageTimers( Map<String, Long> data) simply fills in the fields:
```
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
```
#### Constructor 2: WebPageTimersClass(String line)
This constructor takes a string with commas and parses that into a WebPageTimersClass

#### Appending to CSV: appendToCSV(String fileNameAdd)
The objective is to format the class into a comma separated string so it can be appended to a CSV. The path to the file is defined by environment variable LOCAL_PATH + WEB_TIMERS_FILE_NAME.
If youm have multiple pages in the script you want to measure, you can add fileNameAdd which will be added just before the .csv
Examples for these variables are:
LOCAL_PATH: /Users/Amir/Downloads/
WEB_TIMERS_FILE_NAME: webtimers.csv

So calling appendToCSV(null) would result in the file name /Users/Amir/Download/webtimers.csv
or appendToCSM("Amazon") would result in the file name /Users/Amir/Download/webtimers_Amazon_.csv

#### Comparing to existing data: isPageLoadLongerThanBaseline(int KPI, CompareMethod method, String fileNameAdd)
The objective of the method is to read from an existing CSV file of previously recorded data, and compare the page load time to the previous data collection, with the KPI.
Assuming there is data in the file, and the file exist, valid comparison methods are:
- vs. AVG: vs. the average page load time
- vs. min: vs. the minimal page load time
- vs. max: same, max
- vs. base: if a row is in the file where the name = "base" then this would be the basis for the measurement. Note that if this is the selection and there is only one row, it will be considered as the base.

The method reads all the lines from the CSV file, uses constructor #2 above, and compares the page load time against the selected criteria.

*************
## WebPageResourceTimerClass
This class contains the resource level details of the page. Some pages can contain hundreds of resources, it is possible to get a lot of data about them to find the reason for poor responsiveness and optimize the page for different platforms.
The class contains similar header information, such as OS, browser, execution time etc.
It also contains an array of a sub class called **ResourceDetails**. This class contains, for each resource, the name, type, size and load time for that resource. For example:

```
Name= https://www.xyz.com/image.jpg
Type= jpg
Size= 1148
Duration= 87.99999999999994
```

Similar to WebPageTimerClass, it has a constructor from the webdriver. The constructor initializes the header fields.
It then obtains the resource level data;

```
(ArrayList<Map<String,Object>>) w.executeScript("var a =  window.performance.getEntriesByType(\"resource\") ;     return a; ", resourceTimers);
```

And inside **organizePageResourceTimers** it allocates a new 'ResourceDetails' for each entry.

Both class offer Override to toString(), as well as appendToCSV.
The objective is to format the class into a comma separated string so it can be appended to a CSV. The path to the file is defined by environment variable LOCAL_PATH + WEB_TIMERS_FILE_NAME.
If you have multiple pages in the script you want to measure, you can add fileNameAdd which will be added just before the .csv
In addition, it is possible to save the CSV in different files based on the timestamp. That can be done by setting an environment variable APPLY_TIMESTAMP_TO_RESOURCE_FILENAME.
Examples for these variables are:
LOCAL_PATH: /Users/Amir/Downloads/
WEB_TIMERS_FILE_NAME: webResourceTimers.csv

So calling appendToCSV(null) would result in the file name /Users/Amir/Download/webResourceTimers.csv
or appendToCSM("Amazon") would result in the file name /Users/Amir/Download/webResourceTimers_Amazon_.csv

if APPLY_TIMESTAMP_TO_RESOURCE_FILENAME was defined, an example file name would be

/Users/Amir/Download/webResourceTimers_Amazon__1513312541426.csv




