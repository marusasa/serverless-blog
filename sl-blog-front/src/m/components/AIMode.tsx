import { useState,useEffect } from 'react';
import {SubmitButton} from "../components/FormComp";

export function AIMode({article,className}:{article:string,className: string}) {
	

	const [useText, setUseText] = useState('whole');
	const [prompt, setPrompt] = useState('');
	const [aiSuggestion, setAiSuggestion] = useState('');
	const [inAskAi,setInAskAi] = useState(false);
	
	useEffect(() => {
		setPrompt(`Make grammar suggestion with your result.
The input text is in markdown format. Return plain text without markdown code.
Ignore section between \`\`\` and \`\`\`.`);
		}, []);
	
	const askAI = async () => {
		setInAskAi(true);
		let text = '';
		if(useText == 'selection'){
			if(window.getSelection() == null){
				alert('No selection found.');
				return;
			}
			text = window.getSelection().toString();
		}else{
			text = article;
		}
		
		await fetch('/mng/articles/ai-grammar', {
			method: 'POST',
			body: JSON.stringify({
				prompt: prompt, content: text
			}),
			headers: {
				'Content-type': 'application/json; charset=UTF-8',
			},
		})
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					setAiSuggestion(data.content);					
				} else {
					alert(JSON.stringify(data.messages));
				}
			})
			.catch((err) => {
				alert('Failed.');
				console.log(err.message);
			})
			.finally(() => {
				setInAskAi(false);
			});
	};		
	
	return (
		<div className={"h-full overflow-auto mx-6 " + className}>
			<table className="w-full">
				<tr>
					<td>Use:</td>
					<td>
						<select className="select select-bordered w-full max-w-xs" value={useText}
								onChange={(e) => setUseText(e.target.value)} >
							<option selected value='whole'>Whole Article</option>
							<option value='selection'>Selection</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>Prompt:</td>
					<td>
						<textarea className="textarea textarea-bordered w-full" rows={5} value={prompt}
								onChange={(e) => setPrompt(e.target.value)}>
						</textarea>
					</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<SubmitButton text="Ask AI" inProcess={inAskAi} callback={askAI} classes="btn-sm btn-secondary"/>
						<button className="btn btn-sm" onClick={()=>document.getElementById('dialog_ask_ai_help').showModal()}>?</button>
					</td>
				</tr>
			</table>
			<div>Suggestion:</div>
			<div className="whitespace-pre-wrap py-3">{aiSuggestion}</div>
			<dialog id="dialog_ask_ai_help" className="modal">
				<div className="modal-box">
					<form method="dialog">
						<button className="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
					</form>
					<h3 className="font-bold text-lg">About Ask AI</h3>					
					<table className="my-4">
						<tr>
							<td className="align-top font-bold">Use:</td>
							<td>'Whole Article' - the system will use the whole article.<br/>
								'Selection' - the system will use your selected text within the article.
							</td>
						</tr>
						<tr>
							<td className="align-top font-bold">Prompt:</td>
							<td>The system will use the following prompt when asking AI:<br/><br/>"With the following text, " + [your prompt] + <br/>"Text: " + [content to use].</td>
						</tr>
					</table>					
				</div>
			</dialog>
		</div>
	)
}

export default AIMode