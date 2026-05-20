package com.re.cinema_manager.repository;

import com.re.cinema_manager.model.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.genre")
    List<Movie> findAllWithGenre();

    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.genre WHERE m.id = :id")
    Optional<Movie> findByIdWithGenre(@Param("id") Long id);
}
