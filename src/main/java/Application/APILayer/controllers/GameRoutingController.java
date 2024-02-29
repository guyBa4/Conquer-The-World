package Application.APILayer.controllers;

import Application.Repositories.RepositoryFactory;
import Application.ServiceLayer.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.WebSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "game-router")
public class GameRoutingController {
    TokenHandler tokenHandler;
    Map<UUID, Map<UUID, WebSocket>> socketMap;

    @Autowired
    public GameRoutingController()
    {
        this.socketMap = new HashMap<>();
        tokenHandler = TokenHandler.getInstance();
    }


}
