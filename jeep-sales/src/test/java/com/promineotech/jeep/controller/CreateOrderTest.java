package com.promineotech.jeep.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import com.promineotech.jeep.entity.JeepModel;
import com.promineotech.jeep.entity.Order;

import lombok.Getter;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(
    scripts = {"classpath:flyway/migrations/V1.0__Jeep_Schema.sql", 
        "classpath:flyway/migrations/V1.1__Jeep_Data.sql"},
    config = @SqlConfig(encoding = "utf-8"))

class CreateOrderTest {
	@LocalServerPort
	private int serverPort;
	@Autowired
	@Getter
	private TestRestTemplate restTemplate;	
	
	@Test
	void testCreateOrderReturnsSuccess201() {
		
		String body = createOrderBody();
	    String uri = String.format("http://localhost:%d/orders", serverPort);
	    
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    
	    HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);
	    
	    //When: order is sent
	    ResponseEntity<Order> response = getRestTemplate().exchange(uri, HttpMethod.POST, bodyEntity, Order.class);
	    
	    //Then: a 201 status is returned
	    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	    
	    //And: the returned order is correct
	    assertThat(response.getBody()).isNotNull();
	    
	    Order order = response.getBody();
	    assertThat(order.getCustomer().getCustomerId()).isEqualTo("ROTH_GARTH");
	    assertThat(order.getModel().getModelId()).isEqualTo(JeepModel.COMPASS);
	    assertThat(order.getModel().getTrimLevel()).isEqualTo("Sport");
	    assertThat(order.getModel().getNumDoors()).isEqualTo(4);
	    assertThat(order.getColor().getColorId()).isEqualTo("EXT_ALPINE_WHITE");
	    assertThat(order.getEngine().getEngineId()).isEqualTo("2_4_GAS");
	    assertThat(order.getTire().getTireId()).isEqualTo("225_MICHELIN");
	    assertThat(order.getOptions()).hasSize(2);
	    
	}

	private String createOrderBody() {
		// @formatter:off
	    return "{\n"
	        + "  \"customer\":\"ROTH_GARTH\",\n"
	        + "  \"model\":\"COMPASS\",\n"
	        + "  \"trim\":\"Sport\",\n"
	        + "  \"doors\":4,\n"
	        + "  \"color\":\"EXT_ALPINE_WHITE\",\n"
	        + "  \"engine\":\"2_4_GAS\",\n"
	        + "  \"tire\":\"225_MICHELIN\",\n"
	        + "  \"options\":[\n"
	        + "     \"EXT_MOPAR_TRAILER\",\n"
	        + "     \"EXT_SMITTY_RACK\"\n"
	        + "  ]\n"
	        + "}";
	    // @formatter:on
	}

}
