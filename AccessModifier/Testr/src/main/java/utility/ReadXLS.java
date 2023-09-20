package utility;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import testsuitebase.SuiteBase;

/**
 * This is class is used to Read test data from excel file and write test result
 *
 */
public class ReadXLS extends SuiteBase {
    public String fileLocation;
    public FileInputStream ipstr;
    public FileOutputStream opstr;
    private HSSFWorkbook wb;
    private HSSFSheet ws;

    public ReadXLS(String fileLocation) {
        this.fileLocation = fileLocation;
        try {
            ipstr = new FileInputStream(fileLocation);
            wb = new HSSFWorkbook(ipstr);
            ws = wb.getSheetAt(0);
            ipstr.close();
        } catch (Exception e) {
        	System.err.println("Error in reading file: " + e);
        }
    }

    /**
     * To retrieve No Of Rows from .xls file's sheets.
     * 
     * @param wsName
     * @return
     */
    public int retrieveNoOfRows(String wsName) {
        int sheetIndex = wb.getSheetIndex(wsName);
        if (sheetIndex == -1) {
            return 0;
        } else {
            ws = wb.getSheetAt(sheetIndex);
            int rowCount = ws.getLastRowNum() + 1;
            return rowCount;
        }
    }

    /**
     * To retrieve No Of Columns from .cls file's sheets.
     * 
     * @param wsName
     * @return
     */
    public int retrieveNoOfCols(String wsName) {
        int sheetIndex = wb.getSheetIndex(wsName);
        if (sheetIndex == -1) {
            return 0;
        } else {
            ws = wb.getSheetAt(sheetIndex);
            int colCount = ws.getRow(0).getLastCellNum();
            return colCount;
        }
    }

    /**
     * To retrieve SuiteToRun and CaseToRun flag of test suite and test case.
     * 
     * @param wsName
     * @param colName
     * @param rowName
     * @return
     */
    public String retrieveToRunFlag(String wsName, String colName, String rowName) {
        int sheetIndex = wb.getSheetIndex(wsName);
        if (sheetIndex == -1) {
            return null;
        } else {
            int rowNum = retrieveNoOfRows(wsName);
            int colNum = retrieveNoOfCols(wsName);
            int colNumber = -1;
            int rowNumber = -1;

            HSSFRow suiteRow = ws.getRow(0);

            for (int i = 0; i < colNum; i++) {
                if (suiteRow.getCell(i).getStringCellValue().equals(colName.trim())) {
                    colNumber = i;
                }
            }

            for (int j = 0; j < rowNum; j++) {
                HSSFRow suiteCol = ws.getRow(j);
                if (suiteCol.getCell(0).getStringCellValue().equals(rowName.trim())) {
                    rowNumber = j;
                }
            }

            HSSFRow row = ws.getRow(rowNumber);
            HSSFCell cell = row.getCell(colNumber);
            if (cell == null) {
                return "";
            }
            String value = cellToString(cell);
            return value;
        }
    }

    /**
     * To retrieve DataToRun flag of test data.
     * 
     * @param wsName
     * @param colName
     * @return
     */
    public String[] retrieveToRunFlagTestData(String wsName, String colName) {

    	int sheetIndex=wb.getSheetIndex(wsName);
		if(sheetIndex==-1)
			return null;
		else{
			int rowNum = retrieveNoOfRows(wsName);
			int colNum = retrieveNoOfCols(wsName);
			int colNumber=-1;
					
			
			HSSFRow Suiterow = ws.getRow(0);				
			String data[] = new String[rowNum-1];
			for(int i=0; i<colNum; i++){
				if(Suiterow.getCell(i).getStringCellValue().equals(colName.trim())){
					colNumber=i;					
				}					
			}
			
			if(colNumber==-1){
				return null;				
			}
			
			for(int j=0; j<rowNum-1; j++){
				HSSFRow Row = ws.getRow(j+1);
				if(Row==null){
					data[j] = "";
				}
				else{
					HSSFCell cell = Row.getCell(colNumber);
					if(cell==null){
						data[j] = "";
					}
					else{
						String value = cellToString(cell);
						data[j] = value;	
					}	
				}
			}
			
			return data;			
		}		
    }

    /**
     * To retrieve test data from test case data sheets.
     * 
     * @param wsName
     * @return
     */
    @SuppressWarnings("deprecation")
	public Object[][] retrieveTestData(String wsName) {
        int sheetIndex = wb.getSheetIndex(wsName);
        Object data1[][] = new Object[0][0];
        if (sheetIndex != -1) {
        	int rowNum = retrieveNoOfRows(wsName);
            int colNum = retrieveNoOfCols(wsName);

            Object data[][] = new Object[rowNum - 1][colNum - 2];

            for (int i = 0; i < rowNum - 1; i++) {
                HSSFRow row = ws.getRow(i + 1);
                for (int j = 0; j < colNum - 2; j++) {
                    if (row == null) {
                        data[i][j] = "";
                    } else {
                        HSSFCell cell = row.getCell(j);

                        if (cell == null) {
                            data[i][j] = "";
                        } else {
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                            String value = cellToString(cell);
                            data[i][j] = value;
                        }
                    }
                }
            }
            return data;
        }
		return data1;
        }


    /**
     * Convert value in cell to string
     * 
     * @param cell
     * @return
     */
    @SuppressWarnings("deprecation")
	public static String cellToString(HSSFCell cell) {
        int type;
        Object result = new Object();
        type = cell.getCellType();
        switch (type) {
        case 0:
            result = cell.getNumericCellValue();
            break;

        case 1:
            result = cell.getStringCellValue();
            break;

        case 3:
            result = cell.getRichStringCellValue();
            break;

        default:
        	
        }
        return result.toString();
    }

    /**
     * To write result In test data and test case list sheet.
     * 
     * @param wsName
     * @param colName
     * @param rowNumber
     * @param Result
     * @return
     */
    public boolean writeResult(String wsName, String colName, int rowNumber, String result) {
        try {
            int sheetIndex = wb.getSheetIndex(wsName);
            if (sheetIndex == -1) {
                return false;
            }
            int colNum = retrieveNoOfCols(wsName);
            int colNumber = -1;

            HSSFRow suiterow = ws.getRow(0);
            for (int i = 0; i < colNum; i++) {
                if (suiterow.getCell(i).getStringCellValue().equals(colName.trim())) {
                    colNumber = i;
                }
            }

            HSSFRow row = ws.getRow(rowNumber);
            HSSFCell cell = row.getCell(colNumber);
            if (cell == null) {
                cell = row.createCell(colNumber);
            }
            cell.setCellValue(result);

            opstr = new FileOutputStream(fileLocation);
            wb.write(opstr);
            opstr.close();
        } catch (Exception e) {
        	System.err.println("Error in reading file: " + e);
            return false;
        }
        return true;
    }

    /**
     * To write result In test suite list sheet.
     * 
     * @param wsName
     * @param colName
     * @param rowName
     * @param Result
     * @return
     */
    @SuppressWarnings("deprecation")
	public boolean writeResult(String wsName, String colName, String rowName, String result) {
        try {
            int rowNum = retrieveNoOfRows(wsName);
            int rowNumber = -1;
            int sheetIndex = wb.getSheetIndex(wsName);
            if (sheetIndex == -1) {
                return false;
            }
            int colNum = retrieveNoOfCols(wsName);
            int colNumber = -1;

            HSSFRow suiteRow = ws.getRow(0);
            for (int i = 0; i < colNum; i++) {
                if (suiteRow.getCell(i).getStringCellValue().equals(colName.trim())) {
                    colNumber = i;
                }
            }

            for (int i = 0; i < rowNum - 1; i++) {
                HSSFRow row = ws.getRow(i + 1);
                HSSFCell cell = row.getCell(0);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String value = cellToString(cell);
                if (value.equals(rowName)) {
                    rowNumber = i + 1;
                    break;
                }
            }

            HSSFRow row = ws.getRow(rowNumber);
            HSSFCell cell = row.getCell(colNumber);
            if (cell == null) {
                cell = row.createCell(colNumber);
            }
            cell.setCellValue(result);
            opstr = new FileOutputStream(fileLocation);
            wb.write(opstr);
            opstr.close();
        } catch (Exception e) {
        	System.err.println("Error in reading file: " + e);
            return false;
        }
        return true;
    }
}
