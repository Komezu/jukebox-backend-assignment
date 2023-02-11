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
  private final String settingsUrl = "http://my-json-server.typicode.com/touchtunes/tech-assignment/settings";
  private final String jukeboxesUrl = "https://my-json-server.typicode.com/touchtunes/tech-assignment/jukes";

  public ApplicationService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public Setting fetchRequestedSetting(String settingId) throws MockedApiCallException {
    try {
      ResponseEntity<SettingsWrapper> response = restTemplate.getForEntity(settingsUrl, SettingsWrapper.class);
      SettingsWrapper wrapper = response.getBody();
      if (wrapper == null || wrapper.settings().isEmpty()) {
        throw new MockedApiCallException(502, "Bad Gateway - Unable to fetch settings");
      }
      return findSettingById(settingId, wrapper.settings());
    }
    catch(HttpClientErrorException e) {
      throw new MockedApiCallException(500, "Internal Server Error - Unable to fetch settings");
    }
    catch(HttpServerErrorException e) {
      throw new MockedApiCallException(502, "Bad Gateway - Unable to fetch settings");
    }
  }

  private Setting findSettingById(String id, List<Setting> settings) throws MockedApiCallException {
    for (Setting setting: settings) {
      if (setting.id().equals(id)) {
        return setting;
      }
    }

    throw new MockedApiCallException(404, "Requested Setting Not Found");
  }

  public List<Jukebox> fetchJukeboxes() throws MockedApiCallException {
    try {
      ResponseEntity<Jukebox[]> response = restTemplate.getForEntity(jukeboxesUrl, Jukebox[].class);
      Jukebox[] jukeboxes = response.getBody();
      if (jukeboxes == null || jukeboxes.length == 0) {
        throw new MockedApiCallException(502, "Bad Gateway - Unable to fetch jukeboxes");
      }
      return Arrays.asList(jukeboxes);
    }
    catch(HttpClientErrorException e) {
      throw new MockedApiCallException(500, "Internal Server Error - Unable to fetch jukeboxes");
    }
    catch(HttpServerErrorException e) {
      throw new MockedApiCallException(502, "Bad Gateway - Unable to fetch jukeboxes");
    }
  }

  public List<Jukebox> fetchJukeboxes(String model) throws MockedApiCallException {
    List<Jukebox> allJukeboxes = fetchJukeboxes();
    List<Jukebox> jukeboxesOfModel = new ArrayList<>();

    for (Jukebox jukebox : allJukeboxes) {
      if (jukebox.model().equalsIgnoreCase(model)) {
        jukeboxesOfModel.add(jukebox);
      }
    }

    return jukeboxesOfModel;
  }

  public List<Jukebox> selectJukeboxes(Setting setting, List<Jukebox> jukeboxes) {
    List<String> requiredComponents = setting.requires();
    List<Jukebox> selectedJukeboxes = new ArrayList<>();

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
    if (offset >= jukeboxes.size()) {
      return new ArrayList<>();
    }

    int end = offset + limit > jukeboxes.size() ? jukeboxes.size() : offset + limit;
    return jukeboxes.subList(offset, end);
  }

}
