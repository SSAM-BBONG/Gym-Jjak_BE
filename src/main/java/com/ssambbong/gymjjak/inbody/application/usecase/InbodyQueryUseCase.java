package com.ssambbong.gymjjak.inbody.application.usecase;

import com.ssambbong.gymjjak.inbody.application.query.GetInbodyListQuery;
import com.ssambbong.gymjjak.inbody.application.result.InbodyListResult;

public interface InbodyQueryUseCase {

    InbodyListResult getInbodyList(GetInbodyListQuery query);
}
