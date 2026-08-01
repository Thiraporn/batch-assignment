package com.cp.assignment.miniproject.batch.process;

import com.cp.assignment.miniproject.batch.model.ListATransaction;
import com.cp.assignment.miniproject.batch.service.ListACacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListACacheProcessor implements ItemProcessor<ListATransaction, ListATransaction> {

    private final ListACacheService cacheService;
    //read List A  1 record ,then keep that record in Cache
    @Override
    public ListATransaction process(ListATransaction item) {
        // 1. add item HashMap
        cacheService.put(item);
        // 2. prepare data for DB (if need to save data into data source)
        return item;
    }
}