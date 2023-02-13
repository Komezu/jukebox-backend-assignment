package com.jennyqi.jukebox;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationControllerTest {

  @Autowired
  private MockMvc mockMvc;

	@Test
	void testGetJukeboxesSupportingSetting() throws Exception {
    mockMvc.perform(get("/v1/jukeboxes?settingId=515ef38b-0529-418f-a93a-7f2347fc5805"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.data").isArray())
      .andExpect(jsonPath("$.data[0].id").isString())
      .andExpect(jsonPath("$.data.length()").value(6))
      .andExpect(jsonPath("$.totalCount").value(6));
	}

  @Test
  void testGetJukeboxesSupportingSettingWithoutSettingId() throws Exception {
    mockMvc.perform(get("/v1/jukeboxes"))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(content().json("{ 'status': 400, 'message': 'Bad Request - Missing settingId' }"));
  }

  @Test
  void testGetJukeboxesSupportingSettingWithEmptySettingId() throws Exception {
    mockMvc.perform(get("/v1/jukeboxes?settingId="))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(content().json("{ 'status': 400, 'message': 'Bad Request - Missing settingId' }"));
  }

  @Test
  void testGetJukeboxesSupportingSettingWithInvalidSettingId() throws Exception {
    mockMvc.perform(get("/v1/jukeboxes?settingId=abc123"))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(content().json("{ 'status': 404, 'message': 'Not Found - Invalid setting' }"));
  }

  @Test
  void testGetJukeboxesSupportingSettingWithModel() throws Exception {
    mockMvc.perform(get("/v1/jukeboxes?settingId=515ef38b-0529-418f-a93a-7f2347fc5805&model=angelina"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.data[0].model").value("angelina"))
      .andExpect(jsonPath("$.totalCount").value(2));
  }

  @Test
  void testGetJukeboxesSupportingSettingWithInvalidModel() throws Exception {
    mockMvc.perform(get("/v1/jukeboxes?settingId=aae445bf-72f0-4680-a23e-18fcf7241f8b&model=wrongmodel"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.data").isEmpty())
      .andExpect(jsonPath("$.totalCount").value(0));
  }

  @Test
  void testGetJukeboxesSupportingSettingWithOffset() throws Exception {
    mockMvc.perform(get("/v1/jukeboxes?settingId=515ef38b-0529-418f-a93a-7f2347fc5805&offset=3"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.data.length()").value(3))
      .andExpect(jsonPath("$.totalCount").value(6))
      .andExpect(jsonPath("$.currentPage").value(2))
      .andExpect(jsonPath("$.pageNumberEstimated").value(true));
  }

  @Test
  void testGetJukeboxesSupportingSettingWithOffsetAndLimit() throws Exception {
    mockMvc.perform(get("/v1/jukeboxes?settingId=515ef38b-0529-418f-a93a-7f2347fc5805&offset=3&limit=2"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.data.length()").value(2))
      .andExpect(jsonPath("$.totalCount").value(6))
      .andExpect(jsonPath("$.currentPage").value(3))
      .andExpect(jsonPath("$.pageNumberEstimated").value(true))
      .andExpect(jsonPath("$.pageSize").value(2));
  }

  @Test
  void testGetJukeboxesSupportingSettingWithNegativeOffset() throws Exception {
    mockMvc.perform(get("/v1/jukeboxes?settingId=515ef38b-0529-418f-a93a-7f2347fc5805&offset=-3"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.data.length()").value(6))
      .andExpect(jsonPath("$.totalCount").value(6))
      .andExpect(jsonPath("$.currentPage").value(1))
      .andExpect(jsonPath("$.pageNumberEstimated").value(false));
  }

  @Test
  void testGetJukeboxesSupportingSettingWithLimitAsString() throws Exception {
    mockMvc.perform(get("/v1/jukeboxes?settingId=515ef38b-0529-418f-a93a-7f2347fc5805&limit=hello"))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(content().json("{ 'status': 400, 'message': 'Bad Request - limit should be of type int' }"));
  }

  @Test
  void testGetJukeboxesSupportingSettingWithModelOffsetAndLimit() throws Exception {
    mockMvc.perform(get("/v1/jukeboxes?settingId=67ab1ec7-59b8-42f9-b96c-b261cc2a2ed9&model=virtuo&offset=1&limit=1"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.data.length()").value(1))
      .andExpect(jsonPath("$.totalCount").value(3))
      .andExpect(jsonPath("$.currentPage").value(2))
      .andExpect(jsonPath("$.pageNumberEstimated").value(false))
      .andExpect(jsonPath("$.pageSize").value(1));
  }

}
