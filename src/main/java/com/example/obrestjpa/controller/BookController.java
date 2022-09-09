package com.example.obrestjpa.controller;

import com.example.obrestjpa.entities.Book;
import com.example.obrestjpa.repository.BookRepository;
import com.example.obrestjpa.service.BookService;
import com.example.obrestjpa.util.CsvHelper;
import com.example.obrestjpa.util.ExcelHelper;
import com.example.obrestjpa.util.PdfGenerator;
import com.example.obrestjpa.util.ResponseMessage;
import com.lowagie.text.DocumentException;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class BookController {

    BookRepository repository;

    private final Logger log = LoggerFactory.getLogger(BookController.class); // creamos una variable para ir guardando los logs de las llamadas a la API REST

    @Autowired
    private BookService bookService;

    public BookController(BookRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/prueba")
    public String HW2(){
        return """
                 <!doctype html>
                                <html lang="en">
                                  <head>
                                    <!-- Required meta tags -->
                                    <meta charset="utf-8">
                                    <meta name="viewport" content="width=device-width, initial-scale=1">
                                               \s
                                    <!-- Bootstrap CSS -->
                                    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
                                               \s
                                    <title>Hello, world!</title>
                                  </head>
                                  <body>
                                    <h1>Hola mundo desde Spring Boot!</h1>
                                     <a class="btn btn-primary" href="https://www.google.com"> Google </a>
                                    <!-- Optional JavaScript; choose one of the two! -->
                                               \s
                                    <!-- Option 1: Bootstrap Bundle with Popper -->
                                    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p" crossorigin="anonymous"></script>
                                               \s
                                    <!-- Option 2: Separate Popper and Bootstrap JS -->
                                    <!--
                                    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.10.2/dist/umd/popper.min.js" integrity="sha384-7+zCNj/IqJ95wo16oMtfsKbZ9ccEh31eOz1HGyDuCQ6wgnyJNSYdrPa03rtR1zdB" crossorigin="anonymous"></script>
                                    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.min.js" integrity="sha384-QJHtvGhmr9XOIpI6YVutG+2QOK9T+ZnN4kzFN1RtK3zEFEIsxhlmWl5/YESvpZ13" crossorigin="anonymous"></script>
                                    -->
                                  </body>
                                </html>
                """;
    }


    //CRUD sobre la entidad Book

    //1. Buscar todos los libros
    @GetMapping("/api/books")
    public List<Book> findAllBooks(){
        return repository.findAll();
    }



    //2. Buscar un libro por ID

    //Opción 1
    /*
    @GetMapping("/api/books/{id}")
    public Book findByIdBook(@PathVariable Long id){
        Optional<Book> bookOptional = repository.findById(id);

        //Opción 1
        if(bookOptional.isPresent()){
            return bookOptional.get();
        }
        return null;

        //Opción 2
        return bookOptional.orElse(null);
    }
    */


    //Opción 2
    @GetMapping("/api/books/{id}")
    @ApiOperation("Buscar un libro por clave primaria id Long")
    public ResponseEntity<Book> findByIdBook(@PathVariable Long id){
        Optional<Book> bookOptional = repository.findById(id);

        //Opción 1
        if(bookOptional.isPresent()){
            return ResponseEntity.ok(bookOptional.get());
        }
        return ResponseEntity.notFound().build();

        /*
        //Opción 2
                if(bookOptional.isPresent()){
            return new ResponseEntity<>(bookOptional.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        */
    }


    //3. Crear un libro
    @PostMapping("api/createBook/")
    public ResponseEntity<Book> createBook(@RequestBody Book book){
        // guardar el libro recibido por parámetro en la base de datos
        if(book.getId() != null){ // quiere decir que existe el id y por tanto no es una creación
            log.warn("trying to create a book with id");
            System.out.println("trying to create a book with id");
            return ResponseEntity.badRequest().build();
        }
        Book result = repository.save(book);
        return ResponseEntity.ok(result); // el libro devuelto tiene una clave primaria
    }


    //4. Actualizar un libro
    @PutMapping("/api/updateBook/")
    public ResponseEntity<Book> updateBook(@RequestBody Book book){
        if(book.getId() == null ){ // si no tiene id quiere decir que sí es una creación
            log.warn("Trying to update a non existent book");
            return ResponseEntity.badRequest().build();
        }
        if(!repository.existsById(book.getId())){
            log.warn("Trying to update a non existent book");
            return ResponseEntity.notFound().build();
        }
        // El proceso de actualización
        Book result = repository.save(book);
        return ResponseEntity.ok(result); // el libro devuelto tiene una clave primaria
    }


    //5. Eliminar un libro
    @DeleteMapping("/api/deleteBook/{id}")
    public ResponseEntity<Book> deleteBook(@PathVariable Long id){
        if(!repository.existsById(id)){
            log.warn("Trying to delete a non existent book");
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }


    //6. Eliminar todos los libros
    @DeleteMapping("/api/deleteBooks/")
    public ResponseEntity<Book> deleteAllBooks(){
        log.warn("Trying to delete a non existent book");
        repository.deleteAll();
        return ResponseEntity.noContent().build();
    }




    //EXTRA v1.0. Exportar todos los libros en un fichero PDF

    @GetMapping("/api/exportar")
    public void generatePdfFile(HttpServletResponse response) throws DocumentException, IOException {

        response.setContentType("application/pdf");

        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD");
        String currentDateTime = dateFormat.format(new Date());

        String headerkey = "Content-Disposition";
        String headervalue = "attachment; filename=Libros_" + currentDateTime + ".pdf";

        response.setHeader(headerkey, headervalue);

        List<Book> bookList = bookService.getBookList();

        PdfGenerator generator = new PdfGenerator();
        generator.generate(bookList, response);

    }


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
