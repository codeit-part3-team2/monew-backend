package com.monew.monew_batch.article.job;

import com.monew.monew_api.interest.entity.Interest;
import com.monew.monew_api.interest.entity.Keyword;
import com.monew.monew_api.interest.repository.InterestRepository;
import com.monew.monew_api.interest.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@StepScope
@RequiredArgsConstructor
@Slf4j
public class NaverNewsItemReader implements ItemReader<Keyword> {

    private final KeywordRepository keywordRepository;
    private List<Keyword> items;
    private int nextIndex = 0;

    @Override
    public synchronized Keyword read() {
        if (items == null) {
            items = keywordRepository.findAll();
            log.info("키워드 {}개 로드 완료", items.size());
        }

        if (nextIndex < items.size()) {
            return items.get(nextIndex++);
        } else {
            return null;
        }
    }

}
