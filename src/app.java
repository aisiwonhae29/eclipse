import java.io.IOException;
import java.util.Map;
import java.util.TimerTask;
import java.util.logging.Logger;

public class app {
	
	public static void main(String[] args) throws SecurityException, IOException {
		
 		useRate userate = new useRate();
 		final tools tool = new tools();
 		
 		tool.chkDir();														// Check the log directory is exists.
 		Logger logger = (Logger) tool.logger();
		TimerTask task = (TimerTask) taskMaker(userate, logger);   
		Map<String, Long> map = tool.periodeUnit(0, 1000);					// periodeUnit(int startDelay, int interval)

		tool.ScheduleMaker(task, map);						   
	}
	
	
	public static Object taskMaker(final useRate userate, final Logger logger) {
		
		TimerTask task = new TimerTask() {
			public void run() {
				Map<String, Float> map= userate.test();						// Actual working logic
																			// map { result: [1 || 0], cpuUseRate: [integer rate], memoryUseRate: [memory rate] }
				if (map.get("result")== -1) {								// From system latency, result data passed as null 
					System.out.println("Wait for system latency.");
				}else {
					logger.info(String.join(								// Set the logger message pattern
							"/ ",
							"cpu use rate: " + map.get("cpuUseRate"), 
							"memory use rate: " + map.get("memoryUseRate")+ "%")
							+"\n================================================");
				}
			}
		};
		return task;
	}
}
