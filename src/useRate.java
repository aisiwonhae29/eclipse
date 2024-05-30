import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class useRate {
	
	tools tool = new tools();
	Map<String, Float> map = new HashMap<String, Float>();
	
	
	public Map<String, Float> test() {
		try {
			map.put("result", (float) 1);
			String os = tool.chkOs();
			
            if (os.contains("win")) {					// Check the os
                return checkCpuUsageWindows(map);
            } else {
                return checkCpuUsageUnix(map);
            }
			
		}catch (NullPointerException e) {
			System.out.println(e);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		map.put("result", (float) -1);
		
		return map;
	}
	
	private Map<String, Float> checkCpuUsageUnix(Map<String, Float> map) {
		try {
			
	    String command1  = "top -b -n1 | grep 'Cpu(s)' | awk '{print $2+$4+$6+$10+$12+$14+$16}'";
	    String command2 =  " top -b -n 1 | awk '\r\n";	// Dynamically find the memory data based on %MEM
	    	   command2 += " NR==7 {\r\n";
		       command2 += "     for (i=1; i<=NF; i++) {\r\n";
		       command2 += "         if ($i == \"%MEM\") {\r\n";
		       command2 += "             memIndex = i\r\n";
		       command2 += "         }\r\n";
		       command2 += "     }\r\n";
		       command2 += " }\r\n";
		       command2 += " NR>7 && memIndex {\r\n";
		       command2 += "     sum += $memIndex\r\n";
		       command2 += " }\r\n";
		       command2 += " END {\r\n";
		       command2 += "     if (memIndex) {\r\n";
		       command2 += "         print sum";
		       command2 += "     } else {\r\n";
		       command2 += "         print \"%MEM column not found\"\r\n";
		       command2 += "     }\r\n";
		       command2 += " }'";
		       
            Process process1 = Runtime.getRuntime().exec(new String[]{"bash", "-c", command1}); // 	cpu
            Process process2 = Runtime.getRuntime().exec(new String[] {"bash", "-c", command2});// 	memory
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(process1.getInputStream()));
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(process2.getInputStream()));
            
            String line;
            
            while ((line = reader1.readLine()) != null) {
            	map.put("cpuUseRate", Float.parseFloat(line));
            }
            reader1.close();
            line = reader2.readLine();
            map.put("memoryUseRate",  Float.parseFloat(line));
            
            return map;
            

		}catch (IOException e) {
		    e.printStackTrace();
		}
		return map;
	}

	
//        private String parseCpuUsageFromTop(String topOutput) {
//            String[] tokens = topOutput.split(",");
//            for (String token : tokens) {
//                token = token.trim();
//                if (token.contains("id")) {
//                   String idleStr = token.split(" ")[0];
//                    double idle = Double.parseDouble(idleStr);
//                    double cpuUsage = 100.0 - idle;
//                    return String.format("%.2f", cpuUsage);
//                }
//            }
//            return "N/A";
//        }

	private Map<String, Float> checkCpuUsageWindows(Map<String, Float> map) {
	    try {
	        String command1  = "wmic cpu get loadpercentage";
	        String command2_1= "wmic process get WorkingSetSize";
	        String command2_2= "wmic ComputerSystem get TotalPhysicalMemory";
	        
	        Process process1 = Runtime.getRuntime().exec(command1);
	        Process process2 = Runtime.getRuntime().exec(command2_1);
	        Process process3 = Runtime.getRuntime().exec(command2_2);
	        
	        BufferedReader reader1 = new BufferedReader(new InputStreamReader(process1.getInputStream()));
	        String line;
	        while ((line = reader1.readLine()) != null) {
	        	if (line.isEmpty() || !tool.intChecker(line.trim())) {
	        		continue; // Skip headers and empty lines
	        	}else{	       
	        		map.put("cpuUseRate", Float.parseFloat(line.trim()));
	        	}
	        }
	        reader1.close();
	        
	        BufferedReader reader2 = new BufferedReader(new InputStreamReader(process2.getInputStream()));
	        float curMemUse = 0;
	        while ((line = reader2.readLine()) != null) {
	        	if(tool.intChecker(line.trim())) {
	        		curMemUse += Float.parseFloat(line); 
	        	}
	        }
	        reader2.close();
	        
	        BufferedReader reader3 = new BufferedReader(new InputStreamReader(process3.getInputStream()));
	        float maxMemUse = 0;
	        while((line = reader3.readLine()) != null) {
	        	if(tool.intChecker(line.trim())) {
	        		maxMemUse += Float.parseFloat(line); 
	        	}
	        }
	        reader3.close();
	        
	        float useMemRate 	= curMemUse/maxMemUse;
	        float memoryUseRate = Math.round(useMemRate * 100);
	        map.put("memoryUseRate", memoryUseRate);
	        
	        return map;
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return map;
   }
}