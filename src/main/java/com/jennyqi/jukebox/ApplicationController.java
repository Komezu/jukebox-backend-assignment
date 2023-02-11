package com.jennyqi.jukebox;

import com.jennyqi.jukebox.models.*;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationController {

  private final ApplicationService applicationService;

  public ApplicationController(ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  @GetMapping("/v1/jukeboxes")
  public ResponseEntity<?> getJukeboxesFromSetting(@RequestParam String settingId,
                                                   @RequestParam(required = false) String model,
                                                   @RequestParam(required = false, defaultValue = "0") int offset,
                                                   @RequestParam(required = false, defaultValue = "10") int limit)
                                                   throws MockedApiCallException {
    if (settingId.isEmpty()) {
      throw new MockedApiCallException(400, "Bad Request - Missing settingId");
    }

    Setting requestedSetting = applicationService.fetchRequestedSetting(settingId);

    List<Jukebox> jukeboxes;
    if (model == null || model.isEmpty()) {
      jukeboxes = applicationService.fetchJukeboxes();
    } else {
      jukeboxes = applicationService.fetchJukeboxes(model);
    }

    List<Jukebox> selectedJukeboxes = applicationService.selectJukeboxes(requestedSetting, jukeboxes);
    List<Jukebox> jukeboxesInRange = applicationService.selectJukeboxesInRange(selectedJukeboxes, offset, limit);

    PaginatedResponse<Jukebox> response = new PaginatedResponse<>(
      jukeboxesInRange, selectedJukeboxes.size(), (int) Math.ceil(offset / limit) + 1, !(offset % limit == 0), limit
    );

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

}
