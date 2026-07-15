package com.ssambbong.gymjjak.inbody.application.command;

public record DeleteInbodyCommand(
        Long userId,
        Long inbodyId
) {
}
