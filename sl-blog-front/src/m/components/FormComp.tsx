
export function FormTitle({text}:{text:string}) {
		
	return (
		<h2 className="text-2xl mb-4">{text}</h2>	
	)
}

export function SubmitButton({text, inProcess, callback, classes}:{text:string, 
		inProcess:boolean, callback: (e: React.FormEvent) => void, classes:string}) {
	
	
	return (
		<button className={(inProcess?'btn-disabled ':' ') + classes +" mr-3 btn"} onClick={callback}>
		  <span className={(inProcess? '':'hidden ') + " loading loading-spinner"}></span>
		  {text}
		</button>
	)
}