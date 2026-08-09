package com.cp.assignment.miniproject.batch.writer;

import com.cp.assignment.miniproject.batch.model.ListATransaction;
import com.cp.assignment.miniproject.repository.ReconciliationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListAWriter implements ItemWriter<ListATransaction> {

    private final ReconciliationRepository repository;

    @Override
    public void write(Chunk<? extends ListATransaction> chunk) {
        //call insert new
        repository.insertListA((List<ListATransaction>) chunk.getItems());
    }
}