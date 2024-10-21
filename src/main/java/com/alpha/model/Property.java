package com.alpha.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String type;

    @NotBlank
    private String address;

    @NotBlank
    private String description;

    @Column(precision = 10, scale = 2)
    @NotNull
    private BigDecimal price;

    @JsonProperty("isAvailable")
    private boolean available;

    @Embedded
    private Attachment attachment;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "property", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Like> likes;

    @OneToMany(mappedBy = "property", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "property", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Bookmark> bookmarks;

    @OneToMany(mappedBy = "property", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Share> shares;

    private int shareCount; // Keep track of the total shares

    public Property(String name, String description, BigDecimal price, String type, String address, boolean available, User currentUser) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
        this.address = address;
        this.available = available;
        this.user = currentUser;
    }

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
