package com.roze.api_gateway.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteKey {
    private List<String> path;
    private HttpMethod method;
}
