package com.alpha.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private String address;
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @JsonProperty("isAvailable")
    private boolean available;

    // Attachment class as an inner class
    @Embedded
    private Attachment attachment;

    // Inner class for Attachment
    @Embeddable
    @Data
    @NoArgsConstructor
    public static class Attachment {
        private String fileName;
        private String fileType;

        @Lob
        private byte[] data;

        public Attachment(String fileName, String fileType, byte[] data) {
            this.fileName = fileName;
            this.fileType = fileType;
            this.data = data;
        }
    }
}
