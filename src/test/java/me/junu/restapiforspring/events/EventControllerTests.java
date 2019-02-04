package me.junu.restapiforspring.events;

import lombok.extern.log4j.Log4j2;
import me.junu.restapiforspring.accounts.Account;
import me.junu.restapiforspring.accounts.AccountRepository;
import me.junu.restapiforspring.accounts.AccountRole;
import me.junu.restapiforspring.accounts.AccountService;
import me.junu.restapiforspring.common.AppProperties;
import me.junu.restapiforspring.common.BaseControllerTest;
import me.junu.restapiforspring.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTests extends BaseControllerTest {


    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AppProperties appProperties;



    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 1, 11, 11, 11))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 1, 12, 11, 11))
                .beginEventDateTime(LocalDateTime.of(2019, 1, 13, 11, 11))
                .endEventDateTime(LocalDateTime.of(2019, 1, 14, 11, 11))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타트업 팩토리")
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(10)))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update").exists())
                .andExpect(jsonPath("_links.events").exists())
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("events").description("link to self"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("update").description("link to query")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.ACCEPT).description("accept header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("name of new Event"),
                                fieldWithPath("description").description("description of new Event"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new Event"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTimeof new Event"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime of new Event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime of new Event"),
                                fieldWithPath("location").description("location of new Event"),
                                fieldWithPath("basePrice").description("basePrice of new Event"),
                                fieldWithPath("maxPrice").description("maxPrice of new Event"),
                                fieldWithPath("limitOfEnrollment").description("Limit of Enrollment")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.LOCATION).description("location header")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("id of new Event"),
                                fieldWithPath("name").description("name of new Event"),
                                fieldWithPath("description").description("description of new Event"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new Event"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTimeof new Event"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime of new Event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime of new Event"),
                                fieldWithPath("location").description("location of new Event"),
                                fieldWithPath("basePrice").description("basePrice of new Event"),
                                fieldWithPath("maxPrice").description("maxPrice of new Event"),
                                fieldWithPath("free").description("tells if event is free or not"),
                                fieldWithPath("offline").description("tells if event is offline event or not"),
                                fieldWithPath("eventStatus").description("event status")
                        )
                ));
    }

    private String getBearerToken() throws Exception {
        return "Bearer" + getAccessToken();
    }

    private String getAccessToken() throws Exception {


        ResultActions perform = mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                .param("username", appProperties.getUserUsername())
                .param("password", appProperties.getUserPassword())
                .param("grant_type", "password")
        );

        MockHttpServletResponse response = perform.andDo(print()).andReturn().getResponse();
        String contents = response.getContentAsString();


        Jackson2JsonParser parser = new Jackson2JsonParser();

        return parser.parseMap(contents).get("access_token").toString();
    }


    @Test
    @TestDescription("입력받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 1, 11, 11, 11))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 1, 12, 11, 11))
                .beginEventDateTime(LocalDateTime.of(2019, 1, 13, 11, 11))
                .endEventDateTime(LocalDateTime.of(2019, 1, 14, 11, 11))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타트업 팩토리")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();
        event.setId((long) 10);

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 1, 11, 11, 11))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 1, 12, 11, 11))
                .beginEventDateTime(LocalDateTime.of(2018, 1, 13, 11, 11))
                .endEventDateTime(LocalDateTime.of(2019, 1, 14, 11, 11))
                .basePrice(1000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타트업 팩토리")
                .build();

        mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("_links.index").exists());
    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하는 테스트")
    public void getEventsList() throws Exception {
        //Given
        IntStream.range(0, 30).forEach(this::generateEvent);

        //When
        mockMvc.perform(get("/api/events")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"));

    }


    @Test
    @TestDescription("기존의 이벤트 하나를 조회하기")
    public void getEvent() throws Exception {
        //Given
        Event event = this.generateEvent(100);

        //When & Then
        mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"));
    }

    @Test
    @TestDescription("없는 이벤트를 조회시 404 응답받기")
    public void getEvent404() throws Exception {
        //Given

        //When & Then
        mockMvc.perform(get("/api/events/12321451"))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    @Test
    @TestDescription("이벤트를 정상적으로 수정하기")
    public void updateEvent() throws Exception {
        //Given
        Event event = generateEvent(101);
        EventDto eventDto = modelMapper.map(event, EventDto.class);

        String eventName = "Updated Event";
        eventDto.setName(eventName);

        mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("update-event"));
    }

    @Test
    @TestDescription("입력값이 비어있는 경우의 수정 실패")
    public void updateEventEmpty() throws Exception {
        //Given
        Event event = generateEvent(101);
        EventDto eventDto = new EventDto();

        mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @TestDescription("입력값이 잘못된 경우의 수정 실패")
    public void updateEventBadRequest() throws Exception {
        //Given
        Event event = generateEvent(101);
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(2000000);
        eventDto.setMaxPrice(1000);

        mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("존재하지 않는 이벤트 수정")
    public void updateEventNotExist() throws Exception {
        //Given
        Event event = generateEvent(101);
        EventDto eventDto = modelMapper.map(event, EventDto.class);

        mockMvc.perform(put("/api/events/12341251")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private Event generateEvent(int i) {
        Event event = Event.builder()
                .name("event" + i)
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 1, 11, 11, 11))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 1, 12, 11, 11))
                .beginEventDateTime(LocalDateTime.of(2019, 1, 13, 11, 11))
                .endEventDateTime(LocalDateTime.of(2019, 1, 14, 11, 11))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타트업 팩토리")
                .free(false)
                .offline(true)
                .build();

        return eventRepository.save(event);
    }

}
