package com.example.obrestjpa.util;

import com.example.obrestjpa.entities.Book;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] headers = { "Id", "Title", "Author", "Pages", "Price", "Release Date", "Digital" };
    static String SHEET = "Books";
    public static boolean hasExcelFormat(MultipartFile file) {
        if (!TYPE.equals(file.getContentType())) {
            return false;
        }
        return true;
    }
    public static List<Book> excelToBook(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rows = sheet.iterator();
            List<Book> tutorials = new ArrayList<>();
            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }
                Iterator<Cell> cellsInRow = currentRow.iterator();
                Book book = new Book();
                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    switch (cellIdx) {
                        case 0:
                            book.setId((long) currentCell.getNumericCellValue());
                            break;
                        case 1:
                            book.setTitle(currentCell.getStringCellValue());
                            break;
                        case 2:
                            book.setAuthor(currentCell.getStringCellValue());
                            break;
                        case 3:
                            book.setPages((int) currentCell.getNumericCellValue());
                            break;
                        case 4:
                            book.setPrice((double) currentCell.getNumericCellValue());
                            break;
                        case 5:
                            book.setDigital(currentCell.getBooleanCellValue());
                            break;
                        default:
                            break;
                    }
                    cellIdx++;
                }
                tutorials.add(book);
            }
            workbook.close();
            return tutorials;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }
}
