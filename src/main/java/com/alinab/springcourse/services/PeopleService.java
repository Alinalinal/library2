package com.alinab.springcourse.services;

import com.alinab.springcourse.models.Book;
import com.alinab.springcourse.models.Person;
import com.alinab.springcourse.repositories.PeopleRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> getAllPeople() {
        return peopleRepository.findAll();
    }

    public Person getPersonById(int id) {
        return peopleRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Person person) {
        peopleRepository.save(person);
    }

    @Transactional
    public void update(int id, Person updatedPerson) {
        updatedPerson.setId(id);
        peopleRepository.save(updatedPerson);
    }

    @Transactional
    public void delete(int id) {
        peopleRepository.deleteById(id);
    }

    public Optional<Person> getPersonByName(String name) {
        return peopleRepository.findByName(name);
    }

    public List<Book> getBooksByPersonId(int id) {
        Optional<Person> person = peopleRepository.findById(id);

        if (person.isPresent()) {
            Hibernate.initialize(person.get().getBooks());
            processOverdue(person.get());

            return person.get().getBooks();
        }

        return Collections.emptyList();
    }

    private void processOverdue(Person person) {
        if (person != null) {
            LocalDate now = LocalDate.now();
            person.getBooks().stream().filter(book -> book.getDateOfIssue() != null && ChronoUnit.DAYS.between(
                            book.getDateOfIssue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), now) > 10)
                    .forEach(book -> book.setOverdue(true));
        }
    }
}
