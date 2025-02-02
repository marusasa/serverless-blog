package ssg.serverlessblog.gae.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.HarmCategory;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.cloud.vertexai.generativeai.ResponseStream;

import ssg.serverlessblog.daobase.SettingLogic;
import ssg.serverlessblog.documentref.SettingDoc;
import ssg.serverlessblog.interfaces.AiDaoInt;
import ssg.serverlessblog.util.CloudDocument;

public class AiDao implements AiDaoInt{
	
	@Override
	public String generateAiSummary(String content) throws Exception {
		final var result = new StringBuilder();
		final Optional<CloudDocument> setting = SettingLogic.getSetting();
		try (VertexAI vertexAi = new VertexAI(setting.get().getString(SettingDoc.field_gae_ai_project_id), 
			setting.get().getString(SettingDoc.field_gae_ai_location));) {
		
			final var text1 = content;
			final var textsi_1 = 
					"""
					You are a research bot, tasked with helping college students research quicker. 
					Your job is to summarize the texts submitted to you.
					Be sure to:
					* keep your summaries under 160 characters
					* present it so people will want to read the full text
					* focus on the main points of the text
					* keep it condense and to the point
					* do not hallucinate
					""";

			final GenerationConfig generationConfig = GenerationConfig.newBuilder().setMaxOutputTokens(1024)
					.setTemperature(2F).setTopP(0.95F).build();
			final List<SafetySetting> safetySettings = Arrays.asList(
					SafetySetting.newBuilder().setCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH)
							.setThreshold(SafetySetting.HarmBlockThreshold.OFF).build(),
					SafetySetting.newBuilder().setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
							.setThreshold(SafetySetting.HarmBlockThreshold.OFF).build(),
					SafetySetting.newBuilder().setCategory(HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
							.setThreshold(SafetySetting.HarmBlockThreshold.OFF).build(),
					SafetySetting.newBuilder().setCategory(HarmCategory.HARM_CATEGORY_HARASSMENT)
							.setThreshold(SafetySetting.HarmBlockThreshold.OFF).build());
			final var systemInstruction = ContentMaker.fromMultiModalData(textsi_1);
			final GenerativeModel model = new GenerativeModel.Builder().setModelName("gemini-1.5-flash-002")
					.setVertexAi(vertexAi).setGenerationConfig(generationConfig).setSafetySettings(safetySettings)
					.setSystemInstruction(systemInstruction).build();

			
			final var cnt = ContentMaker.fromMultiModalData(text1);
			final ResponseStream<GenerateContentResponse> responseStream = model.generateContentStream(cnt);

			responseStream.stream().forEach(t -> {
				t.getCandidatesList().forEach(c -> {
					result.append(c.getContent().getParts(0).getText());
				});
			});
		}
		return result.toString();
	}
	
	@Override
	public String generateAiGrammarCheck(final String prompt, final String content) throws Exception {
		String output = "";
		final Optional<CloudDocument> setting = SettingLogic.getSetting();
		try (VertexAI vertexAi = new VertexAI(setting.get().getString(SettingDoc.field_gae_ai_project_id), 
				setting.get().getString(SettingDoc.field_gae_ai_location));) {
			
			final var completePrompt = "With the following text, " + prompt + "\n\nText: " + content; 

			final GenerationConfig generationConfig = GenerationConfig.newBuilder().setMaxOutputTokens(1024)
					.setTemperature(2F).setTopP(0.95F).build();
			final List<SafetySetting> safetySettings = Arrays.asList(
					SafetySetting.newBuilder().setCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH)
							.setThreshold(SafetySetting.HarmBlockThreshold.OFF).build(),
					SafetySetting.newBuilder().setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
							.setThreshold(SafetySetting.HarmBlockThreshold.OFF).build(),
					SafetySetting.newBuilder().setCategory(HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
							.setThreshold(SafetySetting.HarmBlockThreshold.OFF).build(),
					SafetySetting.newBuilder().setCategory(HarmCategory.HARM_CATEGORY_HARASSMENT)
							.setThreshold(SafetySetting.HarmBlockThreshold.OFF).build());
			final GenerativeModel model = new GenerativeModel.Builder().setModelName("gemini-1.5-flash-002")
					.setVertexAi(vertexAi).setGenerationConfig(generationConfig).setSafetySettings(safetySettings)
					.build();

			GenerateContentResponse response = model.generateContent(completePrompt);
		    output = ResponseHandler.getText(response);
		}
		return output;
	}
}
