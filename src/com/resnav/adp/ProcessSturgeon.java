/**
 * 
 */
package com.resnav.adp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;

/**
 * @author Gennady
 *
 */
public class ProcessSturgeon extends ProcessRN {

	/**
	 * @param args
	 */
	static Logger logger = Logger.getLogger(ProcessSturgeon.class);
	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		Properties props = new Properties();
		try {
			if (args == null && args.length < 1) {
				System.out.println("Please, provide properties file");
			    return;	
			}
			InputStream is = new FileInputStream(args[0]);
	        props.load(is);
	        boolean useFTP = true;
	        if (args.length > 1) {
	        	useFTP = Boolean.parseBoolean(args[1]);
	        }
	        logger.info("useFTP:" +useFTP);
			String inputFile = props.getProperty("inputFileRN1", "PRG9GEPI.csv") ;
			String inputPath = props.getProperty("inputPathRN1");
			String outputDir = props.getProperty("outputPathRN1", "Sturgeon");
			String outputFile = props.getProperty("outputFileRN1");
            is.close();
            if (useFTP) inputFile = downloadFileFromFTP(props, "RN1");
            System.out.println("Downloaded inputFileRN1:"+ inputFile);
            
            SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");
    		String dateStr = sdf.format(System.currentTimeMillis());
    		String fileNameBase = inputFile.substring(0, inputFile.indexOf("."));
    		String fileExt = inputFile.substring(inputFile.indexOf(".")+1);
    		String fullFileName = inputPath+"/"+fileNameBase+ "-"+dateStr+ "."+fileExt;
    		File origFile = new File(inputPath +"/"+inputFile);
    		origFile.renameTo(new File(fullFileName));
    		System.out.println("Downloaded fullFileName:"+ fullFileName);
    		
            String fileName = ProcessSturgeon.run(fullFileName, outputFile, outputDir);
            ProcessSturgeon.sendToSFTP(props, fileName);
            String resultFile = renameOutputFile(fileName);
            System.out.println("<><><> Output Processed fullFileName:"+ resultFile);
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static String run(String inputFile, String outputFile, String outputDir) {
		logger.info("Start processing file: "+inputFile);
		String result = "FAIL";
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		
		SimpleDateFormat inputSdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		SimpleDateFormat inputSdf1 = new SimpleDateFormat("MM/dd/yyyy h:mm");
		try {
			File f = new File(inputFile);
			if (!f.exists()) {
				System.out.println("Such file does not exist: "+inputFile);
				return "Such file does not exist: "+inputFile;
			}
			CSVReader reader = new CSVReader(new FileReader(inputFile), ',');
		    String [] nextLine;
		    int rowNum = 0;
		    ArrayList<ArrayList<Object>> alist = new ArrayList<ArrayList<Object>>();
            ArrayList<Object> rowlist = new ArrayList<Object>();
            rowlist.add("Position ID");
            rowlist.add("Employee ID");
            rowlist.add("Change Effective On");
        	rowlist.add("Last Name");
        	rowlist.add("First Name");
        	rowlist.add("Middle Name");
        	rowlist.add("Is Paid By WFN");
        	rowlist.add("Worked In Country");
        	rowlist.add("Address 1 Line 1");
        	rowlist.add("Address 1 City");
        	rowlist.add("Address 1 State Postal Code");
        	rowlist.add("Address 1 Zip Code");
        	rowlist.add("Address 1 Use As Legal");
        	rowlist.add("Address 2 Line 1");
        	rowlist.add("Address 2 City");
        	rowlist.add("Address 2 State Postal Code");
        	rowlist.add("Address 2 Zip Code");
        	rowlist.add("Home Phone Number");
        	rowlist.add("Work Phone Number");
        	rowlist.add("Work E-mail");
        	rowlist.add("Birth Date");
        	rowlist.add("Gender");
        	rowlist.add("Hire Date");
        	rowlist.add("Termination Date");
        	rowlist.add("Tax ID Number");
        	rowlist.add("Tax ID Type");
        	rowlist.add("Maiden Name");
        	rowlist.add("Education Level");
        	rowlist.add("Union Code");
        	rowlist.add("Preferred Name");
        	rowlist.add("Work Mail Stop");
        	rowlist.add("Employee Type");
        	rowlist.add("Location Code");
        	alist.add(rowlist);
        	int empIdx =-1, maidNameIdx=-1, handicapIdx=-1, disVetIdx = -1, eduLevelIdx =-1, prefNameIdx= -1, mailStopIdx = -1, empTypeIdx = -1;
        	int emailIdx = -1, earnCodeIdx = -1, pdDeptIdx = -1;
		    while ((nextLine = reader.readNext()) != null) {
		        // nextLine[] is an array of values from the line
		    	if (rowNum == 0) {
			    	for (int i=0; i< nextLine.length; i++) {
			    		String item = nextLine[i];
			    		//System.out.print("Row#"+rowNum+":" + item+",");
		    			switch (item){
		    				case "HRRef": empIdx = i; break;
		    				case "MaidenName": maidNameIdx = i; break;
		    				case "HandicapYN": handicapIdx = i; break;
		    				case "DisabledVetYN": disVetIdx = i; break;
		    				case "PhysicalYN": eduLevelIdx = i; break;
		    				case "DriveCoVehiclesYN": prefNameIdx= i; break;
		    				case "NoContactEmplYN": mailStopIdx = i; break;
		    				case "TempWorker": empTypeIdx = i; break;
		    				case "Email": emailIdx = i; break;
		    				case "EarnCode": earnCodeIdx = i; break;
		    				case "PRDept": pdDeptIdx = i; break;
		    				default: continue;
		    			} 
			    	}
		    	} else {
		    		rowlist = new ArrayList<Object>();
		    		rowlist.add("G9G0"+nextLine[empIdx]+"N");
		    		rowlist.add(nextLine[empIdx]+"N"); //empId+N
		    		String hireDateInput = nextLine[21];
		    		String hireDate = hireDateInput.equals("")? "": hireDateInput.substring(0, hireDateInput.indexOf(" "));
		    		rowlist.add(hireDate);//hire-date - Change Effective on
		    		rowlist.add(nextLine[4]);
		    		rowlist.add(nextLine[5]);
		    		rowlist.add(nextLine[6]);
		    		rowlist.add("N");//is Paid by WFN
		    		rowlist.add("USA");//worked in USA
		    		rowlist.add(nextLine[8].trim()); //Address
		    		rowlist.add(nextLine[9].trim());
		    		rowlist.add(nextLine[10]);
		    		rowlist.add(nextLine[11]);
		    		if (nextLine[12] != null) rowlist.add("Y");
		    		else rowlist.add("");
		    		//Address 2
		    		rowlist.add("");rowlist.add("");rowlist.add("");rowlist.add("");
		    		rowlist.add(nextLine[13]);
		    		rowlist.add(nextLine[14]);
		    		rowlist.add(nextLine[emailIdx]);//Email 
		    		//sdf.format(inputSdf.parse(nextLine[20])
		    		String birthDate = nextLine[20];
		    		rowlist.add(birthDate.equals("")? "": birthDate.substring(0, birthDate.indexOf(" ")));//birth-date
		    		rowlist.add(nextLine[18]);//gender
		    		rowlist.add(hireDate);//hire-date
		    		String termDate = nextLine[22];
		    		rowlist.add(termDate.equals("")? "": termDate.substring(0, termDate.indexOf(" ")));//term-date
		    		rowlist.add(nextLine[17]);//SSN
		    		rowlist.add("SSN");
		    		//rowlist.add(nextLine[earnCodeIdx]);//NAICS Worker's Comp Code ?
		    		String maidName = nextLine[maidNameIdx];		    		
		    		if (maidName.equals("Y")) rowlist.add("Eligible for Rehire");
		    		else if (maidName.equals("N")) rowlist.add("Not Eligible for Rehire");
		    		else rowlist.add("");
		    		
		    		if (nextLine[eduLevelIdx].equals("Y")) rowlist.add("PCM");
		    		else rowlist.add("");
		    		
		    		String handicap = nextLine[handicapIdx];
		    		String disableVet = nextLine[disVetIdx];
		    		if (handicap.equals("Y")) {
		    			if (disableVet.equals("Y")) rowlist.add("H-D");
		    			else rowlist.add("H");
		    		} else {
		    			if (disableVet.equals("Y")) rowlist.add("D");
		    			else rowlist.add("");
		    		}
		    		
		    		if (nextLine[prefNameIdx].equals("Y")) rowlist.add("Approved to Operate Co. Vehicle");
		    		else rowlist.add("");
		    		
		    		if (nextLine[mailStopIdx].equals("Y")) rowlist.add("Contact current empr = Y");
		    		else if (nextLine[mailStopIdx].equals("N")) rowlist.add("Contact current empr = N");
		    		else rowlist.add("");
		    		
		    		if (nextLine[empTypeIdx].equals("Y")) rowlist.add("TEMP");
		    		else if (nextLine[empTypeIdx].equals("N")) rowlist.add("N");
		    		else rowlist.add("");
		    		rowlist.add(nextLine[pdDeptIdx]);
		    		
		    		alist.add(rowlist);
		    	}
		    	rowNum++;
		    	//System.out.println();
		    }
		    reader.close();
		    result = ProcessSturgeon.createCSVOutput(outputFile, outputDir, alist);
		    logger.info("DONE!");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error:"+ e.getMessage());
		}
		return result;
	}
	
	public static String createCSVOutput(String fileName, String folder, ArrayList<ArrayList<Object>> alist) throws Exception {
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		//String dateStr = sdf.format(System.currentTimeMillis());
		//String fileNameBase = fileName.substring(0, fileName.indexOf("."));
		//String fileExt = fileName.substring(fileName.indexOf(".")+1);
		String fullFileName = folder+"/"+fileName;
		/*
		File f = new File(fullFileName);
		int suffix = 0;
		while (f.exists()) {
			suffix++;
			//fileNameBase += "#";
			fullFileName = folder+"/"+dateStr+ "_"+fileNameBase+"_"+suffix+"."+fileExt;
			f = new File(fullFileName);
		}
		*/
		FileWriter fw = new FileWriter(fullFileName);
        for (ArrayList<Object> row: alist) {
        	for (int i=0; i< row.size(); i++) {
        		fw.write(row.get(i).toString());
        		if (i< row.size()-1) fw.write(",");
        	}
        	fw.write("\r\n");
        }
        fw.close();
        System.out.println(">>>Created file: "+fullFileName);
        return fullFileName;
	}
	
	private static String renameOutputFile(String fileName) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dateStr = sdf.format(System.currentTimeMillis());
		String fileExt = fileName.substring(fileName.indexOf(".")+1);

		String fileNameBase = fileName.substring(0, fileName.lastIndexOf("/"));
		String name = fileName.substring(fileName.lastIndexOf("/")+1, fileName.indexOf("."));
		String resultFileName = fileNameBase+"/"+dateStr+"_"+name+"."+fileExt;
		File f = new File(fileName);
		if (f.exists()) {
			f.renameTo(new File(resultFileName));
			return resultFileName;
		}
		return null;
		
	}
	

}
