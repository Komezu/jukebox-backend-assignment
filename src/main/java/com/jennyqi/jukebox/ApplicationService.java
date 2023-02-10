package com.jennyqi.jukebox;

import com.jennyqi.jukebox.models.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApplicationService {

  private final RestTemplate restTemplate;

  @Autowired
  public ApplicationService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public Setting fetchRequestedSetting(String id) {
    String url = "http://my-json-server.typicode.com/touchtunes/tech-assignment/settings";
    ResponseEntity<SettingsWrapper> response = restTemplate.getForEntity(url, SettingsWrapper.class);
    SettingsWrapper wrapper = response.getBody();
    if (wrapper != null) {
      for (Setting setting: wrapper.settings()) {
        if (setting.id().equals(id)) {
          return setting;
        }
      }
    }
    return null;
  }

  public List<Jukebox> fetchJukeboxes() {
    String url = "https://my-json-server.typicode.com/touchtunes/tech-assignment/jukes";
    ResponseEntity<Jukebox[]> response = restTemplate.getForEntity(url, Jukebox[].class);
    return Arrays.asList(response.getBody());
  }

  public List<Jukebox> selectJukeboxes(Setting setting, List<Jukebox> jukeboxes) {
    List<Jukebox> selectedJukeboxes = new ArrayList<>();
    List<String> requiredComponents = setting.requires();

    for (Jukebox jukebox : jukeboxes) {
      List<String> jukeboxComponents = new ArrayList<>();
      for (Component component : jukebox.components()) {
        jukeboxComponents.add(component.name());
      }
      if (jukeboxComponents.containsAll(requiredComponents)) {
        selectedJukeboxes.add(jukebox);
      }
    }

    return selectedJukeboxes;
  }

}
