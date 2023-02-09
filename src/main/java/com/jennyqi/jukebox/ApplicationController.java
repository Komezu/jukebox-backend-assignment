package com.jennyqi.jukebox;

import com.jennyqi.jukebox.models.*;
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

  @GetMapping("/settings/{id}")
  public Setting getSetting(@PathVariable("id") String id) {
    return applicationService.findRequestedSetting(id);
  }

}
