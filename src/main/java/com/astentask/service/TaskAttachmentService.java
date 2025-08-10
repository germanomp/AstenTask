package com.astentask.service;

import com.astentask.dtos.TaskAttachmentDTO;
import com.astentask.exception.ResourceNotFoundException;
import com.astentask.model.Task;
import com.astentask.model.TaskAttachment;
import com.astentask.repositories.TaskAttachmentRepository;
import com.astentask.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskAttachmentService {

    private final TaskAttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;

    public TaskAttachmentDTO saveAttachment(Long taskId, MultipartFile file) throws IOException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task não encontrada"));

        TaskAttachment attachment = TaskAttachment.builder()
                .filename(file.getOriginalFilename())
                .fileType(file.getContentType())
                .data(file.getBytes())
                .task(task)
                .build();

        TaskAttachment saved = attachmentRepository.save(attachment);

        return new TaskAttachmentDTO(saved.getId(), saved.getFilename(), saved.getFileType());
    }

    public List<TaskAttachmentDTO> getAttachmentsByTaskId(Long taskId) {
        List<TaskAttachment> attachments = attachmentRepository.findByTaskId(taskId);
        return attachments.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public byte[] getAttachmentData(Long attachmentId) {
        TaskAttachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment não encontrado"));
        return attachment.getData();
    }

    public void deleteAttachment(Long attachmentId) {
        TaskAttachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment não encontrado"));
        attachmentRepository.delete(attachment);
    }

    public TaskAttachment getAttachment(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment não encontrado"));
    }

    private TaskAttachmentDTO mapToDTO(TaskAttachment attachment) {
        return TaskAttachmentDTO.builder()
                .id(attachment.getId())
                .filename(attachment.getFilename())
                .fileType(attachment.getFileType())
                .build();
    }
}
