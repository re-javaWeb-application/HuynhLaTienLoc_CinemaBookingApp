package com.re.cinema_manager.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Genre")
@Entity
public class Genre {
    @Id
    private int id;

    @Column(name = "genre_name", length = 50)
    @Size(min = 1, max = 50, message = "Tên thể loại phải từ 1 đến 50 ký tự")
    private String genre_name;
}
