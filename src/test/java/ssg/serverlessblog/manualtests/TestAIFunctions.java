package ssg.serverlessblog.manualtests;

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
import com.google.cloud.vertexai.generativeai.ResponseStream;
import com.google.cloud.vertexai.generativeai.ResponseHandler;

import ssg.serverlessblog.documentref.ArticleDoc;
import ssg.serverlessblog.documentref.SettingDoc;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.CloudDocument;

public class TestAIFunctions {

	public static void main(String[] args) {
		try {
//			String result = generateAiSummary("Hello, my name is Bob. I am a male. Age 45.");
			String result = generateAiGrammarCheck(sampleText2);
			System.out.println(result);
		}catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public static String generateAiGrammarCheck(final String text) throws Exception {
		final var result = new StringBuilder();
		String output = "";
		final Optional<CloudDocument> setting = Env.settingDao.getSetting();
		try (VertexAI vertexAi = new VertexAI(setting.get().getString(SettingDoc.field_gae_ai_project_id), 
				setting.get().getString(SettingDoc.field_gae_ai_location));) {
			// get article
//			final Optional<CloudDocument> op = getArticleForManage(articleId);
//			if (op.isPresent()) {
//				final CloudDocument document = op.get();
//				final var text1 = text;
				final var textPrompt = 
						"""
						With the following text, make grammar suggestion with your result.
						The input text is in markdown format. Return plain text without markdown code.
						Ignore section between ``` and ```.
						
						Text: """ + text;

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
//				final var systemInstruction = ContentMaker.fromMultiModalData(textsi_1);
				final GenerativeModel model = new GenerativeModel.Builder().setModelName("gemini-1.5-flash-002")
						.setVertexAi(vertexAi).setGenerationConfig(generationConfig).setSafetySettings(safetySettings)
						.build();

				GenerateContentResponse response = model.generateContent(textPrompt);
			    output = ResponseHandler.getText(response);
				

//			}
		}
		return output;
	}
	
	public static String generateAiSummary(final String text) throws Exception {
		final var result = new StringBuilder();
		final Optional<CloudDocument> setting = Env.settingDao.getSetting();
		try (VertexAI vertexAi = new VertexAI(setting.get().getString(SettingDoc.field_gae_ai_project_id), 
				setting.get().getString(SettingDoc.field_gae_ai_location));) {
			// get article
//			final Optional<CloudDocument> op = getArticleForManage(articleId);
//			if (op.isPresent()) {
//				final CloudDocument document = op.get();
				final var text1 = text;
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

				
				final var content = ContentMaker.fromMultiModalData(text1);
				final ResponseStream<GenerateContentResponse> responseStream = model.generateContentStream(content);

				responseStream.stream().forEach(t -> {
					t.getCandidatesList().forEach(c -> {
						result.append(c.getContent().getParts(0).getText());
					});
				});
//			}
		}
		return result.toString();
	}
	
	private static String sampleText2 = """
			In part 3 of the analytics implementation article, I will discuss how I implemented the "Monthly Daily Visits View" for the analytics data. "Page Engagement" view will be covered in the next article.
			""";
	private static String sampleText = """
			In part 3 of the analytics implementation article, I will discuss how I implemented the "Monthly Daily Visits View" for the analytics data. "Page Engagement" view will be covered in the next article.

			Here are links to the previous posts:
			
			[Part 1 - Data Collection](/post/Dev-Note-Implementing-Analytics-Part-1---Data-Collection_QFVdGvz0a6F86feQLARV)
			
			[Part 2 - Data Processing](/post/Dev-Note-Implementing-Analytics-Part-2---Data-Processing_mORKuRARcVxQPJLFGe45)
			
			In the part 2 article, I explained the process of creating specific datasets designed for the two views. All the data needed for each view is already present in these two data tables. The idea is to simply format this data into a JSON format as required by the front-end chart library.
			
			![Analytics-view-01.png](https://storage.cloud.google.com/serverless-blog-sasagu-com/ARTICLES/MckfzHIinSNgwU9UAgpw/Analytics-view-01.png)
			
			**Data example of daily site visits:**
			![2024-11-27 daily visits data example.png](https://storage.cloud.google.com/serverless-blog-sasagu-com/ARTICLES/MckfzHIinSNgwU9UAgpw/2024-11-27%20daily%20visits%20data%20example.png)
			
			## Monthly daily visits view
			
			This view will have a line chart showing 2 lines. The chart will represent a monthly view of 1) all the page hits, and 2) count of users actually reading the content.
			
			After doing some research, I decided to use [chart.js](https://www.chartjs.org/). It is not specifically designed for React, but with the use of React's `useRef()`, it seemed like the integration is not too hard. I saw a lot of support on the web as well. End result with sample data looks like this:
			
			![2024-11-27 Daily Visits View.png](https://storage.cloud.google.com/serverless-blog-sasagu-com/ARTICLES/MckfzHIinSNgwU9UAgpw/2024-11-27%20Daily%20Visits%20View.png)
			
			### Implementing chart.js in React
			
			The full source code can be found at [here](https://github.com/marusasa/serverless-blog/blob/master/sl-blog-front/src/m/routes/AnalyticsDailyVisits.tsx).
			
			Installation is simple with `npm install chart.js`
			
			Import the component with `import Chart from 'chart.js/auto'`.
			Next, create an HTML `<canvas/>` element. Use React's `useRef` hook to create a reference to it:
			
			```
			const chartRef = useRef(null);
			...
			<canvas ref={chartRef}/>
			```
			
			Create a `Chart` object from the chart.js library to manipulate the chart. The type is 'line'. Use the ['2d' context](https://developer.mozilla.org/en-US/docs/Web/API/HTMLCanvasElement/getContext) of the '<canvas>' element. Other options are listed below. Note that `labels` and `data` arrays are initially empty. These will be populated with the data returned from the service created for this view.
			```
			const myChart = new Chart(chartRef.current.getContext("2d"), {
						type:"line",
						data: {
							labels: [],
							datasets: [{
								label: 'Actual Views',
								data: [],
								fill: false,
								borderColor: 'rgb(75, 192, 192)',
								tension: 0.1
							}, {
								label: 'Total Views',
								data: [],
								fill: false,
								borderColor: 'rgb(75, 192, 50)',
								tension: 0.1
							},]
						}
					});
			``` 
			The [Chart.js documentation](https://www.chartjs.org/docs/latest/general/data-structures.html), describes different ways to provide data to display a chart. This program utilizes the 'Primitive[]' method. The 'data' and 'labels' arrays will contain simple data structures, as shown in the following example:
			```
			datasets: [{
			      data: [200, 190,108],
			    },{
			      data: [38, 21,40],
			    }]
			labels: ['1', '2','3']
			```
			The 'labels' array represents the X-axis and shows the dates for the month being displayed (the view always displays a month).
			The 'data' array represents the Y-axis and shows the view count.
			
			I won't delve into the details, but the backend service operates as follows. Since the data has already been processed and prepared to match the view requirements, the logic is straightforward. **The backend service returns the labels and data in the format expected by the chart.js library. The program can then directly utilize this data within the chart.js component.**
			```
			AnalyticsController.java
			
			public static void getDailyVisits(Context ctx) {
					ResultAnalyticsDailyVisits result = new ResultAnalyticsDailyVisits();
					try {
						//path parameters
						final String y = ctx.pathParam("year");
						final String m = ctx.pathParam("month");
						final int year = Integer.parseInt(y);
						final int month = Integer.parseInt(m);
									
						//validate
						if(month < 1 || month > 12) {
							result.getMessages().add("Month '%s' is invalid.".formatted(month));				
						}
						
						
						if(result.getMessages().size() == 0) {		
							//create place holder in result.
							final List<Long> actualList = new ArrayList<>();
							final List<Long> allList = new ArrayList<>();
							final List<CloudDocument> list = Env.analyticsDao.getDailyVisits(year, month);
							list.forEach(doc -> {
								actualList.add(doc.getLong(DailyVisitsDoc.field_count_actual));					
								allList.add(doc.getLong(DailyVisitsDoc.field_count_all));					
								final long date = doc.getLong(DailyVisitsDoc.field_date);					
								result.getLabels().add(Long.toString(date%100));	//last 2 digits are date
							});
							//build result
							final Datasets datasetActual = new Datasets.Builder().label("Count Actual").data(actualList).build();
							final Datasets datasetAll = new Datasets.Builder().label("Count All").data(allList).build();
							result.getDatasets().add(datasetActual);
							result.getDatasets().add(datasetAll);
							result.setResult(AppConst.RESULT_SUCCESS);
						}
					}catch(Exception e) {
						logger.error("Error generating analytics data.",e);
						result.getMessages().add("Error retrieving data.");
					}
					ctx.json(result);	
			    }	
			```
			ResultAnalyticsDailyVisits.java class includes an inner 'record' class named 'Datasets'.
			```
			public class ResultAnalyticsDailyVisits extends ResultBase {
				private List<String> labels = new ArrayList<>();
				private List<Datasets> datasets = new ArrayList<>();	
				public record Datasets(String label, List<Long> data) {
					public static class Builder {
			           ...        
					}
				}
				public List<String> getLabels() {
					return labels;
				}
				public List<Datasets> getDatasets() {
					return datasets;
				}	
			}
			```
			When the service is invoked, the following JSON response is returned:
			```
			{
			    "messages": [],
			    "result": "success",
			    "labels": ["1","2","3", ... "30", "31"],
			    "datasets": [
			        {
			            "label": "Count Actual",
			            "data": [35,22,39, ... 75,66]
			        },
			        {
			            "label": "Count All",
			            "data": [199,189,107, ... 163,122]
			        }
			    ]
			}
			```
			Upon retrieving the data, the program seamlessly integrates the returned data into the chart.js component:
			```
			if (data.result == 'success') {
				theChart.data.labels = data.labels;
			    theChart.data.datasets[0].data = data.datasets[0].data;
				theChart.data.datasets[1].data = data.datasets[1].data;
				theChart.update();
				if(data.datasets[0].data.length == 0){
					alert("No data found.");
				}
			}
			```
			`theChart.update();` will update the chart in the page. This is called when the month selection is changed too.
			
			As mentioned, the result with sample data looks like this:
			
			![2024-11-27 Daily Visits View.png](https://storage.cloud.google.com/serverless-blog-sasagu-com/ARTICLES/MckfzHIinSNgwU9UAgpw/2024-11-27%20Daily%20Visits%20View.png)
			
			It's a simple line chart. Visually the result looks good. I think the integration is straightforward without any quirky code. I'm pleased with the result.
			
			Next article will cover "Page Engagement" view, which will use a different library.


			""";

}
