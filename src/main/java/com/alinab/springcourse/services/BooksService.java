package com.alinab.springcourse.services;

import com.alinab.springcourse.models.Book;
import com.alinab.springcourse.models.Person;
import com.alinab.springcourse.repositories.BooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class BooksService {

    private final BooksRepository booksRepository;

    @Autowired
    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> getAllBooks(boolean sortByYear) {
        if (sortByYear) {
            return booksRepository.findAll(Sort.by("year"));
        }

        return booksRepository.findAll();
    }

    public Page<Book> getAllBooksWithPagination(Integer page, Integer booksPerPage, boolean sortByYear) {
        if (sortByYear) {
            return booksRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("year")));
        }

        return booksRepository.findAll(PageRequest.of(page, booksPerPage));
    }

    public Book getBookById(int id) {
        Optional<Book> book = booksRepository.findById(id);

        return book.orElse(null);
    }

    public List<Book> getBooksByStartsWith(String startsWith) {
        if (startsWith == null || startsWith.equals("")) {
            return Collections.emptyList();
        }

        return booksRepository.findByTitleStartingWith(startsWith);
    }

    @Transactional
    public void save(Book book) {
        booksRepository.save(book);
    }

    @Transactional
    public void update(int id, Book updatedBook) {
        Book bookToBeUpdated = booksRepository.findById(id).get();
        updatedBook.setId(id);
        updatedBook.setOwner(bookToBeUpdated.getOwner());
        booksRepository.save(updatedBook);
    }

    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }

    public Person getBookOwner(int id) {

        return booksRepository.findById(id).map(Book::getOwner).orElse(null);
    }

    @Transactional
    public void release(int id) {
        booksRepository.findById(id).ifPresent(book -> {
            book.setOwner(null);
            book.setDateOfIssue(null);
            book.setOverdue(false);
        });
    }

    @Transactional
    public void appoint(int id, Person selectedPerson) {
        booksRepository.findById(id).ifPresent(book -> {
            book.setOwner(selectedPerson);
            book.setDateOfIssue(new Date());
        });
    }
}
