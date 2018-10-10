package com.urbanairship.api.client;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.urbanairship.api.common.parse.DateFormats;
import com.urbanairship.api.push.model.DeviceType;
import com.urbanairship.api.push.model.DeviceTypeData;
import com.urbanairship.api.push.model.PushPayload;
import com.urbanairship.api.push.model.audience.Selectors;
import com.urbanairship.api.push.model.notification.Notifications;
import com.urbanairship.api.push.parse.PushObjectMapper;
import com.urbanairship.api.schedule.ScheduleRequest;
import com.urbanairship.api.schedule.model.Schedule;
import com.urbanairship.api.schedule.model.ScheduleResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.log4j.BasicConfigurator;
import org.asynchttpclient.filter.FilterContext;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;


public class AmplUrbanAirshipClientTest {
    public static final String CONTENT_TYPE_KEY = "Content-type";

    public static final String APP_JSON = "application/json";

    public static final String TEXT_CSV = "text/csv";

    static {
        BasicConfigurator.configure();
    }

    private UrbanAirshipClient client;

    private AsyncRequestClient asyncRequestClient;

    @Before
    public void setup() {
        asyncRequestClient = AsyncRequestClient.newBuilder().setBaseUri("http://localhost:8080").setMaxRetries(5).setRetryPredicate(new Predicate<FilterContext>() {
            @Override
            public boolean apply(FilterContext input) {
                return (input.getResponseStatus().getStatusCode()) >= 500;
            }
        }).build();
        client = UrbanAirshipClient.newBuilder().setKey("key").setSecret("secret").setClient(asyncRequestClient).build();
    }

    @After
    public void takeDown() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule();

    @Rule
    public WireMockClassRule instanceRule = AmplUrbanAirshipClientTest.wireMockRule;

    @Test(timeout = 10000)
    @SuppressWarnings("unchecked")
    public void testSchedule_add32569() throws Exception {
        PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.all()).setDeviceTypes(DeviceTypeData.of(DeviceType.IOS)).setNotification(Notifications.alert("Foo")).build();
        DateTime dateTime = DateTime.now(DateTimeZone.UTC).plusSeconds(60);
        Schedule schedule = Schedule.newBuilder().setScheduledTimestamp(dateTime).build();
        String pushJSON = "{\"ok\" : true,\"operation_id\" : \"OpID\", \"schedule_urls\":[\"ScheduleURL\"]}";
        Assert.assertEquals("{\"ok\" : true,\"operation_id\" : \"OpID\", \"schedule_urls\":[\"ScheduleURL\"]}", pushJSON);
        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/api/schedules/")).willReturn(WireMock.aResponse().withHeader(AmplUrbanAirshipClientTest.CONTENT_TYPE_KEY, AmplUrbanAirshipClientTest.APP_JSON).withBody(pushJSON).withStatus(201)));
        try {
            Response<ScheduleResponse> response = this.client.execute(ScheduleRequest.newRequest(schedule, pushPayload).setName("Test"));
            Assert.assertTrue(((com.urbanairship.api.client.Response)response).getHeaders().containsKey("Transfer-Encoding"));
            Assert.assertEquals("chunked", ((com.urbanairship.api.client.Response)response).getHeaders().get("Transfer-Encoding"));
            Assert.assertTrue(((com.urbanairship.api.client.Response)response).getHeaders().containsKey("Content-type"));
            Assert.assertEquals("application/json", ((com.urbanairship.api.client.Response)response).getHeaders().get("Content-type"));
            Assert.assertTrue(((com.urbanairship.api.client.Response)response).getHeaders().containsKey("Server"));
            Assert.assertEquals("Jetty(6.1.x)", ((com.urbanairship.api.client.Response)response).getHeaders().get("Server"));
            Assert.assertTrue(((Optional) (((Response) (response)).getBody())).isPresent());
            Assert.assertEquals("Optional.of(ScheduleResponse{ok=true, operationId=\'OpID\', scheduleUrls=[ScheduleURL], scheduleIds=[], schedulePayloads=[]})", ((Optional) (((Response) (response)).getBody())).toString());
            Assert.assertEquals(1213779045, ((int) (((Optional) (((Response) (response)).getBody())).hashCode())));
            Assert.assertEquals(201, ((int) (((Response) (response)).getStatus())));
            Assert.assertEquals("Response{body=Optional.of(ScheduleResponse{ok=true, operationId=\'OpID\', scheduleUrls=[ScheduleURL], scheduleIds=[], schedulePayloads=[]}), headers={Transfer-Encoding=chunked, Content-type=application/json, Server=Jetty(6.1.x)}, status=201}", ((Response) (response)).toString());
            Assert.assertEquals(1401101913, ((int) (((Response) (response)).hashCode())));
            WireMock.verify(WireMock.postRequestedFor(WireMock.urlEqualTo("/api/schedules/")).withHeader(AmplUrbanAirshipClientTest.CONTENT_TYPE_KEY, WireMock.equalTo(AmplUrbanAirshipClientTest.APP_JSON)));
            List<LoggedRequest> requests = WireMock.findAll(WireMock.postRequestedFor(WireMock.urlEqualTo("/api/schedules/")));
            int o_testSchedule_add32569__44 = requests.size();
            Assert.assertEquals(1, ((int) (o_testSchedule_add32569__44)));
            String receivedBody = requests.get(0).getBodyAsString();
            ObjectMapper mapper = PushObjectMapper.getInstance();
            Map<String, Object> result = mapper.readValue(receivedBody, new TypeReference<Map<String, Object>>() {});
            String name = ((String) (result.get("name")));
            Assert.assertEquals("Test", name);
            boolean o_testSchedule_add32569__58 = name.equals("Test");
            Assert.assertTrue(o_testSchedule_add32569__58);
            Map<String, String> scheduleMap = ((Map<String, String>) (result.get("schedule")));
            String dateTimeString = scheduleMap.get("scheduled_time");
            DateTime o_testSchedule_add32569__65 = DateTime.parse(dateTimeString, DateFormats.DATE_FORMATTER);
            Assert.assertTrue(((DateTimeZone) (((Chronology) (((DateTime) (o_testSchedule_add32569__65)).getChronology())).getZone())).isFixed());
            Assert.assertEquals("UTC", ((DateTimeZone) (((Chronology) (((DateTime) (o_testSchedule_add32569__65)).getChronology())).getZone())).toString());
            Assert.assertEquals(84356, ((int) (((DateTimeZone) (((Chronology) (((DateTime) (o_testSchedule_add32569__65)).getChronology())).getZone())).hashCode())));
            Assert.assertEquals("UTC", ((DateTimeZone) (((Chronology) (((DateTime) (o_testSchedule_add32569__65)).getChronology())).getZone())).getID());
            Assert.assertEquals("ISOChronology[UTC]", ((Chronology) (((DateTime) (o_testSchedule_add32569__65)).getChronology())).toString());
            Assert.assertEquals(10, ((int) (((DateTime) (o_testSchedule_add32569__65)).getMonthOfYear())));
            Assert.assertEquals(13, ((int) (((DateTime) (o_testSchedule_add32569__65)).getHourOfDay())));
            Assert.assertEquals(49, ((int) (((DateTime) (o_testSchedule_add32569__65)).getMinuteOfHour())));
            Assert.assertEquals(0, ((int) (((DateTime) (o_testSchedule_add32569__65)).getMillisOfSecond())));
            Assert.assertEquals(2018, ((int) (((DateTime) (o_testSchedule_add32569__65)).getWeekyear())));
            Assert.assertEquals(2018, ((int) (((DateTime) (o_testSchedule_add32569__65)).getYearOfEra())));
            Assert.assertEquals(18, ((int) (((DateTime) (o_testSchedule_add32569__65)).getYearOfCentury())));
            Assert.assertEquals(20, ((int) (((DateTime) (o_testSchedule_add32569__65)).getCenturyOfEra())));
            Assert.assertEquals(829, ((int) (((DateTime) (o_testSchedule_add32569__65)).getMinuteOfDay())));
            Assert.assertEquals(41, ((int) (((DateTime) (o_testSchedule_add32569__65)).getWeekOfWeekyear())));
            Assert.assertEquals(1, ((int) (((DateTime) (o_testSchedule_add32569__65)).getEra())));
            Assert.assertEquals(282, ((int) (((DateTime) (o_testSchedule_add32569__65)).getDayOfYear())));
            Assert.assertEquals(2, ((int) (((DateTime) (o_testSchedule_add32569__65)).getDayOfWeek())));
            Assert.assertEquals(9, ((int) (((DateTime) (o_testSchedule_add32569__65)).getDayOfMonth())));
            Assert.assertEquals(2018, ((int) (((DateTime) (o_testSchedule_add32569__65)).getYear())));
            Assert.assertFalse(((DateTime) (o_testSchedule_add32569__65)).isBeforeNow());
            Assert.assertFalse(((DateTime) (o_testSchedule_add32569__65)).isEqualNow());
            Assert.assertTrue(((DateTime) (o_testSchedule_add32569__65)).isAfterNow());
            Assert.assertTrue(((DateTimeZone) (((DateTime) (o_testSchedule_add32569__65)).getZone())).isFixed());
            Assert.assertEquals("UTC", ((DateTimeZone) (((DateTime) (o_testSchedule_add32569__65)).getZone())).toString());
            Assert.assertEquals(84356, ((int) (((DateTimeZone) (((DateTime) (o_testSchedule_add32569__65)).getZone())).hashCode())));
            Assert.assertEquals("UTC", ((DateTimeZone) (((DateTime) (o_testSchedule_add32569__65)).getZone())).getID());
            DateTime receivedDateTime = DateTime.parse(dateTimeString, DateFormats.DATE_FORMATTER);
            receivedDateTime.getMillis();
            dateTime.getMillis();
            Assert.assertTrue(((com.urbanairship.api.client.Response)response).getHeaders().containsKey("Transfer-Encoding"));
            Assert.assertEquals("chunked", ((com.urbanairship.api.client.Response)response).getHeaders().get("Transfer-Encoding"));
            Assert.assertTrue(((com.urbanairship.api.client.Response)response).getHeaders().containsKey("Content-type"));
            Assert.assertEquals("application/json", ((com.urbanairship.api.client.Response)response).getHeaders().get("Content-type"));
            Assert.assertTrue(((com.urbanairship.api.client.Response)response).getHeaders().containsKey("Server"));
            Assert.assertEquals("Jetty(6.1.x)", ((com.urbanairship.api.client.Response)response).getHeaders().get("Server"));
            Assert.assertTrue(((Optional) (((Response) (response)).getBody())).isPresent());
            Assert.assertEquals("Optional.of(ScheduleResponse{ok=true, operationId=\'OpID\', scheduleUrls=[ScheduleURL], scheduleIds=[], schedulePayloads=[]})", ((Optional) (((Response) (response)).getBody())).toString());
            Assert.assertEquals(1213779045, ((int) (((Optional) (((Response) (response)).getBody())).hashCode())));
            Assert.assertEquals(201, ((int) (((Response) (response)).getStatus())));
            Assert.assertEquals("Response{body=Optional.of(ScheduleResponse{ok=true, operationId=\'OpID\', scheduleUrls=[ScheduleURL], scheduleIds=[], schedulePayloads=[]}), headers={Transfer-Encoding=chunked, Content-type=application/json, Server=Jetty(6.1.x)}, status=201}", ((Response) (response)).toString());
            Assert.assertEquals(1401101913, ((int) (((Response) (response)).hashCode())));
            Assert.assertEquals(1, ((int) (o_testSchedule_add32569__44)));
            Assert.assertEquals("Test", name);
            Assert.assertTrue(o_testSchedule_add32569__58);
            Assert.assertTrue(((DateTimeZone) (((Chronology) (((DateTime) (o_testSchedule_add32569__65)).getChronology())).getZone())).isFixed());
            Assert.assertEquals("UTC", ((DateTimeZone) (((Chronology) (((DateTime) (o_testSchedule_add32569__65)).getChronology())).getZone())).toString());
            Assert.assertEquals(84356, ((int) (((DateTimeZone) (((Chronology) (((DateTime) (o_testSchedule_add32569__65)).getChronology())).getZone())).hashCode())));
            Assert.assertEquals("UTC", ((DateTimeZone) (((Chronology) (((DateTime) (o_testSchedule_add32569__65)).getChronology())).getZone())).getID());
            Assert.assertEquals("ISOChronology[UTC]", ((Chronology) (((DateTime) (o_testSchedule_add32569__65)).getChronology())).toString());
            Assert.assertEquals(10, ((int) (((DateTime) (o_testSchedule_add32569__65)).getMonthOfYear())));
            Assert.assertEquals(13, ((int) (((DateTime) (o_testSchedule_add32569__65)).getHourOfDay())));
            Assert.assertEquals(49, ((int) (((DateTime) (o_testSchedule_add32569__65)).getMinuteOfHour())));
            Assert.assertEquals(0, ((int) (((DateTime) (o_testSchedule_add32569__65)).getMillisOfSecond())));
            Assert.assertEquals(2018, ((int) (((DateTime) (o_testSchedule_add32569__65)).getWeekyear())));
            Assert.assertEquals(2018, ((int) (((DateTime) (o_testSchedule_add32569__65)).getYearOfEra())));
            Assert.assertEquals(18, ((int) (((DateTime) (o_testSchedule_add32569__65)).getYearOfCentury())));
            Assert.assertEquals(20, ((int) (((DateTime) (o_testSchedule_add32569__65)).getCenturyOfEra())));
            Assert.assertEquals(829, ((int) (((DateTime) (o_testSchedule_add32569__65)).getMinuteOfDay())));
            Assert.assertEquals(41, ((int) (((DateTime) (o_testSchedule_add32569__65)).getWeekOfWeekyear())));
            Assert.assertEquals(1, ((int) (((DateTime) (o_testSchedule_add32569__65)).getEra())));
            Assert.assertEquals(282, ((int) (((DateTime) (o_testSchedule_add32569__65)).getDayOfYear())));
            Assert.assertEquals(2, ((int) (((DateTime) (o_testSchedule_add32569__65)).getDayOfWeek())));
            Assert.assertEquals(9, ((int) (((DateTime) (o_testSchedule_add32569__65)).getDayOfMonth())));
            Assert.assertEquals(2018, ((int) (((DateTime) (o_testSchedule_add32569__65)).getYear())));
            Assert.assertFalse(((DateTime) (o_testSchedule_add32569__65)).isBeforeNow());
            Assert.assertFalse(((DateTime) (o_testSchedule_add32569__65)).isEqualNow());
            Assert.assertTrue(((DateTime) (o_testSchedule_add32569__65)).isAfterNow());
            Assert.assertTrue(((DateTimeZone) (((DateTime) (o_testSchedule_add32569__65)).getZone())).isFixed());
            Assert.assertEquals("UTC", ((DateTimeZone) (((DateTime) (o_testSchedule_add32569__65)).getZone())).toString());
            Assert.assertEquals(84356, ((int) (((DateTimeZone) (((DateTime) (o_testSchedule_add32569__65)).getZone())).hashCode())));
            Assert.assertEquals("UTC", ((DateTimeZone) (((DateTime) (o_testSchedule_add32569__65)).getZone())).getID());
        } catch (Exception ex) {
            String String_246 = "Exception " + ex;
        }
        Assert.assertEquals("{\"ok\" : true,\"operation_id\" : \"OpID\", \"schedule_urls\":[\"ScheduleURL\"]}", pushJSON);
    }

    @Test(timeout = 10000)
    @SuppressWarnings("unchecked")
    public void testSchedule_add32538() throws Exception {
        PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.all()).setDeviceTypes(DeviceTypeData.of(DeviceType.IOS)).setNotification(Notifications.alert("Foo")).build();
        DateTime dateTime = DateTime.now(DateTimeZone.UTC).plusSeconds(60);
        Schedule o_testSchedule_add32538__13 = Schedule.newBuilder().setScheduledTimestamp(dateTime).build();
        Assert.assertFalse(((Schedule) (o_testSchedule_add32538__13)).getLocalTimePresent());
        Assert.assertEquals("ISOChronology[UTC]", ((Chronology) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getChronology())).toString());
        Assert.assertEquals(10, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getMonthOfYear())));
        Assert.assertEquals(13, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getHourOfDay())));
        Assert.assertEquals(49, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getMinuteOfHour())));
        Assert.assertEquals(2018, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getWeekyear())));
        Assert.assertEquals(2018, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getYearOfEra())));
        Assert.assertEquals(18, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getYearOfCentury())));
        Assert.assertEquals(20, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getCenturyOfEra())));
        Assert.assertEquals(829, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getMinuteOfDay())));
        Assert.assertEquals(41, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getWeekOfWeekyear())));
        Assert.assertEquals(1, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getEra())));
        Assert.assertEquals(282, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getDayOfYear())));
        Assert.assertEquals(2, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getDayOfWeek())));
        Assert.assertEquals(9, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getDayOfMonth())));
        Assert.assertEquals(2018, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getYear())));
        Assert.assertFalse(((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).isBeforeNow());
        Assert.assertFalse(((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).isEqualNow());
        Assert.assertTrue(((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).isAfterNow());
        Assert.assertTrue(((DateTimeZone) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getZone())).isFixed());
        Assert.assertEquals("UTC", ((DateTimeZone) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getZone())).toString());
        Assert.assertEquals(84356, ((int) (((DateTimeZone) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getZone())).hashCode())));
        Assert.assertEquals("UTC", ((DateTimeZone) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getZone())).getID());
        Assert.assertFalse(((java.util.Optional) (((Schedule) (o_testSchedule_add32538__13)).getBestTime())).isPresent());
        Assert.assertEquals("Optional.empty", ((java.util.Optional) (((Schedule) (o_testSchedule_add32538__13)).getBestTime())).toString());
        Assert.assertEquals(0, ((int) (((java.util.Optional) (((Schedule) (o_testSchedule_add32538__13)).getBestTime())).hashCode())));
        Schedule schedule = Schedule.newBuilder().setScheduledTimestamp(dateTime).build();
        String pushJSON = "{\"ok\" : true,\"operation_id\" : \"OpID\", \"schedule_urls\":[\"ScheduleURL\"]}";
        Assert.assertEquals("{\"ok\" : true,\"operation_id\" : \"OpID\", \"schedule_urls\":[\"ScheduleURL\"]}", pushJSON);
        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/api/schedules/")).willReturn(WireMock.aResponse().withHeader(AmplUrbanAirshipClientTest.CONTENT_TYPE_KEY, AmplUrbanAirshipClientTest.APP_JSON).withBody(pushJSON).withStatus(201)));
        try {
            Response<ScheduleResponse> response = this.client.execute(ScheduleRequest.newRequest(schedule, pushPayload).setName("Test"));
            Assert.assertTrue(((com.urbanairship.api.client.Response)response).getHeaders().containsKey("Transfer-Encoding"));
            Assert.assertEquals("chunked", ((com.urbanairship.api.client.Response)response).getHeaders().get("Transfer-Encoding"));
            Assert.assertTrue(((com.urbanairship.api.client.Response)response).getHeaders().containsKey("Content-type"));
            Assert.assertEquals("application/json", ((com.urbanairship.api.client.Response)response).getHeaders().get("Content-type"));
            Assert.assertTrue(((com.urbanairship.api.client.Response)response).getHeaders().containsKey("Server"));
            Assert.assertEquals("Jetty(6.1.x)", ((com.urbanairship.api.client.Response)response).getHeaders().get("Server"));
            Assert.assertTrue(((Optional) (((Response) (response)).getBody())).isPresent());
            Assert.assertEquals("Optional.of(ScheduleResponse{ok=true, operationId=\'OpID\', scheduleUrls=[ScheduleURL], scheduleIds=[], schedulePayloads=[]})", ((Optional) (((Response) (response)).getBody())).toString());
            Assert.assertEquals(1213779045, ((int) (((Optional) (((Response) (response)).getBody())).hashCode())));
            Assert.assertEquals(201, ((int) (((Response) (response)).getStatus())));
            Assert.assertEquals("Response{body=Optional.of(ScheduleResponse{ok=true, operationId=\'OpID\', scheduleUrls=[ScheduleURL], scheduleIds=[], schedulePayloads=[]}), headers={Transfer-Encoding=chunked, Content-type=application/json, Server=Jetty(6.1.x)}, status=201}", ((Response) (response)).toString());
            Assert.assertEquals(1401101913, ((int) (((Response) (response)).hashCode())));
            WireMock.verify(WireMock.postRequestedFor(WireMock.urlEqualTo("/api/schedules/")).withHeader(AmplUrbanAirshipClientTest.CONTENT_TYPE_KEY, WireMock.equalTo(AmplUrbanAirshipClientTest.APP_JSON)));
            List<LoggedRequest> requests = WireMock.findAll(WireMock.postRequestedFor(WireMock.urlEqualTo("/api/schedules/")));
            int o_testSchedule_add32538__47 = requests.size();
            Assert.assertEquals(1, ((int) (o_testSchedule_add32538__47)));
            String receivedBody = requests.get(0).getBodyAsString();
            ObjectMapper mapper = PushObjectMapper.getInstance();
            Map<String, Object> result = mapper.readValue(receivedBody, new TypeReference<Map<String, Object>>() {});
            String name = ((String) (result.get("name")));
            Assert.assertEquals("Test", name);
            boolean o_testSchedule_add32538__61 = name.equals("Test");
            Assert.assertTrue(o_testSchedule_add32538__61);
            Map<String, String> scheduleMap = ((Map<String, String>) (result.get("schedule")));
            String dateTimeString = scheduleMap.get("scheduled_time");
            DateTime receivedDateTime = DateTime.parse(dateTimeString, DateFormats.DATE_FORMATTER);
            receivedDateTime.getMillis();
            dateTime.getMillis();
            Assert.assertTrue(((com.urbanairship.api.client.Response)response).getHeaders().containsKey("Transfer-Encoding"));
            Assert.assertEquals("chunked", ((com.urbanairship.api.client.Response)response).getHeaders().get("Transfer-Encoding"));
            Assert.assertTrue(((com.urbanairship.api.client.Response)response).getHeaders().containsKey("Content-type"));
            Assert.assertEquals("application/json", ((com.urbanairship.api.client.Response)response).getHeaders().get("Content-type"));
            Assert.assertTrue(((com.urbanairship.api.client.Response)response).getHeaders().containsKey("Server"));
            Assert.assertEquals("Jetty(6.1.x)", ((com.urbanairship.api.client.Response)response).getHeaders().get("Server"));
            Assert.assertTrue(((Optional) (((Response) (response)).getBody())).isPresent());
            Assert.assertEquals("Optional.of(ScheduleResponse{ok=true, operationId=\'OpID\', scheduleUrls=[ScheduleURL], scheduleIds=[], schedulePayloads=[]})", ((Optional) (((Response) (response)).getBody())).toString());
            Assert.assertEquals(1213779045, ((int) (((Optional) (((Response) (response)).getBody())).hashCode())));
            Assert.assertEquals(201, ((int) (((Response) (response)).getStatus())));
            Assert.assertEquals("Response{body=Optional.of(ScheduleResponse{ok=true, operationId=\'OpID\', scheduleUrls=[ScheduleURL], scheduleIds=[], schedulePayloads=[]}), headers={Transfer-Encoding=chunked, Content-type=application/json, Server=Jetty(6.1.x)}, status=201}", ((Response) (response)).toString());
            Assert.assertEquals(1401101913, ((int) (((Response) (response)).hashCode())));
            Assert.assertEquals(1, ((int) (o_testSchedule_add32538__47)));
            Assert.assertEquals("Test", name);
            Assert.assertTrue(o_testSchedule_add32538__61);
        } catch (Exception ex) {
            String String_215 = "Exception " + ex;
        }
        Assert.assertFalse(((Schedule) (o_testSchedule_add32538__13)).getLocalTimePresent());
        Assert.assertEquals("ISOChronology[UTC]", ((Chronology) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getChronology())).toString());
        Assert.assertEquals(10, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getMonthOfYear())));
        Assert.assertEquals(13, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getHourOfDay())));
        Assert.assertEquals(49, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getMinuteOfHour())));
        Assert.assertEquals(2018, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getWeekyear())));
        Assert.assertEquals(2018, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getYearOfEra())));
        Assert.assertEquals(18, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getYearOfCentury())));
        Assert.assertEquals(20, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getCenturyOfEra())));
        Assert.assertEquals(829, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getMinuteOfDay())));
        Assert.assertEquals(41, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getWeekOfWeekyear())));
        Assert.assertEquals(1, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getEra())));
        Assert.assertEquals(282, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getDayOfYear())));
        Assert.assertEquals(2, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getDayOfWeek())));
        Assert.assertEquals(9, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getDayOfMonth())));
        Assert.assertEquals(2018, ((int) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getYear())));
        Assert.assertFalse(((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).isBeforeNow());
        Assert.assertFalse(((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).isEqualNow());
        Assert.assertTrue(((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).isAfterNow());
        Assert.assertTrue(((DateTimeZone) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getZone())).isFixed());
        Assert.assertEquals("UTC", ((DateTimeZone) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getZone())).toString());
        Assert.assertEquals(84356, ((int) (((DateTimeZone) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getZone())).hashCode())));
        Assert.assertEquals("UTC", ((DateTimeZone) (((DateTime) (((Schedule) (o_testSchedule_add32538__13)).getScheduledTimestamp())).getZone())).getID());
        Assert.assertFalse(((java.util.Optional) (((Schedule) (o_testSchedule_add32538__13)).getBestTime())).isPresent());
        Assert.assertEquals("Optional.empty", ((java.util.Optional) (((Schedule) (o_testSchedule_add32538__13)).getBestTime())).toString());
        Assert.assertEquals(0, ((int) (((java.util.Optional) (((Schedule) (o_testSchedule_add32538__13)).getBestTime())).hashCode())));
        Assert.assertEquals("{\"ok\" : true,\"operation_id\" : \"OpID\", \"schedule_urls\":[\"ScheduleURL\"]}", pushJSON);
    }

    @Test(timeout = 10000)
    @SuppressWarnings("unchecked")
    public void testSchedulenull32574_failAssert24() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.all()).setDeviceTypes(DeviceTypeData.of(DeviceType.IOS)).setNotification(Notifications.alert("Foo")).build();
            DateTime dateTime = DateTime.now(DateTimeZone.UTC).plusSeconds(60);
            Schedule schedule = Schedule.newBuilder().setScheduledTimestamp(null).build();
            String pushJSON = "{\"ok\" : true,\"operation_id\" : \"OpID\", \"schedule_urls\":[\"ScheduleURL\"]}";
            WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/api/schedules/")).willReturn(WireMock.aResponse().withHeader(AmplUrbanAirshipClientTest.CONTENT_TYPE_KEY, AmplUrbanAirshipClientTest.APP_JSON).withBody(pushJSON).withStatus(201)));
            try {
                Response<ScheduleResponse> response = client.execute(ScheduleRequest.newRequest(schedule, pushPayload).setName("Test"));
                WireMock.verify(WireMock.postRequestedFor(WireMock.urlEqualTo("/api/schedules/")).withHeader(AmplUrbanAirshipClientTest.CONTENT_TYPE_KEY, WireMock.equalTo(AmplUrbanAirshipClientTest.APP_JSON)));
                List<LoggedRequest> requests = WireMock.findAll(WireMock.postRequestedFor(WireMock.urlEqualTo("/api/schedules/")));
                requests.size();
                String receivedBody = requests.get(0).getBodyAsString();
                ObjectMapper mapper = PushObjectMapper.getInstance();
                Map<String, Object> result = mapper.readValue(receivedBody, new TypeReference<Map<String, Object>>() {});
                String name = ((String) (result.get("name")));
                name.equals("Test");
                Map<String, String> scheduleMap = ((Map<String, String>) (result.get("schedule")));
                String dateTimeString = scheduleMap.get("scheduled_time");
                DateTime receivedDateTime = DateTime.parse(dateTimeString, DateFormats.DATE_FORMATTER);
                receivedDateTime.getMillis();
                dateTime.getMillis();
            } catch (Exception ex) {
                String String_251 = "Exception " + ex;
            }
            org.junit.Assert.fail("testSchedulenull32574 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    @SuppressWarnings("unchecked")
    public void testSchedule_add32536() throws Exception {
        PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.all()).setDeviceTypes(DeviceTypeData.of(DeviceType.IOS)).setNotification(Notifications.alert("Foo")).build();
        DateTime o_testSchedule_add32536__10 = DateTime.now(DateTimeZone.UTC).plusSeconds(60);
        Assert.assertTrue(((DateTimeZone) (((Chronology) (((DateTime) (o_testSchedule_add32536__10)).getChronology())).getZone())).isFixed());
        Assert.assertEquals("UTC", ((DateTimeZone) (((Chronology) (((DateTime) (o_testSchedule_add32536__10)).getChronology())).getZone())).toString());
        Assert.assertEquals(84356, ((int) (((DateTimeZone) (((Chronology) (((DateTime) (o_testSchedule_add32536__10)).getChronology())).getZone())).hashCode())));
        Assert.assertEquals("UTC", ((DateTimeZone) (((Chronology) (((DateTime) (o_testSchedule_add32536__10)).getChronology())).getZone())).getID());
        Assert.assertEquals("ISOChronology[UTC]", ((Chronology) (((DateTime) (o_testSchedule_add32536__10)).getChronology())).toString());
        Assert.assertEquals(10, ((int) (((DateTime) (o_testSchedule_add32536__10)).getMonthOfYear())));
        Assert.assertEquals(13, ((int) (((DateTime) (o_testSchedule_add32536__10)).getHourOfDay())));
        Assert.assertEquals(49, ((int) (((DateTime) (o_testSchedule_add32536__10)).getMinuteOfHour())));
        Assert.assertEquals(2018, ((int) (((DateTime) (o_testSchedule_add32536__10)).getWeekyear())));
        Assert.assertEquals(2018, ((int) (((DateTime) (o_testSchedule_add32536__10)).getYearOfEra())));
        Assert.assertEquals(18, ((int) (((DateTime) (o_testSchedule_add32536__10)).getYearOfCentury())));
        Assert.assertEquals(20, ((int) (((DateTime) (o_testSchedule_add32536__10)).getCenturyOfEra())));
        Assert.assertEquals(829, ((int) (((DateTime) (o_testSchedule_add32536__10)).getMinuteOfDay())));
        Assert.assertEquals(41, ((int) (((DateTime) (o_testSchedule_add32536__10)).getWeekOfWeekyear())));
        Assert.assertEquals(1, ((int) (((DateTime) (o_testSchedule_add32536__10)).getEra())));
        Assert.assertEquals(282, ((int) (((DateTime) (o_testSchedule_add32536__10)).getDayOfYear())));
        Assert.assertEquals(2, ((int) (((DateTime) (o_testSchedule_add32536__10)).getDayOfWeek())));
        Assert.assertEquals(9, ((int) (((DateTime) (o_testSchedule_add32536__10)).getDayOfMonth())));
        Assert.assertEquals(2018, ((int) (((DateTime) (o_testSchedule_add32536__10)).getYear())));
        Assert.assertFalse(((DateTime) (o_testSchedule_add32536__10)).isBeforeNow());
        Assert.assertFalse(((DateTime) (o_testSchedule_add32536__10)).isEqualNow());
        Assert.assertTrue(((DateTime) (o_testSchedule_add32536__10)).isAfterNow());
        Assert.assertTrue(((DateTimeZone) (((DateTime) (o_testSchedule_add32536__10)).getZone())).isFixed());
        Assert.assertEquals("UTC", ((DateTimeZone) (((DateTime) (o_testSchedule_add32536__10)).getZone())).toString());
        Assert.assertEquals(84356, ((int) (((DateTimeZone) (((DateTime) (o_testSchedule_add32536__10)).getZone())).hashCode())));
        Assert.assertEquals("UTC", ((DateTimeZone) (((DateTime) (o_testSchedule_add32536__10)).getZone())).getID());
        DateTime dateTime = DateTime.now(DateTimeZone.UTC).plusSeconds(60);
        Schedule schedule = Schedule.newBuilder().setScheduledTimestamp(dateTime).build();
        String pushJSON = "{\"ok\" : true,\"operation_id\" : \"OpID\", \"schedule_urls\":[\"ScheduleURL\"]}";
        Assert.assertEquals("{\"ok\" : true,\"operation_id\" : \"OpID\", \"schedule_urls\":[\"ScheduleURL\"]}", pushJSON);
        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/api/schedules/")).willReturn(WireMock.aResponse().withHeader(AmplUrbanAirshipClientTest.CONTENT_TYPE_KEY, AmplUrbanAirshipClientTest.APP_JSON).withBody(pushJSON).withStatus(201)));
        try {
            Response<ScheduleResponse> response = this.client.execute(ScheduleRequest.newRequest(schedule, pushPayload).setName("Test"));
            Assert.assertTrue(((com.urbanairship.api.client.Response)response).getHeaders().containsKey("Transfer-Encoding"));
            Assert.assertEquals("chunked", ((com.urbanairship.api.client.Response)response).getHeaders().get("Transfer-Encoding"));
            Assert.assertTrue(((com.urbanairship.api.client.Response)response).getHeaders().containsKey("Content-type"));
            Assert.assertEquals("application/json", ((com.urbanairship.api.client.Response)response).getHeaders().get("Content-type"));
            Assert.assertTrue(((com.urbanairship.api.client.Response)response).getHeaders().containsKey("Server"));
            Assert.assertEquals("Jetty(6.1.x)", ((com.urbanairship.api.client.Response)response).getHeaders().get("Server"));
            Assert.assertTrue(((Optional) (((Response) (response)).getBody())).isPresent());
            Assert.assertEquals("Optional.of(ScheduleResponse{ok=true, operationId=\'OpID\', scheduleUrls=[ScheduleURL], scheduleIds=[], schedulePayloads=[]})", ((Optional) (((Response) (response)).getBody())).toString());
            Assert.assertEquals(1213779045, ((int) (((Optional) (((Response) (response)).getBody())).hashCode())));
            Assert.assertEquals(201, ((int) (((Response) (response)).getStatus())));
            Assert.assertEquals("Response{body=Optional.of(ScheduleResponse{ok=true, operationId=\'OpID\', scheduleUrls=[ScheduleURL], scheduleIds=[], schedulePayloads=[]}), headers={Transfer-Encoding=chunked, Content-type=application/json, Server=Jetty(6.1.x)}, status=201}", ((Response) (response)).toString());
            Assert.assertEquals(1401101913, ((int) (((Response) (response)).hashCode())));
            WireMock.verify(WireMock.postRequestedFor(WireMock.urlEqualTo("/api/schedules/")).withHeader(AmplUrbanAirshipClientTest.CONTENT_TYPE_KEY, WireMock.equalTo(AmplUrbanAirshipClientTest.APP_JSON)));
            List<LoggedRequest> requests = WireMock.findAll(WireMock.postRequestedFor(WireMock.urlEqualTo("/api/schedules/")));
            int o_testSchedule_add32536__46 = requests.size();
            Assert.assertEquals(1, ((int) (o_testSchedule_add32536__46)));
            String receivedBody = requests.get(0).getBodyAsString();
            ObjectMapper mapper = PushObjectMapper.getInstance();
            Map<String, Object> result = mapper.readValue(receivedBody, new TypeReference<Map<String, Object>>() {});
            String name = ((String) (result.get("name")));
            Assert.assertEquals("Test", name);
            boolean o_testSchedule_add32536__60 = name.equals("Test");
            Assert.assertTrue(o_testSchedule_add32536__60);
            Map<String, String> scheduleMap = ((Map<String, String>) (result.get("schedule")));
            String dateTimeString = scheduleMap.get("scheduled_time");
            DateTime receivedDateTime = DateTime.parse(dateTimeString, DateFormats.DATE_FORMATTER);
            receivedDateTime.getMillis();
            dateTime.getMillis();
            Assert.assertTrue(((com.urbanairship.api.client.Response)response).getHeaders().containsKey("Transfer-Encoding"));
            Assert.assertEquals("chunked", ((com.urbanairship.api.client.Response)response).getHeaders().get("Transfer-Encoding"));
            Assert.assertTrue(((com.urbanairship.api.client.Response)response).getHeaders().containsKey("Content-type"));
            Assert.assertEquals("application/json", ((com.urbanairship.api.client.Response)response).getHeaders().get("Content-type"));
            Assert.assertTrue(((com.urbanairship.api.client.Response)response).getHeaders().containsKey("Server"));
            Assert.assertEquals("Jetty(6.1.x)", ((com.urbanairship.api.client.Response)response).getHeaders().get("Server"));
            Assert.assertTrue(((Optional) (((Response) (response)).getBody())).isPresent());
            Assert.assertEquals("Optional.of(ScheduleResponse{ok=true, operationId=\'OpID\', scheduleUrls=[ScheduleURL], scheduleIds=[], schedulePayloads=[]})", ((Optional) (((Response) (response)).getBody())).toString());
            Assert.assertEquals(1213779045, ((int) (((Optional) (((Response) (response)).getBody())).hashCode())));
            Assert.assertEquals(201, ((int) (((Response) (response)).getStatus())));
            Assert.assertEquals("Response{body=Optional.of(ScheduleResponse{ok=true, operationId=\'OpID\', scheduleUrls=[ScheduleURL], scheduleIds=[], schedulePayloads=[]}), headers={Transfer-Encoding=chunked, Content-type=application/json, Server=Jetty(6.1.x)}, status=201}", ((Response) (response)).toString());
            Assert.assertEquals(1401101913, ((int) (((Response) (response)).hashCode())));
            Assert.assertEquals(1, ((int) (o_testSchedule_add32536__46)));
            Assert.assertEquals("Test", name);
            Assert.assertTrue(o_testSchedule_add32536__60);
        } catch (Exception ex) {
            String String_213 = "Exception " + ex;
        }
        Assert.assertTrue(((DateTimeZone) (((Chronology) (((DateTime) (o_testSchedule_add32536__10)).getChronology())).getZone())).isFixed());
        Assert.assertEquals("UTC", ((DateTimeZone) (((Chronology) (((DateTime) (o_testSchedule_add32536__10)).getChronology())).getZone())).toString());
        Assert.assertEquals(84356, ((int) (((DateTimeZone) (((Chronology) (((DateTime) (o_testSchedule_add32536__10)).getChronology())).getZone())).hashCode())));
        Assert.assertEquals("UTC", ((DateTimeZone) (((Chronology) (((DateTime) (o_testSchedule_add32536__10)).getChronology())).getZone())).getID());
        Assert.assertEquals("ISOChronology[UTC]", ((Chronology) (((DateTime) (o_testSchedule_add32536__10)).getChronology())).toString());
        Assert.assertEquals(10, ((int) (((DateTime) (o_testSchedule_add32536__10)).getMonthOfYear())));
        Assert.assertEquals(13, ((int) (((DateTime) (o_testSchedule_add32536__10)).getHourOfDay())));
        Assert.assertEquals(49, ((int) (((DateTime) (o_testSchedule_add32536__10)).getMinuteOfHour())));
        Assert.assertEquals(2018, ((int) (((DateTime) (o_testSchedule_add32536__10)).getWeekyear())));
        Assert.assertEquals(2018, ((int) (((DateTime) (o_testSchedule_add32536__10)).getYearOfEra())));
        Assert.assertEquals(18, ((int) (((DateTime) (o_testSchedule_add32536__10)).getYearOfCentury())));
        Assert.assertEquals(20, ((int) (((DateTime) (o_testSchedule_add32536__10)).getCenturyOfEra())));
        Assert.assertEquals(829, ((int) (((DateTime) (o_testSchedule_add32536__10)).getMinuteOfDay())));
        Assert.assertEquals(41, ((int) (((DateTime) (o_testSchedule_add32536__10)).getWeekOfWeekyear())));
        Assert.assertEquals(1, ((int) (((DateTime) (o_testSchedule_add32536__10)).getEra())));
        Assert.assertEquals(282, ((int) (((DateTime) (o_testSchedule_add32536__10)).getDayOfYear())));
        Assert.assertEquals(2, ((int) (((DateTime) (o_testSchedule_add32536__10)).getDayOfWeek())));
        Assert.assertEquals(9, ((int) (((DateTime) (o_testSchedule_add32536__10)).getDayOfMonth())));
        Assert.assertEquals(2018, ((int) (((DateTime) (o_testSchedule_add32536__10)).getYear())));
        Assert.assertFalse(((DateTime) (o_testSchedule_add32536__10)).isBeforeNow());
        Assert.assertFalse(((DateTime) (o_testSchedule_add32536__10)).isEqualNow());
        Assert.assertTrue(((DateTime) (o_testSchedule_add32536__10)).isAfterNow());
        Assert.assertTrue(((DateTimeZone) (((DateTime) (o_testSchedule_add32536__10)).getZone())).isFixed());
        Assert.assertEquals("UTC", ((DateTimeZone) (((DateTime) (o_testSchedule_add32536__10)).getZone())).toString());
        Assert.assertEquals(84356, ((int) (((DateTimeZone) (((DateTime) (o_testSchedule_add32536__10)).getZone())).hashCode())));
        Assert.assertEquals("UTC", ((DateTimeZone) (((DateTime) (o_testSchedule_add32536__10)).getZone())).getID());
        Assert.assertEquals("{\"ok\" : true,\"operation_id\" : \"OpID\", \"schedule_urls\":[\"ScheduleURL\"]}", pushJSON);
    }

    @Test(timeout = 10000)
    public void testUpdateSchedulenull177459_failAssert149() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.all()).setDeviceTypes(DeviceTypeData.of(DeviceType.IOS)).setNotification(Notifications.alert("Foo")).build();
            DateTime dateTime = DateTime.now(DateTimeZone.UTC).plusSeconds(60);
            Schedule schedule = Schedule.newBuilder().setScheduledTimestamp(null).build();
            String responseJson = "{\"ok\" : true,\"operation_id\" : \"OpID\" }";
            WireMock.stubFor(WireMock.put(WireMock.urlEqualTo("/api/schedules/id")).willReturn(WireMock.aResponse().withHeader(AmplUrbanAirshipClientTest.CONTENT_TYPE_KEY, AmplUrbanAirshipClientTest.APP_JSON).withBody(responseJson).withStatus(201)));
            try {
                Response<ScheduleResponse> response = client.execute(ScheduleRequest.newUpdateRequest(schedule, pushPayload, "id").setName("test"));
                WireMock.verify(WireMock.putRequestedFor(WireMock.urlEqualTo("/api/schedules/id")).withHeader(AmplUrbanAirshipClientTest.CONTENT_TYPE_KEY, WireMock.equalTo(AmplUrbanAirshipClientTest.APP_JSON)));
                List<LoggedRequest> requests = WireMock.findAll(WireMock.putRequestedFor(WireMock.urlEqualTo("/api/schedules/id")));
                requests.size();
                response.getHeaders();
                response.getStatus();
                response.getBody().isPresent();
            } catch (Exception ex) {
                String String_701 = "Exception " + ex;
            }
            org.junit.Assert.fail("testUpdateSchedulenull177459 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected_1.getMessage());
        }
    }
}

