/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.contrib.llm;

import javax.ws.rs.*;

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;
import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;
import com.xpn.xwiki.objects.BaseObject;

import liquibase.pro.packaged.I;

import org.xwiki.stability.Unstable;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.Response;

import org.xwiki.component.manager.ComponentManager;

import org.xwiki.component.annotation.Component;
import org.xwiki.rest.XWikiRestException;
import org.xwiki.rest.XWikiRestComponent;
import org.xwiki.rest.internal.resources.pages.ModifiablePageResource;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.xwiki.contrib.llm.GPTAPIConfigProvider;
import org.xwiki.contrib.llm.internal.DefaultGPTAPIConfigProvider;

@Component
@Named("org.xwiki.contrib.llm.GPTRestAPI")
@Path("/v1")
@Unstable
@Singleton
public class GPTRestAPI extends ModifiablePageResource implements XWikiRestComponent {

    @Inject
    @Named("context")
    protected ComponentManager componentManager;
    @Inject
    protected Logger logger;

    @Inject
    private GPTAPI gptApi;

    @GET
    public Response get() {
        try {
            String message = "test";
            return Response.status(Response.Status.OK)
                    .entity(message)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            String errorMessage = "An error occurred: " + e.getMessage();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorMessage)
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @POST
    @Path("/chat/completions")
    @Consumes("application/json")
    public Response getContents(Map<String, Object> data) throws XWikiRestException {
        try {
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                logger.info("key: " + entry.getKey() + "; value: " + entry.getValue());
            }
            boolean isMapEmpty = data.get("text") == null || data.get("modelType") == null || data.get("model") == null
                    || data.get("prompt") == null;
            if (isMapEmpty) {
                logger.info("Invalid error data");
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input data.").build();
            }
            String modelInfoString = (String) data.get("modelType") + "/" + data.get("model");
            logger.info(modelInfoString);
            String model = "";
            String modelType = "";
            if (modelInfoString.indexOf("/") != -1) {
                String[] modelInfo = modelInfoString.split("/");
                modelType = modelInfo[0];
                model = modelInfo[1];
                logger.info("model : ", model);
                logger.info("modelType : ", modelType);
            } else {
                model = modelInfoString;
                modelType = "openai";
            }
            logger.info("modelType after evaluation :", modelType);
            logger.info("Received text: " + data.get("text"));
            logger.info("Received mode: " + data.get("stream"));
            boolean isStreaming = (Objects.equals(data.get("stream").toString(), "streamMode"));
            logger.info("is streaming : " + isStreaming);
            logger.info("config : " + modelType);
            GPTAPIConfig config = gptApi.getConfig(modelType);
            if (config == null) {
                throw new Exception(
                        "There is no configuration available for this model, please be sure that your configuration exist and is valid.");
            }

            // Create an instance of HttpClient.
            HttpClient client = new HttpClient();

            String url = config.getURL() + "chat/completions";
            // Create a method instance.
            logger.info("Calling url: " + url);
            PostMethod post = new PostMethod(url);

            // Set headers
            post.setRequestHeader("Content-Type", "application/json");
            post.setRequestHeader("Accept", "application/json");

            if (config.getToken() != "") {
                post.setRequestHeader("Authorization", "Bearer " + config.getToken());
            }

            JSONArray messagesArray = new JSONArray();
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", data.get("prompt").toString());
            messagesArray.put(systemMessage);

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", data.get("text").toString());
            messagesArray.put(userMessage);

            // Construct the JSON input string
            JSONObject jsonInput = new JSONObject();
            jsonInput.put("model", model);
            if (isStreaming)
                jsonInput.put("stream", isStreaming);
            jsonInput.put("messages", messagesArray);
            String jsonInputString = jsonInput.toString();
            logger.info("Sending: " + jsonInputString);

            StringRequestEntity requestEntity = new StringRequestEntity(
                    jsonInputString,
                    "application/json",
                    "UTF-8");

            post.setRequestEntity(requestEntity);

            // Execute the method.
            int statusCode = client.executeMethod(post);
            if (statusCode != HttpStatus.SC_OK) {
                logger.error("Method failed: " + post.getStatusLine());
                // throw new XWikiRestException(post.getStatusLine().toString() +
                // post.getStatusText(), null);
            }

            if (!isStreaming) {
                // Read the response body.
                byte[] responseBody = post.getResponseBody();

                // Deal with the response.
                // Use caution: ensure correct character encoding and is not binary data
                logger.info("response body" + new String(responseBody));

                // Return the response as a JSON string
                return Response.ok(responseBody, MediaType.APPLICATION_JSON).build();
            } else {
                // Read the response body.
                InputStream responseBody = post.getResponseBodyAsStream();
                StreamingOutput stream = new StreamingOutput() {
                    final BufferedReader reader = new BufferedReader(
                            new InputStreamReader(responseBody, StandardCharsets.UTF_8));
                    String line;

                    @Override
                    public void write(OutputStream outputStream) throws IOException {
                        logger.info("Writing in output Stream..");
                        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
                        boolean isErrSent = false;
                        boolean stop = false;
                        while ((line = reader.readLine()) != null && !stop) {
                            // Write each line to the output
                            logger.info("stream response line: " + line);
                            writer.write(line);
                            writer.flush();
                        }
                    }
                };
                return Response.ok(stream, MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            logger.error("Error processing request: " + e);
            JSONObject builder = new JSONObject();
            JSONObject root = new JSONObject();
            root.put("error", "An error occured. " + e.getMessage());
            builder.put("", root);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(builder.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @POST
    @Path("/models")
    @Consumes("application/json")
    public Response getModels() throws XWikiRestException {
        Map<String, GPTAPIConfig> configMap;
        try {
            configMap = gptApi.getConfigs();
        } catch (GPTAPIException e) {
            logger.error("Error in getModels REST method: ", e);
            configMap = new HashMap<>();
        }
        JSONArray finalResponse = new JSONArray();
        try {
            if (configMap.isEmpty())
                throw new Exception("The configurations object is empty.");
            for (Map.Entry<String, GPTAPIConfig> entry : configMap.entrySet()) {
                try {
                    HttpClient client = new HttpClient();
                    String url = entry.getValue().getURL() + "models";
                    logger.info("calling url : " + url);
                    GetMethod get = new GetMethod(url);
                    get.setRequestHeader("Content-Type", "application/json");
                    get.setRequestHeader("Accept", "application/json");
                    get.setRequestHeader("Authorization", "Bearer " + entry.getValue().getToken());
                    // setting the timeouts
                    get.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 10000);
                    client.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
                    int statusCode = client.executeMethod(get);
                    if (statusCode != HttpStatus.SC_OK) {
                        logger.error("Method failed: " + get.getStatusLine());
                        throw new XWikiRestException(get.getStatusLine().toString() + get.getStatusText(), null);
                    }
                    byte[] responseBody = get.getResponseBody();
                    get.releaseConnection();
                    logger.info("response body" + new String(responseBody));
                    JSONObject responseBodyJson = new JSONObject(new String(responseBody, StandardCharsets.UTF_8));
                    responseBodyJson.put("prefix", entry.getValue().getName().toLowerCase());
                    responseBodyJson.put("filter", entry.getValue().getConfigModels());
                    finalResponse.put(responseBodyJson);
                } catch (Exception e) {
                    logger.error("An error occured on one of the requested URI: ", e);
                }
            }
            byte[] finalResponseBytes = finalResponse.toString().getBytes(StandardCharsets.UTF_8);
            return Response.ok(finalResponseBytes, MediaType.APPLICATION_JSON).build();

        } catch (Exception e) {
            logger.error("Error processing request: " + e);
            JSONObject builder = new JSONObject();
            builder.put("", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(builder.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}
