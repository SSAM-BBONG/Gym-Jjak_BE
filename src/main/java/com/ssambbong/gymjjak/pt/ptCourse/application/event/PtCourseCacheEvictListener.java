package com.ssambbong.gymjjak.pt.ptCourse.application.event;

import com.ssambbong.gymjjak.pt.ptCourse.domain.event.PtCourseListCacheEvictEvent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PtCourseCacheEvictListener {

    @CacheEvict(value = "ptCourseList", allEntries = true)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void evictPtCourseListCache(PtCourseListCacheEvictEvent event) {}
}
