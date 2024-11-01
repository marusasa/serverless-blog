function ViewCompTextBox({title, text}:{title:string, text:string}) {
	
	const replaceMarkdownLinks = (markdownText:string) => {
	    const regex = /\[([^\]]+)\]\(([^)]+)\)/g;
		//return markdownText.replace(regex, '<a href="$2" target="_blank">$1</a>');
		
		const parts = markdownText.split(regex);

		let skip = false;
	    return parts.map((part, index) => {
	        // Check if the part matches the link format
	        if (index % 3 === 1 && index + 1 < parts.length) {
	            const linkText = part;
	            const linkUrl = parts[index + 1];
				skip = true;
	            return <a key={index} href={linkUrl} target="_blank" className="link link-accent">{linkText}</a>;
	        }
			if(skip){
				skip = false;
				return "";
			}else{
	        	return part; // Return plain text parts as they are
			}
	    });
	};
	
	return (
		<>
		<div className="shadow-lg bg-teal-100 text-left	p-3 mb-5">
			<p className="text-center mb-2">{title}</p>
			<p className="whitespace-pre-wrap">{replaceMarkdownLinks(text)}</p>
		</div>

		</>
	)
}

export default ViewCompTextBox