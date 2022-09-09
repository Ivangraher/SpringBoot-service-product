package com.example.obrestjpa.controller;

import com.example.obrestjpa.entities.Book;
import com.example.obrestjpa.service.BookService;
import com.example.obrestjpa.util.ExcelHelper;
import com.example.obrestjpa.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/api/excel")
@RestController
public class ExcelController {

    @Autowired
    private BookService bookService;

    //EXTRA v3.0. Subir un fichero .excel para crear la BBDD
    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFileExcel(@RequestParam("file") MultipartFile file) {
        String message = "";
        if (ExcelHelper.hasExcelFormat(file)) {
            try {
                bookService.saveExcel(file);
                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }
        message = "Please upload an excel file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
    }

    @GetMapping("/api/fileBooks")
    public ResponseEntity<List<Book>> getAllBooksToFile() {
        try {
            List<Book> bookList = bookService.getBookList();
            if (bookList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(bookList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
