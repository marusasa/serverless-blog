

function FullScreenEditorHelp({idName}:{idName:string}) {
		
	return (
		<>
			<dialog id={idName} className="modal ">
				<div className="modal-box w-[48rem] max-w-[48rem]">
					<form method="dialog">
						<button className="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
					</form>
					<h3 className="font-bold text-lg">About Full-Screen Editor</h3>									
					<table className="my-4 helpContent">
						<tr>
							<td className="align-top font-bold text-nowrap pr-2">Content Formatting:</td>
							<td className="pb-2">'Markdown' content formatting is supported. This system 
							supports <a href="https://commonmark.org/help/" target="_blank">CommonMark</a> tags
							to format your content. Right hand side panel can show the formatted preview of 
							the content.</td>
						</tr>
						<tr>
							<td className="align-top font-bold text-nowrap pr-2">Image Manager:</td>
							<td className="pb-2">
								You can upload images to use within the article. Once uploaded, the image will be listed within the Image Manager dialog. 
								Click "Copy URL Code" to obtain the markdown code and paste it within the editor to use it. 
							</td>
						</tr>
						<tr>
							<td className="align-top font-bold text-nowrap pr-2">Preview Mode:</td>
							<td className="pb-2">
								This mode shows the formatted content. Supported 'Markdown' tags will be displayed with the appropriate format applied.
							</td>
						</tr>
						<tr>
							<td className="align-top font-bold text-nowrap pr-2">AI Mode:</td>
							<td className="pb-2">AI Mode can be used to ask an AI to check and make grammar suggestions + more. (example: Ask for a title suggestion.) 
								Click "?" button within the AI mode to learn more.</td>
						</tr>
					</table>
				</div>
			</dialog>
		</>
	)
}

export default FullScreenEditorHelp