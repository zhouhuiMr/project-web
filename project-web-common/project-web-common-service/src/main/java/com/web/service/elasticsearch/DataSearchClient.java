package com.web.service.elasticsearch;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Component
public class DataSearchClient {
	
	@Value("${elasticsearch.server.host}")
	private String host;
	
	@Value("${elasticsearch.server.port}")
	private Integer port;
	
	@Value("${elasticsearch.server.scheme: 'http'}")
	private String scheme;
	
	@Value("${elasticsearch.username}")
	private String username;
	
	@Value("${elasticsearch.password}")
	private String password;

	@Bean
	public ElasticsearchClient elClient() {
		RestClient restClient = elRestClient();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JacksonJsonpMapper jacksonMapper = new JacksonJsonpMapper(mapper);
		ElasticsearchTransport transport = new RestClientTransport(restClient, jacksonMapper);
		return new ElasticsearchClient(transport);
	}
	
	@Bean
	public RestClient elRestClient() {
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
		HttpHost httpHost = new HttpHost(host, port, scheme);
		
		return RestClient.builder(httpHost)
		.setHttpClientConfigCallback(result -> {
			result.setDefaultCredentialsProvider(credentialsProvider);
			return result;
		})
		.setRequestConfigCallback(config -> {
			config.setConnectTimeout(10000);
			config.setSocketTimeout(60000);
			return config;
		})
		.build();
	}
}
