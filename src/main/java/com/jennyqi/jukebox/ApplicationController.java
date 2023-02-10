package com.jennyqi.jukebox;

import com.jennyqi.jukebox.models.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationController {

  private final ApplicationService applicationService;

  @Autowired
  public ApplicationController(ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  @GetMapping("/v1/jukeboxes")
  public List<Jukebox> getJukeboxesFromSetting(@RequestParam String settingId,
                                               @RequestParam(required = false) String model,
                                               @RequestParam(required = false, defaultValue = "0") int offset,
                                               @RequestParam(required = false, defaultValue = "10") int limit) {
    Setting requestedSetting = applicationService.fetchRequestedSetting(settingId);

    List<Jukebox> jukeboxes;
    if (model != null) {
      jukeboxes = applicationService.fetchJukeboxes(model);
    } else {
      jukeboxes = applicationService.fetchJukeboxes();
    }

    List<Jukebox> selectedJukeboxes = applicationService.selectJukeboxes(requestedSetting, jukeboxes);

    return applicationService.selectJukeboxesInRange(selectedJukeboxes, offset, limit);
  }

}
