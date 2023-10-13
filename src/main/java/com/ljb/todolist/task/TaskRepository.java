package com.ljb.todolist.task;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;


public interface TaskRepository extends JpaRepository<TaskModel, UUID>{
    
    Optional<TaskModel> findByIdUser(UUID idUser);

    //Optional<TaskModel> findByIdAndIdUser(UUID id, UUID idUser);
}
