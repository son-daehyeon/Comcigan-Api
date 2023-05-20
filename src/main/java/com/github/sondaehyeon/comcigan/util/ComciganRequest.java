package com.github.sondaehyeon.comcigan.util;

import java.io.IOException;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ComciganRequest {

	private static final OkHttpClient client = new OkHttpClient();
	private static final Gson gson = new Gson();

	public static Optional<JsonObject> request(String subUrl) throws IOException {
		Request request = new Request.Builder()
				.url("http://comci.net:4082/"+subUrl)
				.build();

		Response response = client.newCall(request).execute();
		if (response.body() == null)
			return Optional.empty();

		String responseBody = response.body()
				.string()
				.replaceAll("\u0000", "");

		JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);

		return Optional.of(jsonObject);
	}
}
