package com.eazybytes.jobportal.client.service;

import com.eazybytes.jobportal.dto.TodoDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.*;

import java.util.List;

//@HttpExchange(url="https://jsonplaceholder.typicode.com/todos")
@HttpExchange
public interface TodoService {

    @GetExchange
    List<TodoDto> findAll();

    @GetExchange(url="/{id}")
    TodoDto findById(@PathVariable Long id);

    @PostExchange
    TodoDto create(@RequestBody TodoDto post);

    @PutExchange(url="/{id}")
    TodoDto update(@PathVariable Long id, @RequestBody TodoDto post);

    @DeleteExchange(url="/{id}")
    void delete(@PathVariable Long id);





}
