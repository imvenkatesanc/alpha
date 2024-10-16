//package com.alpha.controller;
//
//import com.alpha.model.AttachmentResponseData;
//import com.alpha.model.Attachment;
//import com.alpha.service.AttachmentService;
//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.core.io.Resource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
//
//import java.awt.*;
//
//@RestController
//public class AttachmentController {
//
//    private AttachmentService attachmentService;
//
//    public AttachmentController(AttachmentService attachmentService) {
//        this.attachmentService = attachmentService;
//    }
//
//    @PostMapping("/upload")
//    public AttachmentResponseData uploadFile(@RequestParam("file")MultipartFile file) throws Exception {
//        Attachment attachment = null;
//        String downloadURl = "";
//        attachment = attachmentService.saveAttachment(file);
//        downloadURl = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/download/")
//                .path(attachment.getId())
//                .toUriString();
//
//        return new AttachmentResponseData(attachment.getFileName(),
//                downloadURl,
//                file.getContentType(),
//                file.getSize());
//    }
//
//    @GetMapping("/download/{fileId}")
//    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) throws Exception {
//        Attachment attachment = attachmentService.getAttachment(fileId);
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(attachment.getFileType()))
//                .header(HttpHeaders.CONTENT_DISPOSITION,
//                        "inline; filename=\"" + attachment.getFileName() + "\"") // Changed 'attachment' to 'inline'
//                .body(new ByteArrayResource(attachment.getData()));
//    }
//}