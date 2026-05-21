package com.re.cinema_manager.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/** Nhóm phim sắp chiếu theo thể loại. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenreSectionView {

    private Long genreId;
    private String genreName;

    @Builder.Default
    private List<UpcomingMovieView> movies = new ArrayList<>();
}
