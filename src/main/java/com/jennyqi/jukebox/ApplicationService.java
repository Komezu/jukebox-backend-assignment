package com.jennyqi.jukebox;

import com.jennyqi.jukebox.models.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApplicationService {

  private final RestTemplate restTemplate;

  @Autowired
  public ApplicationService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public Setting findRequestedSetting(String id) {
    List<Setting> allSettings = fetchSettings();
    for (Setting setting : allSettings) {
      if (setting.id().equals(id))
        return setting;
    }
    return null;
  }

  private List<Setting> fetchSettings() {
    String url = "http://my-json-server.typicode.com/touchtunes/tech-assignment/settings";
    SettingsWrapper wrapper = restTemplate.getForObject(url, SettingsWrapper.class);
    return wrapper == null ? null : wrapper.settings();
  }

}
