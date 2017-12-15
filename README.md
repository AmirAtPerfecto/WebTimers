# WebTimers
This project demonstrates the usage of W3C Navigation Timing API (on any browser) to examine web responsiveness and root cause in CI.

Objective:
Assume you have a desktop or mobile website, and you develop it as part of your agile cycle. Would it be useful to know, by page, by browser/version/OS/OS version whether suddenly you increased the page load time significantly?
Would it be good to know that immediately, say, as part of the smoke test on commit?
Also, if the time did increase, would it be helpful to know what resources this page download, the URL for each, size, time etc.?
And last, would it be helpful to have all the data stored in CSV, or exportable, such that you can show trends, identify degredations etc.?


Well W3C Navigation Timing API (https://www.w3.org/TR/navigation-timing/#sec-navigation-info-interface) and the Resource Timing API (https://developer.mozilla.org/en-US/docs/Web/API/Resource_Timing_API/Using_the_Resource_Timing_API) allow you to do just that. They contain the necessary data at the page level as well as the resource level.
All that is left for you is to collect and analyze these as a part of your script, compare to past data if you like, and save for future use.

This project contains the following classes and the following is their usage:
*************
NewTestClass: This is just the test itself. Initiate the driver. Add the relevant objects:

WebPageTimersClass pageTimers;

WebPageResourceTimerClass pageResourceTimers;
    
and then, after each page you want to measure, you can:
                
// analyze the page level timers </br>
pageTimers = new WebPageTimersClass(driver, "Amazon Home");
// analyze the resource level data
pageResourceTimers = new WebPageResourceTimerClass(driver, "Amazon Home");
// analyze the data, compare the page timers to saved data etc.
analyzeWebTimers("Amazon.com");

Of specific interest is the function:
       if (pageTimers.isPageLoadLongerThanBaseline(200, WebPageTimersClass.CompareMethod.VS_AVG, pageName))
            System.out.println("Page "+ pageName+ "took much longer to load!!! ");
The idea here is to compare the page load time vs. past data (stored in CSV), based on a method: could be average load times, compare agains a minimum, maximum, or alternatively one can define a 'base' (in the CSV Name column).
The comparison is within 'KPI', in this case 200 mSec.

*************
WebPageTimerClass

