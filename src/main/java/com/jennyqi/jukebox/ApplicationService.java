package com.jennyqi.jukebox;

import com.jennyqi.jukebox.models.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ApplicationService {

  private final RestTemplate restTemplate;

  public ApplicationService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public DataResponse<?> fetchRequestedSetting(String settingId) {
    String url = "http://my-json-server.typicode.com/touchtunes/tech-assignment/settings";

    try {
      ResponseEntity<SettingsWrapper> response = restTemplate.getForEntity(url, SettingsWrapper.class);
      SettingsWrapper wrapper = response.getBody();

      if (wrapper == null) {
        ErrorResponse error = new ErrorResponse(502, "Bad Gateway - Unable to fetch settings");
        return new DataResponse<>(null, error);
      }

      return findSettingById(settingId, wrapper.settings());
    }
    catch(HttpClientErrorException e) {
      ErrorResponse error = new ErrorResponse(500, "Internal Server Error - Unable to fetch settings");
      return new DataResponse<>(null, error);
    }
    catch(HttpServerErrorException e) {
      ErrorResponse error = new ErrorResponse(502, "Bad Gateway - Unable to fetch settings");
      return new DataResponse<>(null, error);
    }
  }

  private DataResponse<?> findSettingById(String id, List<Setting> settings) {
    for (Setting setting: settings) {
      if (setting.id().equals(id)) {
        return new DataResponse<Setting>(setting, null);
      }
    }
    ErrorResponse error = new ErrorResponse(404, "Requested Setting Not Found");
    return new DataResponse<>(null, error);
  }

  public List<Jukebox> fetchJukeboxes() {
    String url = "https://my-json-server.typicode.com/touchtunes/tech-assignment/jukes";
    ResponseEntity<Jukebox[]> response = restTemplate.getForEntity(url, Jukebox[].class);
    return Arrays.asList(response.getBody());
  }

  public List<Jukebox> fetchJukeboxes(String model) {
    String url = "https://my-json-server.typicode.com/touchtunes/tech-assignment/jukes";
    ResponseEntity<Jukebox[]> response = restTemplate.getForEntity(url, Jukebox[].class);
    Jukebox[] allJukeboxes = response.getBody();

    if (allJukeboxes != null) {
      List<Jukebox> jukeboxesOfModel = new ArrayList<>();
      for (Jukebox jukebox : allJukeboxes) {
        if (jukebox.model().equalsIgnoreCase(model)) {
          jukeboxesOfModel.add(jukebox);
        }
      }
      return jukeboxesOfModel;
    }

    return null;
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

  public List<Jukebox> selectJukeboxesInRange(List<Jukebox> jukeboxes, int offset, int limit) {
    int end = offset + limit > jukeboxes.size() ? jukeboxes.size() : offset + limit;
    return jukeboxes.subList(offset, end);
  }

}
