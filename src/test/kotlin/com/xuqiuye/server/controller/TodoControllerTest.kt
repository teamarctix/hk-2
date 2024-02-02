package com.xuqiuye.server.controller

import com.xuqiuye.server.model.Priority
import com.xuqiuye.server.model.Subtask
import com.xuqiuye.server.model.Todo
import com.xuqiuye.server.service.TodoService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class TodoControllerTest {

    private val mockedTodoService = mockk<TodoService>()
    private lateinit var mockMvc: MockMvc

    val mockedTodo = Todo(
        name = "Saturday Board Game Night",
        priority = Priority.MEDIUM,
        subtasks = mutableListOf(
            Subtask(subtaskName = "Buy food and drink"),
            Subtask(subtaskName = "Decide which friends to invite")
        )
    )

    private val mockedTodoWithId1 = Todo(
        id = 1,
        name = "Plan a trip",
        subtasks = mutableListOf(
            Subtask(subtaskName = "Decide where to go"),
            Subtask(subtaskName = "Check flights and hotels"),
            Subtask(subtaskName = "Reserve the dates")
        )
    )

    private val mockedTodoWithId2 = Todo(
        id = 2,
        name = "Buy egg and milk",
        priority = Priority.HIGH,
        completeStatus = true
    )

    @BeforeEach
    fun setup() {
        mockMvc = buildController()
    }

    @Nested
    @DisplayName("/api/note [POST]")
    inner class ApiTodoPostTest {
        @Test
        fun `createTodo() returns status 201 if successfully create todo`() {
            every { mockedTodoService.createTodo(any()) } returns mockedTodo

            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/todos")
                    .content(
                        """
                        {
                            "name": "Saturday Board Game Night",
                            "priority": "MEDIUM",
                            "subtasks": [
                                {
                                    "subtaskName": "Buy food and drink"
                                },
                                {
                                    "subtaskName": "Decide which friends to invite"
                                }
                            ]
                        }
                        """.trimIndent()
                    )
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isCreated)

            verify {
                mockedTodoService.createTodo(
                    Todo(
                        name = "Saturday Board Game Night",
                        priority = Priority.MEDIUM,
                        subtasks = mutableListOf(
                            Subtask(subtaskName = "Buy food and drink"),
                            Subtask(subtaskName = "Decide which friends to invite")
                        )
                    )
                )
            }
        }
    }

    @Nested
    @DisplayName("/api/note [GET]")
    inner class ApiTodoGetTest {
        @Test
        fun `getTodos() returns status 200 and the list of todos`() {
            every { mockedTodoService.getTodos() } returns mutableListOf(mockedTodoWithId1, mockedTodoWithId2)

            mockMvc.perform(
                get("/api/v1/todos")
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Plan a trip"))
                .andExpect(jsonPath("$[0].priority").value("LOW"))
                .andExpect(jsonPath("$[0].completeStatus").value(false))
                .andExpect(jsonPath("$[0].subtasks[0].subtaskName").value("Decide where to go"))
                .andExpect(jsonPath("$[0].subtasks[1].subtaskName").value("Check flights and hotels"))
                .andExpect(jsonPath("$[0].subtasks[2].subtaskName").value("Reserve the dates"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Buy egg and milk"))
                .andExpect(jsonPath("$[1].priority").value("HIGH"))
                .andExpect(jsonPath("$[1].completeStatus").value(true))
        }

        @Test
        fun `getTodo() returns status 200 and the todo by id`() {
            every { mockedTodoService.getTodo(1) } returns mockedTodoWithId1

            mockMvc.perform(
                get("/api/v1/todos/1")
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Plan a trip"))
                .andExpect(jsonPath("$.priority").value("LOW"))
                .andExpect(jsonPath("$.completeStatus").value(false))
                .andExpect(jsonPath("$.subtasks[0].subtaskName").value("Decide where to go"))
                .andExpect(jsonPath("$.subtasks[1].subtaskName").value("Check flights and hotels"))
                .andExpect(jsonPath("$.subtasks[2].subtaskName").value("Reserve the dates"))
        }
    }

    private fun buildController(): MockMvc {
        return MockMvcBuilders
            .standaloneSetup(TodoController(mockedTodoService))
            .build()
    }
}
