package com.jennyqi.jukebox;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.jennyqi.jukebox.model.*;

@RestController
public class ApplicationController {

  private final ApplicationService applicationService;

  public ApplicationController(ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  @GetMapping("/v1/jukeboxes")
  public ResponseEntity<PaginatedResponse<Jukebox>> getJukeboxesSupportingSetting(
                                                    @RequestParam String settingId,
                                                    @RequestParam(required = false) String model,
                                                    @RequestParam(required = false, defaultValue = "0") int offset,
                                                    @RequestParam(required = false, defaultValue = "10") int limit)
                                                    throws MissingServletRequestParameterException, MockedApiCallException {
    if (settingId.isEmpty()) {
      throw new MissingServletRequestParameterException("settingId", "String");
    }

    Setting setting = applicationService.fetchRequestedSetting(settingId);

    List<Jukebox> jukeboxes;
    if (model == null || model.isEmpty()) {
      jukeboxes = applicationService.fetchJukeboxes();
    } else {
      jukeboxes = applicationService.fetchJukeboxes(model);
    }

    PaginatedResponse<Jukebox> response = applicationService.findCompatibleJukeboxes(setting, jukeboxes, offset, limit);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

}
