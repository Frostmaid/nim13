package com.games.nim13;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class GameInMemoryRepository {
    private final Map<String, Game> data = new ConcurrentHashMap<>();

    public Game getById(String id) {
        return data.get(id);
    }

    public void save(Game game) {
        data.put(game.id(), game);
    }


}
