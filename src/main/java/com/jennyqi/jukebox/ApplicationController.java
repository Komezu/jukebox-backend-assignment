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

  @GetMapping("/jukeboxes/v1")
  public List<Jukebox> getJukeboxesFromSetting(@RequestParam String settingId,
                                               @RequestParam(required = false) String model) {
    Setting requestedSetting = applicationService.fetchRequestedSetting(settingId);
    List<Jukebox> jukeboxes;

    if (model != null) {
      jukeboxes = applicationService.fetchJukeboxes(model);
    } else {
      jukeboxes = applicationService.fetchJukeboxes();
    }

    return applicationService.selectJukeboxes(requestedSetting, jukeboxes);
  }

}
