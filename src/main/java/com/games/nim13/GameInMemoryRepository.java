package com.games.nim13;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.springframework.util.StringUtils.isEmpty;
import static org.springframework.util.StringUtils.trimWhitespace;

@Repository
public class GameInMemoryRepository {
    private final Map<String, Game> data = new ConcurrentHashMap<>();


    public Optional<Game> findById(String id) {
        if (id == null || isEmpty(trimWhitespace(id))) {
            empty();
        }

        Game game = data.get(id);
        if (game == null) {
            return empty();
        }

        return of(game);
    }

    public void save(Game game) {
        data.put(game.id(), game);
    }


}
