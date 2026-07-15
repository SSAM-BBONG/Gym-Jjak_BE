package com.ssambbong.gymjjak.inbody.domain.repository;

import com.ssambbong.gymjjak.inbody.domain.model.Inbody;

import java.util.List;

public record InbodySlice(
        // 화면에 보여줄 인바디 목록
        List<Inbody> inbodies,
        // 이후 존재 여부
        boolean hasNext
) {
}
