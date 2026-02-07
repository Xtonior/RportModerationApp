package kz.lab.moderationapp.model;

import java.util.List;

public record ClientDataDto(String clientId, List<String> activeCategories) {

}
