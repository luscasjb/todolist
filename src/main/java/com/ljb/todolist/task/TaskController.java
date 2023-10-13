package com.ljb.todolist.task;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ljb.todolist.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor 
public class TaskController {
    
    private TaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){
        taskModel.setIdUser((UUID)request.getAttribute("idUser"));

        var currentDate = LocalDateTime.now();
        if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body("Date doesnt corresponds");
        }

        if(taskModel.getStartAt().isAfter(taskModel.getStartAt())){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body("Start date doesnt corresponds");
        }

        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/")
    public Optional<TaskModel> getTaskList(HttpServletRequest request){
        return this.taskRepository.findByIdUser((UUID)request.getAttribute("idUser"));
    }

    @PutMapping("/{id}")
    public ResponseEntity updateTasks(@RequestBody TaskModel taskModel, HttpServletRequest request,
        @PathVariable UUID id){

        var task = this.taskRepository.findById(id).orElse(null);
        
        if(task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Task not found");
        }

        var idUser = request.getAttribute("idUser");

        if (!task.getIdUser().equals(idUser)) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body("User not allowed to change this task");
        }

        Utils.copyNonNullProperties(taskModel, task);

        var taskUpdated = this.taskRepository.save(task);
        return ResponseEntity.ok().body(taskUpdated);
    }

}
