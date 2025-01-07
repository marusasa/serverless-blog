import { Link } from 'react-router-dom';
import {convertToAllowedChars} from '../util/LinkUtil';

function PostTag({ tagName, tagId, index }: { tagName: string, tagId: string, index: number }) {
	
	const link = tagId + '/' + convertToAllowedChars(tagName);
	return (
		<>
			<div className="mb-2">
				<span className="badge badge-lg mr-3 shadow-md bg-stone-50" >
					<Link  to={"/tag/" + link} reloadDocument >{tagName}</Link >
				</span>
			</div>
		</>
	)
	
	
}

export default PostTag