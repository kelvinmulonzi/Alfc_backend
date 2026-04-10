package com.example.Alfc.sermons;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SermonRepository extends JpaRepository<Sermon, Long> {

    List<Sermon> findAllByOrderByPreachedOnDesc();

    List<Sermon> findAllBySeriesOrderByPreachedOnDesc(String series);
}
