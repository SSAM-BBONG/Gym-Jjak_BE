package com.ssambbong.gymjjak.inbody.presentation.api.mapper;

import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import com.ssambbong.gymjjak.inbody.application.result.CreateInbodyResult;
import com.ssambbong.gymjjak.inbody.application.result.InbodyItemResult;
import com.ssambbong.gymjjak.inbody.application.result.InbodyListResult;
import com.ssambbong.gymjjak.inbody.presentation.api.response.CreateInbodyResponse;
import com.ssambbong.gymjjak.inbody.presentation.api.response.InbodyListResponse;
import com.ssambbong.gymjjak.inbody.presentation.api.response.InbodyItemResponse;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface InbodyResponseMapper {

    CreateInbodyResponse toCreateResponse(CreateInbodyResult result);

    InbodyItemResponse toInbodyItemResponse(InbodyItemResult result);

    InbodyListResponse toInbodyListResponse(InbodyListResult result);
}
