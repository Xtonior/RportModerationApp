package kz.lab.redisapp.model;

import java.util.List;

public record ClientData(String clientId, List<String> activeCategories ) {
}