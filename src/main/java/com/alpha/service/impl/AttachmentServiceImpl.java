//package com.alpha.service.impl;
//
//import com.alpha.model.Attachment;
//import com.alpha.repository.AttachmentRepository;
//import com.alpha.service.AttachmentService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//import org.springframework.web.multipart.MultipartFile;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.Objects;
//
//@Service
//public class AttachmentServiceImpl implements AttachmentService {
//
//    private final AttachmentRepository attachmentRepository;
//
//    private static final Logger logger = LoggerFactory.getLogger(AttachmentServiceImpl.class);
//
//    @Autowired
//    public AttachmentServiceImpl(AttachmentRepository attachmentRepository) {
//        this.attachmentRepository = attachmentRepository;
//    }
//
//    @Override
//    public Attachment saveAttachment(MultipartFile file) throws Exception {
//        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
//
//        // Check for invalid path sequence
//        if (fileName.contains("..")) {
//            throw new Exception("Filename contains invalid path sequence: " + fileName);
//        }
//
//        // Validate file type (optional, add your desired file types)
//        String fileType = file.getContentType();
//        if (!isValidFileType(fileType)) {
//            throw new Exception("Invalid file type: " + fileType);
//        }
//
//        try {
//            // Check if the file is empty
//            if (file.isEmpty()) {
//                throw new Exception("Cannot save empty file: " + fileName);
//            }
//            // Check the size limit (e.g., 10 MB)
//            if (file.getSize() > 10 * 1024 * 1024) {
//                throw new Exception("File is too large: " + fileName);
//            }
//
//            // Create the attachment object
//            Attachment attachment = new Attachment(fileName, fileType, file.getBytes());
//
//            // Save to the repository
//            return attachmentRepository.save(attachment);
//        } catch (DataIntegrityViolationException e) {
//            // Log specific details about the exception
//            logger.error("Integrity violation while saving file: " + fileName, e);
//            throw new Exception("Could not save file due to integrity violation: " + fileName, e);
//        } catch (Exception e) {
//            // Log any other exceptions
//            logger.error("Exception while saving file: " + fileName, e);
//            throw new Exception("Could not save file: " + fileName, e);
//        }
//    }
//
//    @Override
//    public Attachment getAttachment(String fileId) throws Exception {
//        return attachmentRepository
//                .findById(fileId)
//                .orElseThrow(
//                        () -> new Exception("File not found with Id: " + fileId));
//    }
//
//    // Helper method to validate file types
//    private boolean isValidFileType(String fileType) {
//        return fileType != null && (
//                fileType.equals("application/pdf") ||
//                        fileType.equals("image/jpeg") ||
//                        fileType.equals("image/png")
//        );
//    }
//}
