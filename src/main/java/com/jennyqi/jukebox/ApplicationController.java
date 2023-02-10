package com.jennyqi.jukebox;

import com.jennyqi.jukebox.models.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationController {

  private final ApplicationService applicationService;

  @Autowired
  public ApplicationController(ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  @GetMapping("/{settingId}")
  public List<Jukebox> getJukeboxesSupportingSetting(@PathVariable("settingId") String settingId) {
    Setting requestedSetting = applicationService.fetchRequestedSetting(settingId);
    List<Jukebox> allJukeboxes = applicationService.fetchJukeboxes();
    return applicationService.selectJukeboxes(requestedSetting, allJukeboxes);
  }

}
