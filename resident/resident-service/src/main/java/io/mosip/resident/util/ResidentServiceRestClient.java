package io.mosip.resident.util;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.resident.config.LoggerConfiguration;
import io.mosip.resident.constant.ApiName;
import io.mosip.resident.constant.LoggerFileConstant;
import io.mosip.resident.exception.ApisResourceAccessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The Class RestApiClient.
 *
 * @author Monobikash Das
 */
@Component
public class ResidentServiceRestClient {

	/** The logger. */
	private final Logger logger = LoggerConfiguration.logConfig(ResidentServiceRestClient.class);

	/** The builder. */
	@Autowired
	RestTemplateBuilder builder;

	@Autowired
	@Qualifier("selfTokenRestTemplate")
	private RestTemplate residentRestTemplate;

	@Autowired
	Environment environment;

	/**
	 * Gets the api.
	 *
	 * @param <T>
	 *            the generic type
	 * @param responseType
	 *            the response type
	 * @return the api
	 * @throws Exception
	 */
	public <T> T getApi(URI uri, Class<?> responseType) throws ApisResourceAccessException {
		try {
			return (T) residentRestTemplate.exchange(uri, HttpMethod.GET, setRequestHeader(null, null), responseType)
					.getBody();
		} catch (Exception e) {
			logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
					LoggerFileConstant.APPLICATIONID.toString(), e.getMessage() + ExceptionUtils.getStackTrace(e));
			throw new ApisResourceAccessException("Exception occurred while accessing " + uri, e);
		}

	}

	public Object getApi(ApiName apiName, List<String> pathsegments, String queryParamName, String queryParamValue,
			Class<?> responseType) throws ApisResourceAccessException {

		Object obj = null;
		String apiHostIpPort = environment.getProperty(apiName.name());
		UriComponentsBuilder builder = null;
		UriComponents uriComponents = null;
		if (apiHostIpPort != null) {
			builder = UriComponentsBuilder.fromUriString(apiHostIpPort);
			if (!((pathsegments == null) || (pathsegments.isEmpty()))) {
				for (String segment : pathsegments) {
					if (!((segment == null) || (("").equals(segment)))) {
						builder.pathSegment(segment);
					}
				}
			}

			if (StringUtils.isNotEmpty(queryParamName)) {

				String[] queryParamNameArr = queryParamName.split(",");
				String[] queryParamValueArr = queryParamValue.split(",");
				for (int i = 0; i < queryParamNameArr.length; i++) {
					builder.queryParam(queryParamNameArr[i], queryParamValueArr[i]);
				}

			}
			try {

				uriComponents = builder.build(false).encode();
				obj = getApi(uriComponents.toUri(), responseType);

			} catch (Exception e) {
				logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
						LoggerFileConstant.APPLICATIONID.toString(), e.getMessage() + ExceptionUtils.getStackTrace(e));
				throw new ApisResourceAccessException("Exception occured while accessing ", e);

			}
		}

		return obj;
	}


	public Object getApi(ApiName apiName, List<String> pathsegments, List<String> queryParamName, List<Object> queryParamValue,
						 Class<?> responseType) throws ApisResourceAccessException {

		Object obj = null;
		String apiHostIpPort = environment.getProperty(apiName.name());
		UriComponentsBuilder builder = null;
		UriComponents uriComponents = null;
		if (apiHostIpPort != null) {
			builder = UriComponentsBuilder.fromUriString(apiHostIpPort);
			if (!((pathsegments == null) || (pathsegments.isEmpty()))) {
				for (String segment : pathsegments) {
					if (!((segment == null) || (("").equals(segment)))) {
						builder.pathSegment(segment);
					}
				}
			}

			if (!((queryParamName == null) || (("").equals(queryParamName)))) {

				for (int i = 0; i < queryParamName.size(); i++) {
					builder.queryParam(queryParamName.get(i), queryParamValue.get(i));
				}

			}
			try {

				uriComponents = builder.build(false).encode();
				obj = getApi(uriComponents.toUri(), responseType);

			} catch (Exception e) {
				logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
						LoggerFileConstant.APPLICATIONID.toString(), e.getMessage() + ExceptionUtils.getStackTrace(e));
				throw new ApisResourceAccessException("Exception occurred while accessing ", e);

			}
		}

		return obj;
	}

	@SuppressWarnings({ "unchecked", "null" })
	public <T> T getApi(ApiName apiName, Map<String, String> pathsegments, Class<?> responseType)
			throws Exception {

		String apiHostIpPort = environment.getProperty(apiName.name());
		Object obj = null;
		UriComponentsBuilder builder = null;
		if (apiHostIpPort != null) {

			builder = UriComponentsBuilder.fromUriString(apiHostIpPort);

			URI urlWithPath = builder.build(pathsegments);
			try {
				obj = getApi(urlWithPath, responseType);

			} catch (Exception e) {
				throw new Exception(e);
			}

		}
		return (T) obj;
	}

	@SuppressWarnings("unchecked")
	public <T> T postApi(String uri, MediaType mediaType, Object requestType, Class<?> responseClass)
			throws ApisResourceAccessException {
		try {
			logger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
					LoggerFileConstant.APPLICATIONID.toString(), uri);
			T response = (T) residentRestTemplate.postForObject(uri, setRequestHeader(requestType, mediaType),
					responseClass);
			return response;

		} catch (Exception e) {
			logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
					LoggerFileConstant.APPLICATIONID.toString(), e.getMessage() + ExceptionUtils.getStackTrace(e));

			throw new ApisResourceAccessException("Exception occurred while accessing " + uri, e);
		}
	}

	/**
	 * Patch api.
	 *
	 * @param <T>
	 *            the generic type
	 * @param uri
	 *            the uri
	 * @param requestType
	 *            the request type
	 * @param responseClass
	 *            the response class
	 * @return the t
	 */
	@SuppressWarnings("unchecked")
	public <T> T patchApi(String uri, MediaType mediaType, Object requestType, Class<?> responseClass)
			throws ApisResourceAccessException {

		T result = null;
		try {
			logger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
					LoggerFileConstant.APPLICATIONID.toString(), uri);
			result = (T) residentRestTemplate.patchForObject(uri, setRequestHeader(requestType, mediaType),
					responseClass);

		} catch (Exception e) {

			logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
					LoggerFileConstant.APPLICATIONID.toString(), e.getMessage() + ExceptionUtils.getStackTrace(e));

			throw new ApisResourceAccessException("Exception occurred while accessing " + uri, e);
		}
		return result;
	}

	public <T> T patchApi(String uri, Object requestType, Class<?> responseClass) throws Exception {
		return patchApi(uri, null, requestType, responseClass);
	}

	/**
	 * Put api.
	 *
	 * @param <T>
	 *            the generic type
	 * @param uri
	 *            the uri
	 * @param requestType
	 *            the request type
	 * @param responseClass
	 *            the response class
	 * @param mediaType
	 * @return the t
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("unchecked")
	public <T> T putApi(String uri, Object requestType, Class<?> responseClass, MediaType mediaType)
			throws ApisResourceAccessException {

		T result = null;
		ResponseEntity<T> response = null;
		try {
			logger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
					LoggerFileConstant.APPLICATIONID.toString(), uri);

			response = (ResponseEntity<T>) residentRestTemplate.exchange(uri, HttpMethod.PUT,
					setRequestHeader(requestType.toString(), mediaType), responseClass);
			result = response.getBody();
		} catch (Exception e) {

			logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
					LoggerFileConstant.APPLICATIONID.toString(), e.getMessage() + ExceptionUtils.getStackTrace(e));

			throw new ApisResourceAccessException("Exception occured while accessing " + uri, e);
		}
		return result;
	}

	/**
	 * this method sets token to header of the request
	 *
	 * @param requestType
	 * @param mediaType
	 * @return HttpEntity<Object>
	 */
	@SuppressWarnings("unchecked")
	private HttpEntity<Object> setRequestHeader(Object requestType, MediaType mediaType) {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("Authorization", "futureProof");
		if (mediaType != null) {
			headers.add("Content-Type", mediaType.toString());
		}
		if (requestType != null) {
			try {
				HttpEntity<Object> httpEntity = (HttpEntity<Object>) requestType;
				HttpHeaders httpHeader = httpEntity.getHeaders();
				for (String key : httpHeader.keySet()) {
					if (!(headers.containsKey("Content-Type") && Objects.equals(key, "Content-Type")))
						headers.add(key, httpHeader.get(key).get(0));
				}
				return new HttpEntity<>(httpEntity.getBody(), headers);
			} catch (ClassCastException e) {
				return new HttpEntity<>(requestType, headers);
			}
		} else
			return new HttpEntity<>(headers);
	}

}
