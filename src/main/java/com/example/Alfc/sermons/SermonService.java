package com.example.Alfc.sermons;

import com.example.Alfc.common.NotFoundException;
import com.example.Alfc.sermons.dto.SermonRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SermonService {

    private final SermonRepository repository;

    public SermonService(SermonRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Sermon> listAll() {
        return repository.findAllByOrderByPreachedOnDesc();
    }

    @Transactional(readOnly = true)
    public List<Sermon> listBySeries(String series) {
        return repository.findAllBySeriesOrderByPreachedOnDesc(series);
    }

    @Transactional(readOnly = true)
    public Sermon get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sermon " + id + " not found"));
    }

    @Transactional
    public Sermon create(SermonRequest req) {
        return repository.save(Sermon.builder()
                .title(req.title())
                .speaker(req.speaker())
                .scripture(req.scripture())
                .summary(req.summary())
                .preachedOn(req.preachedOn())
                .youtubeVideoId(req.youtubeVideoId())
                .audioUrl(req.audioUrl())
                .thumbnailUrl(req.thumbnailUrl())
                .series(req.series())
                .build());
    }

    @Transactional
    public Sermon update(Long id, SermonRequest req) {
        Sermon s = get(id);
        s.setTitle(req.title());
        s.setSpeaker(req.speaker());
        s.setScripture(req.scripture());
        s.setSummary(req.summary());
        s.setPreachedOn(req.preachedOn());
        s.setYoutubeVideoId(req.youtubeVideoId());
        s.setAudioUrl(req.audioUrl());
        s.setThumbnailUrl(req.thumbnailUrl());
        s.setSeries(req.series());
        return s;
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Sermon " + id + " not found");
        }
        repository.deleteById(id);
    }
}
