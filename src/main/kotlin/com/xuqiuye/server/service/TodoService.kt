package com.xuqiuye.server.service

import com.xuqiuye.server.exception.NotFoundException
import com.xuqiuye.server.model.Todo
import com.xuqiuye.server.repository.TodoRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime

interface TodoService {
    fun getTodos(): List<Todo>
    fun getTodo(id: Long): Todo
    fun createTodo(newTodo: Todo): Todo
}

@Service
class TodoServiceImpl(
    private val todoRepo: TodoRepository
) : TodoService {
    override fun getTodos(): List<Todo> {
        return todoRepo.findAllByOrderByCreatedAtDesc()
    }

    override fun getTodo(id: Long): Todo {
        val todo = todoRepo.findByIdOrNull(id)
        if (todo != null) {
            return todo
        } else {
            throw NotFoundException("Could not find a todo by id: $id")
        }
    }

    override fun createTodo(newTodo: Todo): Todo {
        newTodo.apply {
            this.createdAt = LocalDateTime.now()
        }
        return todoRepo.save(newTodo)
    }
}
