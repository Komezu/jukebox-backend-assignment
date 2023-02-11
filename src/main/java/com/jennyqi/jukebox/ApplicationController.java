package com.jennyqi.jukebox;

import com.jennyqi.jukebox.models.*;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
  public ResponseEntity<?> getJukeboxesFromSetting(
                                                    @RequestParam String settingId,
                                                    @RequestParam(required = false) String model,
                                                    @RequestParam(required = false, defaultValue = "0") int offset,
                                                    @RequestParam(required = false, defaultValue = "10") int limit) {
    if (settingId == null || settingId.isEmpty()) {
      ErrorResponse error = new ErrorResponse(400, "Bad Request - Missing settingId");
      return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    DataResponse<?> settingResponse = applicationService.fetchRequestedSetting(settingId);
    if (settingResponse.error() != null) {
      int statusCode = settingResponse.error().status();
      return new ResponseEntity<>(settingResponse.error(), HttpStatus.valueOf(statusCode));
    }
    Setting requestedSetting = (Setting) settingResponse.data();

    List<Jukebox> jukeboxes;
    if (model != null) {
      jukeboxes = applicationService.fetchJukeboxes(model);
    } else {
      jukeboxes = applicationService.fetchJukeboxes();
    }

    List<Jukebox> selectedJukeboxes = applicationService.selectJukeboxes(requestedSetting, jukeboxes);
    List<Jukebox> jukeboxesInRange = applicationService.selectJukeboxesInRange(selectedJukeboxes, offset, limit);

    PaginatedResponse<Jukebox> response = new PaginatedResponse<>(
      jukeboxesInRange, selectedJukeboxes.size(), (int) Math.ceil(offset / limit) + 1, !(offset % limit == 0), limit
    );

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingRequestParamException(MissingServletRequestParameterException ex) {
    ErrorResponse error = new ErrorResponse(400, "Bad Request - Missing " + ex.getParameterName());
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

}
