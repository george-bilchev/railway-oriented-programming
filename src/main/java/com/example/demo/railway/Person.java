package com.example.demo.railway;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Builder
@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Person {
	
	@NotNull
	@Accessors(fluent = true)
	@JsonProperty("hasWristBand")
	private Boolean hasWristBand;
	
	@NotNull
	private Integer heightInCm;
	
	@NotNull
	private Integer weightInKg;
	
	@NotNull
	@Accessors(fluent = true)
	@JsonProperty("isPrgenant")
	private Boolean isPrgenant;
	
	@NotNull
	@Accessors(fluent = true)
	@JsonProperty("hasHeartCondition")
	private Boolean hasHeartCondition;
	
	@NotNull
	@Accessors(fluent = true)
	@JsonProperty("jumpedQueue")
	private Boolean jumpedQueue;
}
