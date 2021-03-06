/**
 * 
 */
package com.resnav.adp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * @author Gennady
 *
 */
public class ProcessInterLakes extends ProcessRN {

	/**
	 * 
	 */
	static Logger logger = Logger.getLogger(ProcessInterLakes.class);
	
	public ProcessInterLakes() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BasicConfigurator.configure();
		Properties props = new Properties();
		try {
			//ProcessInterLakes.run1();
			if (args == null && args.length < 1) {
				System.out.println("Please, provide properties file");
			    return;	
			}
			InputStream is = new FileInputStream(args[0]);
	        props.load(is);
	        System.out.println("Property inputFileRN2:"+ props.getProperty("inputFileRN2"));
			String inputFile = props.getProperty("inputFileRN2", "punches.csv") ;
			String inputPath = props.getProperty("inputPathRN2");
			String outputDir = props.getProperty("outputPathRN2", "InterLakes");
			String outputFile = props.getProperty("outputFileRN2");
            is.close();
			ProcessInterLakes.run(inputPath +"/"+inputFile, outputFile, outputDir);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	public void run2(String inputFile) {
		try {
			CSVReader reader = new CSVReader(new FileReader("punches.csv"), '\t');
		    String [] nextLine;
		    while ((nextLine = reader.readNext()) != null) {
		        // nextLine[] is an array of values from the line
		        System.out.println(nextLine[0] + nextLine[1] + "etc...");
		    }
		    reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		 
		
		String csvFile = "C:/documents/resnav/punches.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
	 
		try {
	 
			br = new BufferedReader(new FileReader(csvFile));
			int recNum = 0;
			/**
			Reader reader = new FileReader("C:/documents/resnav/punches.csv");

			CSVReader<String[]> csvPersonReader = CSVReaderBuilder.newDefaultReader(reader);
			List<String[]> punches = csvPersonReader.readAll();
			//while ((line = br.readLine()) != null) {
			        // use comma as separator
			//	String[] punches = line.split(cvsSplitBy);
	            for (int i=0; i < punches.size(); i++) {
	            	String[] record = punches.get(i);
	            	for (int j=0; j < record.length; j++) {
	            		System.out.print("Rec #"+i+", Code "+j+":" + record[j]);
	            	}
	            }
	            System.out.println();
			//}
	            csvPersonReader.close();
	            reader.close();
	           
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	 
		System.out.println("Done");
	  }
	**/
	public static void run1() {
		try {
		//	File file = new File("C:/documents/resnav/punches.xlsx");
		//	FileInputStream fis = new FileInputStream(file);
		//	final Reader reader = new InputStreamReader(fis, "UTF-8");
		//	final CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL.withHeader());

		Reader in = new FileReader("C:/documents/ResNav/InterLakes/MPunches.csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
		int recNum = 0;
		int empIdx =-1, regHrIdx=-1, rateIdx=-1;
        int tiDeptIdx = -1, tiCostIdx = -1, tiOTT1Idx = -1, tiOTT2Idx = -1, tiShftIdx = -1;
		for (final CSVRecord record : records) {
			if (recNum ==0) {
				for (int i=0; i<record.size(); i++) {
					
				}
			}
	        final String t1 = record.get(0);
		    String t2 = "";
		    System.out.println("Rec #"+recNum+", Code "+t1+":" + t2);
		    recNum++;
		}
		in.close();
		//fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String run(String inputFile, String outputFile, String outputDir) {
		String result = "FAIL";
		try {
			
			File f = new File(inputFile);
			if (!f.exists()) {
				System.out.println("Such file does not exist: "+inputFile);
				return result;
			}
			logger.info("Start processing file: "+inputFile);
			ArrayList<ArrayList<Object>> alist = new ArrayList<ArrayList<Object>>();
            ArrayList<Object> rowlist = new ArrayList<Object>();
            rowlist.add("Co Code");
        	rowlist.add("Batch ID");
        	rowlist.add("File #");
        	rowlist.add("Reg Hours");
        	//rowlist.add("Temp Rate");
        	rowlist.add("Temp Cost Number");
        	rowlist.add("O/T Hours");
        	rowlist.add("Hours 3 Code");
        	rowlist.add("Hours 3 Amount");
        	rowlist.add("Shift");
        	alist.add(rowlist);
            FileInputStream file = new FileInputStream(f);
 
            //Create Workbook instance holding reference to .xlsx file
            //XSSFWorkbook workbook = new XSSFWorkbook(file);
 
            //Get first/desired sheet from the workbook
            //XSSFSheet sheet = workbook.getSheetAt(0);
            try {
            	Workbook workbook = WorkbookFactory.create(file);
	            Sheet sheet = workbook.getSheetAt(0);
	            //Iterate through each rows one by one
	            Iterator<Row> rowIterator = sheet.iterator();
	            
	            int empIdx =-1, regHrIdx=-1, codeIdx=-1, retm1Idx=-1;
	            int tiDeptIdx = -1, tiCostIdx = -1, tiOTT1Idx = -1, tiOTT2Idx = -1, tiShftIdx = -1;
	            while (rowIterator.hasNext())
	            {
	                Row row = rowIterator.next();
	                //For each row, iterate through all the columns
	                int rowNum = row.getRowNum();
	                Iterator<Cell> cellIterator = row.cellIterator();
	                //System.out.print("Row#"+row.getRowNum()+":");
	                rowlist = new ArrayList<Object>();
	                if (rowNum == 0) {
		                while (cellIterator.hasNext())
		                {
		                    Cell cell = cellIterator.next();
		                    int index = cell.getColumnIndex();
		                    double dbValue = 0;
		                    String strValue = "";
		                    switch (cell.getCellType())
		                    {
		                        case Cell.CELL_TYPE_NUMERIC:
		                        	dbValue = cell.getNumericCellValue();
		                            //System.out.print("[N"+index+"]"+dbValue + ",");
		                            break;
		                        case Cell.CELL_TYPE_STRING:
		                        	strValue = cell.getStringCellValue();
		                            //System.out.print("[S"+index+"]"+strValue + ",");
		                            break;
		                    }
	                    	switch (strValue){
	                    		case "TPBADG": empIdx = index; break;
	                    		case "TPDEPT": tiDeptIdx = index; break;
	                    		case "TPCOST": tiCostIdx = index; break;
	                    		case "TPCODE": codeIdx = index; break; //temp rate
	                    		case "TPREGH": regHrIdx = index; break;
	                    		case "TPOTT1": tiOTT1Idx = index; break;
	                    		case "TPOTT2": tiOTT2Idx = index; break;
	                    		case "TPSHFT": tiShftIdx = index; break;
	                    		case "TPRETM1": retm1Idx = index; break;
	                    		default: continue;	                    	
		                    } 		                    
		                }   
	                } else {
	                	rowlist.add("KWE");
	                	rowlist.add("RNKWE");
	                	int empId = (int) row.getCell(empIdx).getNumericCellValue();
	                	rowlist.add(empId+"");
	                	Cell c = row.getCell(regHrIdx);
	                	rowlist.add(c.getNumericCellValue() == 0.0? "0": c.getNumericCellValue());
	                	//temp rate
	                	//c = row.getCell(rateIdx);
	                	//rowlist.add(c.getNumericCellValue() == 0.0? "": c.getNumericCellValue());
	                	//tempCost Number
	                	rowlist.add(row.getCell(tiDeptIdx).getStringCellValue()+row.getCell(tiCostIdx).getStringCellValue());
	                	
	                	c = row.getCell(tiOTT1Idx);
	                	rowlist.add(c.getNumericCellValue() == 0.0? "0": c.getNumericCellValue());
	                	//TODO change this
	                	rowlist.add("DT");
	                	
	                	c = row.getCell(tiOTT2Idx);
	                	rowlist.add(c.getNumericCellValue() == 0.0? "0": c.getNumericCellValue());
	                	
	                	c = row.getCell(tiShftIdx);
	                	rowlist.add(c.getStringCellValue().trim().equals("1")? "": c.getStringCellValue());
	
	                	alist.add(rowlist);
	                }     
	            }//while row iterator
	            file.close();
            } catch (Exception e) {
            	logger.error("Error:"+ e.getMessage());
            	logger.info("Processing file with Apache Commons...");
            	processOtherCSV(inputFile, alist); 
            	logger.info("Finished Processing file with Apache Commons!");
            }
            result = createCSVOutput(outputFile, outputDir, alist);
            logger.info("DONE!");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("Error:"+ e.getMessage());
        }	
		finally {
			
		}
		return result;
	}
	
	private static void processOtherCSV(String inputFile, ArrayList<ArrayList<Object>> alist) throws Exception {
		Reader in = new FileReader(inputFile);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
		int recNum = 0;
		int empIdx =-1, regHrIdx=-1, rateIdx=-1;
        int tiDeptIdx = -1, tiCostIdx = -1, tiOTT1Idx = -1, tiOTT2Idx = -1, tiShftIdx = -1;
		for (final CSVRecord record : records) {
			if (recNum ==0) {
				for (int index=0; index<record.size(); index++) {
					String strValue = record.get(index);
					switch (strValue){
	            		case "TPBADG": empIdx = index; break;
	            		case "TPDEPT": tiDeptIdx = index; break;
	            		case "TPCOST": tiCostIdx = index; break;
	            		//case "TPPRAT": rateIdx = index; break; //temp rate
	            		case "TPREGH": regHrIdx = index; break;
	            		case "TPOTT1": tiOTT1Idx = index; break;
	            		case "TPOTT2": tiOTT2Idx = index; break;
	            		case "TPSHFT": tiShftIdx = index; break;
	            		default: continue;	                    	
					} 		               
				}
			} else {
				ArrayList<Object> rowlist = new ArrayList<Object>();
				rowlist.add("KWE");
            	rowlist.add("RNKWE");
            	
            	rowlist.add(record.get(empIdx));
            	rowlist.add(record.get(regHrIdx));
            	//temp rate
            	//String tempRate = record.get(rateIdx);
            	//rowlist.add(tempRate.trim().equals("0")? "": tempRate);
            	//tempCost Number
            	rowlist.add(record.get(tiDeptIdx)+record.get(tiCostIdx));

            	rowlist.add(record.get(tiOTT1Idx));
            	rowlist.add("DT");
            	rowlist.add(record.get(tiOTT2Idx));
            	
            	String shift = record.get(tiShftIdx);
            	rowlist.add(shift.trim().equals("1")? "": shift);

            	alist.add(rowlist);
			}
		    recNum++;
		}
		in.close();
		//fis.close();		
	}

}
