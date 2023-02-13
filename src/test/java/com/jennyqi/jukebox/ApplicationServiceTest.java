package com.jennyqi.jukebox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.jennyqi.jukebox.exception.MockedApiCallException;
import com.jennyqi.jukebox.model.*;
import com.jennyqi.jukebox.response.PaginatedResponse;

// Tests with mocked RestTemplate behavior through Mockito (no actual calls to mocked APIs)

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private ApplicationService applicationService;

	@Test
	void testFetchRequestedSetting() {
    // Arrange
    List<Setting> settings = List.of(
      new Setting("abc123", List.of("camera")),
      new Setting("def456", List.of("touchscreen", "money_pcb"))
    );
    SettingsWrapper wrapper = new SettingsWrapper(settings);

    Mockito.when(restTemplate.getForEntity(any(String.class), eq(SettingsWrapper.class)))
      .thenReturn(new ResponseEntity<>(wrapper, HttpStatus.OK));

    // Act
    Setting result = applicationService.fetchRequestedSetting("def456");

    // Assert
    assertEquals("def456", result.id());
    assertEquals(List.of("touchscreen", "money_pcb"), result.requires());
	}

  @Test
	void testFetchRequestedSettingRetrievesNoSettings() {
    // Arrange
    List<Setting> settings = new ArrayList<>();
    SettingsWrapper wrapper = new SettingsWrapper(settings);

    Mockito.when(restTemplate.getForEntity(any(String.class), eq(SettingsWrapper.class)))
      .thenReturn(new ResponseEntity<>(wrapper, HttpStatus.OK));

    // Act & Assert
    MockedApiCallException ex = assertThrows(MockedApiCallException.class, () -> {
      applicationService.fetchRequestedSetting("abc123");
    });
    assertEquals(502, ex.getStatus());
	}

  @Test
  void testFetchRequestedSettingDoesNotFindSetting() {
    // Arrange
    List<Setting> settings = List.of(
      new Setting("abc123", List.of("camera")),
      new Setting("def456", List.of("touchscreen", "money_pcb"))
    );
    SettingsWrapper wrapper = new SettingsWrapper(settings);

    Mockito.when(restTemplate.getForEntity(any(String.class), eq(SettingsWrapper.class)))
      .thenReturn(new ResponseEntity<>(wrapper, HttpStatus.OK));

    // Act & Assert
    MockedApiCallException ex = assertThrows(MockedApiCallException.class, () -> {
      applicationService.fetchRequestedSetting("ghi789");
    });
    assertEquals(404, ex.getStatus());
  }

  @Test
  void testFetchJukeboxes() {
    // Arrange
    Jukebox[] jukeboxes = {
      new Jukebox("123abc", "angelina", List.of(new Component("camera"))),
      new Jukebox("456def", "fusion", List.of(new Component("pcb"), new Component("money_storage"))),
      new Jukebox("789ghi", "virtuo", List.of(new Component("speaker"), new Component("amplifier")))
    };

    Mockito.when(restTemplate.getForEntity(any(String.class), eq(Jukebox[].class)))
      .thenReturn(new ResponseEntity<>(jukeboxes, HttpStatus.OK));

    // Act
    List<Jukebox> result = applicationService.fetchJukeboxes();

    // Assert
    assertEquals(Arrays.asList(jukeboxes), result);
  }

  @Test
  void testFetchJukeboxesWithModel() {
    // Arrange
    Jukebox jukebox1 = new Jukebox("123abc", "angelina", List.of(new Component("camera")));
    Jukebox jukebox2 = new Jukebox("456def", "fusion", List.of(new Component("pcb"), new Component("money_storage")));
    Jukebox jukebox3 = new Jukebox("789ghi", "virtuo", List.of(new Component("speaker"), new Component("amplifier")));
    Jukebox[] jukeboxes = {jukebox1, jukebox2, jukebox3};

    Mockito.when(restTemplate.getForEntity(any(String.class), eq(Jukebox[].class)))
      .thenReturn(new ResponseEntity<>(jukeboxes, HttpStatus.OK));

    // Act
    List<Jukebox> result = applicationService.fetchJukeboxes("virtuo");

    // Assert
    assertEquals(List.of(jukebox3), result);
  }

  @Test
  void testFetchJukeboxesRetrievesNoJukeboxes() {
    // Arrange
    Jukebox[] jukeboxes = new Jukebox[0];

    Mockito.when(restTemplate.getForEntity(any(String.class), eq(Jukebox[].class)))
      .thenReturn(new ResponseEntity<>(jukeboxes, HttpStatus.OK));

    // Act & Assert
    MockedApiCallException ex = assertThrows(MockedApiCallException.class, () -> {
      applicationService.fetchJukeboxes();
    });
    assertEquals(502, ex.getStatus());
  }

  @Test
	void testFetchMethodThrowsHttpClientErrorException() {
    // Arrange
    Mockito.when(restTemplate.getForEntity(any(String.class), any()))
      .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

    // Act & Assert
    MockedApiCallException settingsEx = assertThrows(MockedApiCallException.class, () -> {
      applicationService.fetchRequestedSetting("abc123");
    });
    assertEquals(500, settingsEx.getStatus());

    MockedApiCallException jukeboxesEx = assertThrows(MockedApiCallException.class, () -> {
      applicationService.fetchJukeboxes();
    });
    assertEquals(500, jukeboxesEx.getStatus());
	}

  @Test
	void testFetchMethodThrowsHttpServerErrorException() {
    // Arrange
    Mockito.when(restTemplate.getForEntity(any(String.class), any()))
      .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

    // Act & Assert
    MockedApiCallException settingsEx = assertThrows(MockedApiCallException.class, () -> {
      applicationService.fetchRequestedSetting("abc123");
    });
    assertEquals(502, settingsEx.getStatus());

    MockedApiCallException jukeboxesEx = assertThrows(MockedApiCallException.class, () -> {
      applicationService.fetchJukeboxes();
    });
    assertEquals(502, jukeboxesEx.getStatus());
	}

  @Test
  void testFindCompatibleJukeboxes() {
    // Arrange
    Setting setting = new Setting("abc123", List.of("amplifier", "speaker"));
    Jukebox jukebox1 = new Jukebox("123abc", "angelina", List.of(new Component("camera")));
    Jukebox jukebox2 = new Jukebox("456def", "fusion", List.of(new Component("pcb"), new Component("money_storage")));
    Jukebox jukebox3 = new Jukebox("789ghi", "virtuo", List.of(new Component("speaker"), new Component("amplifier")));
    Jukebox jukebox4 = new Jukebox("012jkl", "virtuo", List.of(new Component("led_matrix")));
    Jukebox jukebox5 = new Jukebox("345mno", "fusion", List.of(new Component("touchscreen"), new Component("amplifier"), new Component("speaker")));
    List<Jukebox> jukeboxes = List.of(jukebox1, jukebox2, jukebox3, jukebox4, jukebox5);
    int offset = 0; // Default value
    int limit = 10; // Default value

    // Act
    PaginatedResponse<Jukebox> result = applicationService.findCompatibleJukeboxes(setting, jukeboxes, offset, limit);

    // Assert
    PaginatedResponse<Jukebox> expectedResult = new PaginatedResponse<>(List.of(jukebox3, jukebox5), 2, 1, false, limit);
    assertEquals(expectedResult, result);
  }

  @Test
  void testFindCompatibleJukeboxesWithUserSpecifiedOffsetAndLimit() {
    // Arrange
    Setting setting = new Setting("abc123", List.of("camera"));
    Jukebox jukebox1 = new Jukebox("123abc", "angelina", List.of(new Component("camera")));
    Jukebox jukebox2 = new Jukebox("456def", "fusion", List.of(new Component("camera"), new Component("money_storage")));
    Jukebox jukebox3 = new Jukebox("789ghi", "virtuo", List.of(new Component("speaker"), new Component("camera")));
    Jukebox jukebox4 = new Jukebox("012jkl", "virtuo", List.of(new Component("led_matrix")));
    Jukebox jukebox5 = new Jukebox("345mno", "fusion", List.of(new Component("touchscreen"), new Component("amplifier"), new Component("camera")));
    List<Jukebox> jukeboxes = List.of(jukebox1, jukebox2, jukebox3, jukebox4, jukebox5);
    int offset = 1;
    int limit = 2;

    // Act
    PaginatedResponse<Jukebox> result = applicationService.findCompatibleJukeboxes(setting, jukeboxes, offset, limit);

    // Assert
    PaginatedResponse<Jukebox> expectedResult = new PaginatedResponse<>(List.of(jukebox2, jukebox3), 4, 2, true, limit);
    assertEquals(expectedResult, result);
  }

  @Test
  void testFindCompatibleJukeboxesDoesNotFindResults() {
    // Arrange
    Setting setting = new Setting("abc123", List.of("led_panel"));
    Jukebox jukebox1 = new Jukebox("123abc", "angelina", List.of(new Component("camera")));
    Jukebox jukebox2 = new Jukebox("456def", "fusion", List.of(new Component("pcb"), new Component("money_storage")));
    Jukebox jukebox3 = new Jukebox("789ghi", "virtuo", List.of(new Component("speaker"), new Component("amplifier")));
    Jukebox jukebox4 = new Jukebox("012jkl", "virtuo", List.of(new Component("led_matrix")));
    Jukebox jukebox5 = new Jukebox("345mno", "fusion", List.of(new Component("touchscreen"), new Component("amplifier"), new Component("speaker")));
    List<Jukebox> jukeboxes = List.of(jukebox1, jukebox2, jukebox3, jukebox4, jukebox5);
    int offset = 0; // Default value
    int limit = 10; // Default value

    // Act
    PaginatedResponse<Jukebox> result = applicationService.findCompatibleJukeboxes(setting, jukeboxes, offset, limit);

    // Assert
    PaginatedResponse<Jukebox> expectedResult = new PaginatedResponse<>(new ArrayList<>(), 0, 1, false, limit);
    assertEquals(expectedResult, result);
  }

}
