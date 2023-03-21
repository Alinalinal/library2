package com.alinab.springcourse.controllers;

import com.alinab.springcourse.models.Book;
import com.alinab.springcourse.models.Person;
import com.alinab.springcourse.services.BooksService;
import com.alinab.springcourse.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final BooksService booksService;
    private final PeopleService peopleService;

    @Autowired
    public BooksController(BooksService booksService, PeopleService peopleService) {
        this.booksService = booksService;
        this.peopleService = peopleService;
    }

    @GetMapping
    public String index(Model model, @RequestParam(name = "page", required = false) Integer page,
                        @RequestParam(name = "books_per_page", required = false) Integer booksPerPage,
                        @RequestParam(name = "sort_by_year", required = false,
                                defaultValue = "false") boolean sortByYear) {

        if (page != null && booksPerPage != null) {
            Page<Book> bookPage = booksService.getAllBooksWithPagination(page - 1, booksPerPage, sortByYear);
            model.addAttribute("books", bookPage.getContent());
            model.addAttribute("currentPage", bookPage.getNumber() + 1);
            model.addAttribute("countOfPage", bookPage.getTotalPages());
            model.addAttribute("booksPerPage", bookPage.getSize());
        } else {
            model.addAttribute("books", booksService.getAllBooks(sortByYear));
            model.addAttribute("countOfPage", 1);
        }
        model.addAttribute("sortByYear", sortByYear);

        return "books/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model, @ModelAttribute("person") Person person) {
        model.addAttribute("book", booksService.getBookById(id));
        Person bookOwner = booksService.getBookOwner(id);
        if (bookOwner != null) {
            model.addAttribute("bookOwner", bookOwner);
        } else {
            model.addAttribute("people", peopleService.getAllPeople());
        }
        return "books/show";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book) {
        return "books/new";
    }

    @PostMapping
    public String create(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "books/new";
        }

        booksService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("book", booksService.getBookById(id));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
                         @PathVariable("id") int id) {
        if (bindingResult.hasErrors()) {
            return "books/edit";
        }

        booksService.update(id, book);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int id) {
        booksService.release(id);
        return "redirect:/books/" + id;
    }

    @PatchMapping("/{id}/appoint")
    public String appoint(@ModelAttribute("person") Person person, @PathVariable("id") int id) {
        booksService.appoint(id, person);
        return "redirect:/books/" + id;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        booksService.delete(id);
        return "redirect:/books";
    }

    @GetMapping("/search")
    public String searchPage() {
        return "books/search";
    }

    @PostMapping("/search")
    public String search(Model model, @RequestParam(name = "starts_with") String startsWith) {
        model.addAttribute("books", booksService.getBooksByStartsWith(startsWith));
        return "books/search";
    }
}
