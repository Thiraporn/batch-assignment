package com.cp.assignment.miniproject.batch.writer;

import com.cp.assignment.miniproject.batch.model.ListBTransaction;
import com.cp.assignment.miniproject.repository.ReconciliationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListBWriter implements ItemWriter<ListBTransaction> {

    private final ReconciliationRepository repository;

    @Override
    public void write(Chunk<? extends ListBTransaction> chunk) {
        //call insert new
        repository.insertListB((List<ListBTransaction>) chunk.getItems());
    }
}