function ViewCompTextBox({title, text}:{title:string, text:string}) {
	
	
	return (
		<>
		<div className="shadow-lg bg-teal-100 text-left	p-3 mb-5">
			<p className="text-center mb-2">{title}</p>
			<p className="whitespace-pre-wrap">{text}</p>
		</div>

		</>
	)
}

export default ViewCompTextBox