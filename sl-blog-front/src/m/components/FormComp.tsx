

export function FormTitle({text}:{text:string}) {
		
	return (
		<h2 className="text-2xl mb-4">{text}</h2>	
	)
}

export function SubmitButton({text, inProcess}:{text:string, inProcess:boolean}) {
		
	return (
		<button className={(inProcess?'btn-disabled':'') + " btn btn-primary mr-3 btn-sm"} type="submit">
		  <span className={(inProcess? '':'hidden ') + " loading loading-spinner"}></span>
		  {text}
		</button>
	)
}