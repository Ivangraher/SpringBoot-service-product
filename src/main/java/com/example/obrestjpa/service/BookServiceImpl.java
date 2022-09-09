package com.example.obrestjpa.service;

import com.example.obrestjpa.entities.Book;
import com.example.obrestjpa.repository.BookRepository;
import com.example.obrestjpa.util.CsvHelper;
import com.example.obrestjpa.util.ExcelHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class BookServiceImpl implements BookService{

    @Autowired
    BookRepository repository;


    @Override
    public void addBook(Book book) {
        repository.save(book);
    }

     @Override
    public void save(MultipartFile file) {
        try {
            List<Book> bookList = CsvHelper.csvToBBDD(file.getInputStream());
            repository.saveAll(bookList);
        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }

    public void saveExcel(MultipartFile file) {
        try {
            List<Book> bookList = ExcelHelper.excelToBook(file.getInputStream());
            repository.saveAll(bookList);
        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }

    @Override
    public List<Book> getBookList() { return repository.findAll(); }
}
