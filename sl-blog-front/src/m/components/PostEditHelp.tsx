

function PostEditHelp({idName}:{idName:string}) {
		
	return (
		<>
			<dialog id={idName} className="modal ">
				<div className="modal-box w-[48rem] max-w-[48rem]">
					<form method="dialog">
						<button className="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
					</form>
					<h3 className="font-bold text-lg">About Edit Post</h3>									
					<table className="my-4 helpContent">
						<tr>
							<td className="align-top font-bold text-nowrap pr-2">Title:</td>
							<td className="pb-2">Title is also used to generate the url of the post.</td>
						</tr>
						<tr>
							<td className="align-top font-bold text-nowrap pr-2">AI Summary:</td>
							<td className="pb-2">
								Blog viewer has an option to view this AI Summary before reading your post. <br/>
								<span className="font-semibold">View AI Summary</span> - view the current AI Summary.<br/>
								<span className="font-semibold">Generate AI Summary</span> - AI will analize your post content and generate an AI summary. 
								AI summary is not automatically generated. 
								Whenever you update your content, click this button to re-generate a summary.<br/>
								<span className="font-semibold">Clear AI Summary</span> - Clear the generated AI summary. 
							</td>
						</tr>
						<tr>
							<td className="align-top font-bold text-nowrap pr-2">Tags:</td>
							<td className="pb-2">
								Posts can be filtered by tags. Create a 'Tag' from the 'Tags' menu. Multiple tags can be specified per post.
							</td>
						</tr>
						<tr>
							<td className="align-top font-bold text-nowrap pr-2">Article:</td>
							<td className="pb-2">Click on "Full-Screen Editor with AI" to open a full-screen article editor with Markdown preview.</td>
						</tr>
					</table>
				</div>
			</dialog>
		</>
	)
}

export default PostEditHelp