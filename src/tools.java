import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class tools {
	// Check String data is constructed with int base or not. ex) "1234" => true
	public boolean intChecker(String str) {
		
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (i == 0 && str.charAt(i) == '-') {
                if (str.length() == 1) {
                    return false; // Only a minus sign is not valid
                } else {
                    continue; // Skip the minus sign
                }
            }
            if (!Character.isDigit(str.charAt(i))) {
                return false; // If any character is not a digit
            }
        }
        return true;
	}
	
	public Object logger() throws SecurityException, IOException {
		
		String os = chkOs();
		Logger logger = Logger.getLogger("lgUplusLog");
		FileHandler fileHandler = new FileHandler(String.join("/",config.rootDir, "log", os, "lgUplusLog.log"), true);
		fileHandler.setFormatter(new SimpleFormatter());
		logger.addHandler(fileHandler);

		return logger;
	}
	
	public Map<String, Long> periodeUnit(long startDelay, long Interval) {
		Map<String, Long> map = new HashMap<String, Long>();
		
		map.put("startDelay", startDelay);
		map.put("Interval", Interval);
		
		return map;
	}
	
	public Object ScheduleMaker(TimerTask task, Map<String, Long> map) {
		
			Timer timer = new Timer();
			long delay = map.get("startDelay");
			long intervalPeriod = map.get("Interval");
			timer.scheduleAtFixedRate(task, delay, intervalPeriod);
			
			return timer;
	}
	
	public String chkOs () {
		String os = System.getProperty("os.name").toLowerCase().split(" ")[0];
		
		return os;
	}
	

	public void chkDir() {
		
		String dirPath = String.join("/", config.rootDir, "log", chkOs());
        File directory = new File(dirPath);
       
        boolean testDir = directory.exists();
         // Check if the directory already exists
         if (!testDir) {
             // Create the directory
             if (directory.mkdirs()) {
                 System.out.println("Directory created successfully: " + dirPath);
             } else {
                 System.out.println("Failed to create directory: " + dirPath);
             }
         } else {
             System.out.println("Start the loggin on " + dirPath);
         }
	}
}
