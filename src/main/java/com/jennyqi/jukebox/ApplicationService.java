package com.jennyqi.jukebox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.jennyqi.jukebox.exception.MockedApiCallException;
import com.jennyqi.jukebox.model.*;
import com.jennyqi.jukebox.response.PaginatedResponse;

@Service
public class ApplicationService {

  private final RestTemplate restTemplate;
  private final String settingsUrl = "http://my-json-server.typicode.com/touchtunes/tech-assignment/settings";
  private final String jukeboxesUrl = "https://my-json-server.typicode.com/touchtunes/tech-assignment/jukes";
  private final int defaultOffset = 0;
  private final int defaultLimit = 10;

  public ApplicationService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  // Call mocked API to retrieve all settings, pass them to filter method, and return requested one
  public Setting fetchRequestedSetting(String settingId) {
    try {
      // Returned JSON will have all settings in array mapped to "settings" key
      // Map this response format to a wrapper class to retrieve settings after
      ResponseEntity<SettingsWrapper> response = restTemplate.getForEntity(settingsUrl, SettingsWrapper.class);
      SettingsWrapper wrapper = response.getBody();

      // Response should have settings, throw exception if doesn't even if no error status
      if (wrapper == null || wrapper.settings().isEmpty()) {
        throw new MockedApiCallException(502, "Bad Gateway - Unable to fetch settings");
      }

      return findSettingById(settingId, wrapper.settings());
    }
    catch(HttpClientErrorException ex) {
      // Turn 4xx errors into 500 error (something wrong on our side)
      throw new MockedApiCallException(500, "Internal Server Error - Unable to fetch settings");
    }
    catch(HttpServerErrorException ex) {
      // Turn 5xx errors into 502 error (something wrong with mocked API)
      throw new MockedApiCallException(502, "Bad Gateway - Unable to fetch settings");
    }
  }

  // Filter provided settings and find the one with id matching requested setting's id
  private Setting findSettingById(String id, List<Setting> settings) {
    for (Setting setting: settings) {
      if (setting.id().equals(id)) {
        return setting;
      }
    }
    throw new MockedApiCallException(404, "Not Found - Invalid setting");
  }

  // Call mocked API to retrieve and return all jukeboxes
  public List<Jukebox> fetchJukeboxes() {
    try {
      // Returned JSON will have all jukeboxes in an array
      ResponseEntity<Jukebox[]> response = restTemplate.getForEntity(jukeboxesUrl, Jukebox[].class);
      Jukebox[] jukeboxes = response.getBody();

      // Response should have jukeboxes, throw exception if doesn't even if no error status
      if (jukeboxes == null || jukeboxes.length == 0) {
        throw new MockedApiCallException(502, "Bad Gateway - Unable to fetch jukeboxes");
      }

      return Arrays.asList(jukeboxes);
    }
    catch(HttpClientErrorException ex) {
      // Turn 4xx errors into 500 error (something wrong on our side)
      throw new MockedApiCallException(500, "Internal Server Error - Unable to fetch jukeboxes");
    }
    catch(HttpServerErrorException ex) {
      // Turn 5xx errors into 502 error (something wrong with mocked API)
      throw new MockedApiCallException(502, "Bad Gateway - Unable to fetch jukeboxes");
    }
  }

  // Retrieve all jukeboxes, filter by model, and return ones of requested model
  public List<Jukebox> fetchJukeboxes(String model) {
    List<Jukebox> allJukeboxes = fetchJukeboxes();
    List<Jukebox> jukeboxesOfModel = new ArrayList<>();

    for (Jukebox jukebox : allJukeboxes) {
      if (jukebox.model().equalsIgnoreCase(model)) {
        jukeboxesOfModel.add(jukebox);
      }
    }
    return jukeboxesOfModel;
  }

  // Find jukeboxes supporting requested setting, and return requested section with pagination information
  public PaginatedResponse<Jukebox> findCompatibleJukeboxes(Setting setting, List<Jukebox> jukeboxes, int offset, int limit) {
    List<String> requiredComponents = setting.requires();
    List<Jukebox> compatibleJukeboxes = new ArrayList<>();

    for (Jukebox jukebox : jukeboxes) {
      List<String> jukeboxComponents = new ArrayList<>();

      // Extract the names of the jukebox's components into a list
      for (Component component : jukebox.components()) {
        jukeboxComponents.add(component.name());
      }

      // Jukebox is compatible if its components include all of requested setting's components
      // NOTE: Duplicate components for both setting and jukeboxes are ignored
      if (jukeboxComponents.containsAll(requiredComponents)) {
        compatibleJukeboxes.add(jukebox);
      }
    }

    // If offset or limit is not a valid value, set it to default value
    offset = offset < 0 ? defaultOffset : offset;
    limit = limit <= 0 ? defaultLimit : limit;

    List<Jukebox> selectedJukeboxes = selectJukeboxesInRange(compatibleJukeboxes, offset, limit);
    return formatPaginatedResponse(selectedJukeboxes, compatibleJukeboxes.size(), offset, limit);
  }

  // Select the section of jukebox list requested using offset and limit
  private List<Jukebox> selectJukeboxesInRange(List<Jukebox> jukeboxes, int offset, int limit) {
    // Return empty results if trying to access jukeboxes beyond length of list
    if (offset >= jukeboxes.size()) {
      return new ArrayList<>();
    }

    // Return until end of list if requesting more results than are available
    int end = offset + limit > jukeboxes.size() ? jukeboxes.size() : offset + limit;
    return jukeboxes.subList(offset, end);
  }

  // Add pagination information to the compatible jukebox list response
  private PaginatedResponse<Jukebox> formatPaginatedResponse(List<Jukebox> selectedJukeboxes,
                                                             int totalCount, int offset, int limit) {
    // Divide number of results skipped (offset) by number of results per page (limit) to get pages skipped
    // An incomplete page (division with remainder) counts as one page (using ceil)
    int currentPage = (int) Math.ceil((double) offset / limit) + 1;
    // If there was an incomplete page skipped, then current page number is estimated instead of exact
    boolean pageNumberEstimated = !(offset % limit == 0);

    return new PaginatedResponse<>(selectedJukeboxes, totalCount, currentPage, pageNumberEstimated, limit);
  }

}
