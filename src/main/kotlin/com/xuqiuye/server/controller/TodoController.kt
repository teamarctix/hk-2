package com.xuqiuye.server.controller

import com.xuqiuye.server.model.Todo
import com.xuqiuye.server.service.TodoService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class TodoController(val service: TodoService) {
    @GetMapping("/todos")
    fun getTodos(): List<Todo> {
        return service.getTodos()
    }

    @GetMapping("/todos/{id}")
    fun getTodo(@PathVariable id: String): Todo {
        return service.getTodo(id.toLong())
    }

    @PostMapping("/todos")
    @ResponseStatus(HttpStatus.CREATED)
    fun createTodo(@RequestBody newTodo: Todo): Todo {
        return service.createTodo(newTodo)
    }
}
