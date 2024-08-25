package com.github.son_daehyeon.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ComciganRequest {

	private static final OkHttpClient client = new OkHttpClient();
	private static final Gson gson = new Gson();

	public static Optional<JsonObject> request(String subUrl) {
		Request request = new Request.Builder()
				.url("http://comci.net:4082/"+subUrl)
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (response.body() == null)
				return Optional.empty();

			String responseBody = response.body()
					.string()
					.replaceAll("\u0000", "");

			JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);

			return Optional.of(jsonObject);
		} catch (Exception ignored) {}

		return Optional.empty();
	}
}
