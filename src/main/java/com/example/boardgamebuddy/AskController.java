package com.example.boardgamebuddy;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
public class AskController {

    private final BoardGameService boardGameService;

    public AskController(BoardGameService bgs) {
        this.boardGameService = bgs;
    }

    @PostMapping(path ="/ask", produces="application/json")
    public Answer ask(@RequestBody @Valid Question question) {
        return boardGameService.askQuestion(question);
    }

}