package com.example.Alfc.devotionals;

import com.example.Alfc.common.NotFoundException;
import com.example.Alfc.devotionals.dto.DevotionalRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DevotionalService {

    private final DevotionalRepository repository;

    public DevotionalService(DevotionalRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Devotional today() {
        return repository.findByDate(LocalDate.now())
                .orElseThrow(() -> new NotFoundException("No devotional published for today"));
    }

    @Transactional(readOnly = true)
    public List<Devotional> listAll() {
        return repository.findAllByOrderByDateDesc();
    }

    @Transactional(readOnly = true)
    public Devotional get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Devotional " + id + " not found"));
    }

    @Transactional
    public Devotional create(DevotionalRequest req) {
        repository.findByDate(req.date()).ifPresent(existing -> {
            throw new IllegalArgumentException("Devotional already exists for " + req.date());
        });
        return repository.save(Devotional.builder()
                .date(req.date())
                .title(req.title())
                .verseReference(req.verseReference())
                .verseText(req.verseText())
                .body(req.body())
                .author(req.author())
                .build());
    }

    @Transactional
    public Devotional update(Long id, DevotionalRequest req) {
        Devotional d = get(id);
        d.setDate(req.date());
        d.setTitle(req.title());
        d.setVerseReference(req.verseReference());
        d.setVerseText(req.verseText());
        d.setBody(req.body());
        d.setAuthor(req.author());
        return d;
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Devotional " + id + " not found");
        }
        repository.deleteById(id);
    }
}
