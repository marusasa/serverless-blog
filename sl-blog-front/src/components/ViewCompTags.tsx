import PostTag from "./PostTag";
import { useState, useEffect } from 'react';
import Loading from "../m/components/Loading";

function ViewCompTags() {
	

	const [tagsHtml, setTagsHtml] = useState('');
	const [loaded, setLoaded] = useState(false);
	
	useEffect(() => {
		fetch('/tags')
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					const tagItems = data.tags.map((tag, index) =>
						<PostTag tagName={tag.name} tagId={tag.tagId} index={index}/>
					);
					setTagsHtml(tagItems);
					setLoaded(true);
				} else {
					alert('Post not loaded...');
				}
			})
			.catch((err) => {
				console.log(err.message);
				alert('Failed to load data.');
			});
	}, []);
	
	return (
		<>
		<div className="shadow-lg bg-teal-100 text-left	p-3 mb-5">
			<p className="text-center mb-2">Tags</p>
			<Loading loaded={loaded}/>			
			<div className={'flex flex-wrap ' + (loaded ? 'visible' : 'invisible')}>
				{tagsHtml}
			</div>
		</div>

		</>
	)
}

export default ViewCompTags